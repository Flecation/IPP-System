package IPPSystem.Controllers;

import IPPSystem.Constants.projectStatus;
import IPPSystem.Constants.role;
import IPPSystem.DAO.databaseConnection;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.sql.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class currentProjectDashboardController implements loadPaneAware {

    // ====== FXML (must match currentProjectDashboard.fxml) ======
    @FXML private Label projectName;
    @FXML private Label lblProgressStatus;
    @FXML private Label lbCurrentTask;

    @FXML private ComboBox<String> comboProjectList;
    @FXML private Label lbDate;

    @FXML private Label lbDaysRemaining;
    @FXML private Label lbEarnedValue;
    @FXML private Label lbTotalManHour;
    @FXML private Label lbActualCost;

    @FXML private Circle circleSPI;
    @FXML private Label lbSPIPercentage;
    @FXML private Label lbSPIValue;
    @FXML private Label lbSPIStatus;

    @FXML private Circle circleCPI;
    @FXML private Label lbCPIPercentages;
    @FXML private Label lbCPIValue;
    @FXML private Label lbCPIStatus;

    @FXML private LineChart<String, Number> lcMonthlyProjectPerformance;
    @FXML private AreaChart<String, Number> acWeeklyResourceUsage;

    // ====== loadPaneAware ======
    private StackPane loadPane;
    @Override public void setLoadPane(StackPane loadPane) { this.loadPane = loadPane; }

    // ====== state ======
    private Integer assignProjectId;
    private String projectInstanceName;

    private Integer currentAssignWorkItemId;
    private String currentWorkItemName;
    private String currentTaskName;

    // ====== format ======
    private final DecimalFormat moneyFmt = new DecimalFormat("#,##0.00");
    private final DateTimeFormatter uiDateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // supervisor only (as you requested)
        if (session.getInstance().getUser() == null ||
                !role.SUPERVISOR.toString().equalsIgnoreCase(session.getInstance().getUser().getUserRole())) {
            Platform.runLater(() -> utils.openFxml("allProjectDashboard.fxml", comboProjectList));
            return;
        }

        if (lbDate != null) lbDate.setText(LocalDate.now().format(uiDateFmt));

        initGauge(circleSPI);
        initGauge(circleCPI);
        clearAllUI();

        Task<Void> t = new Task<>() {
            @Override
            protected Void call() {
                loadCurrentInProgressProjectForSupervisor();
                loadCurrentInProgressWorkItemAndTask();
                return null;
            }
        };

        t.setOnSucceeded(e -> Platform.runLater(() -> {
            setupComboTwoItems();
            if (assignProjectId == null) {
                setNoProjectUI();
                return;
            }
            renderHeaderBase();
            refreshDashboardAll();
        }));

        t.setOnFailed(e -> {
            if (t.getException() != null) t.getException().printStackTrace();
            Platform.runLater(this::setNoProjectUI);
        });

        Thread th = new Thread(t, "load-current-project-dashboard");
        th.setDaemon(true);
        th.start();
    }

    // =========================================================
    // Combo behavior: only 2 items
    // =========================================================
    private void setupComboTwoItems() {
        if (comboProjectList == null) return;

        comboProjectList.getItems().clear();
        comboProjectList.getItems().add("Overall Project");

        if (projectInstanceName != null && !projectInstanceName.isBlank()) {
            comboProjectList.getItems().add(projectInstanceName);
            comboProjectList.getSelectionModel().select(projectInstanceName);
        } else {
            comboProjectList.getSelectionModel().select("Overall Project");
        }
    }

    @FXML
    void clickProjectSelect(ActionEvent event) {
        if (comboProjectList == null) return;

        String v = comboProjectList.getValue();
        if (v == null) return;

        if ("Overall Project".equals(v)) {
            utils.openFxml("allProjectDashboard.fxml", comboProjectList);
            return;
        }

        // current project selected: reload dashboard
        if (assignProjectId != null) refreshDashboardAll();
    }

    private void refreshDashboardAll() {
        loadProjectCardsAndStatus();        // daysRemaining, EV, AC, manhour, inTime/delay
        loadWorkItemCpiSpi();               // CPI/SPI from getWorkItemDashboard
        loadMonthlyPerformance();           // monthly EV/AC using calculateCpiSpi
        loadWeeklyResourceUsage();          // weekly cost (dailyReportTasks+dailyReportLabors)
    }

    // =========================================================
    // Load current in-progress project for THIS supervisor
    // =========================================================
    private void loadCurrentInProgressProjectForSupervisor() {
        assignProjectId = null;
        projectInstanceName = null;

        final int IN_PROGRESS = 2;
        final int DELAY = 3;

        String sql =
                "SELECT ap.assignProjectId, ap.projectInstanceName " +
                        "FROM assignProjects ap " +
                        "WHERE ap.supervisorId = ? AND ap.projectStatus IN (?, ?) " +
                        "ORDER BY ap.assignProjectId DESC " +
                        "LIMIT 1";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, session.getInstance().getUser().getUserId());
            ps.setInt(2, IN_PROGRESS);
            ps.setInt(3, DELAY);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assignProjectId = rs.getInt("assignProjectId");
                    projectInstanceName = rs.getString("projectInstanceName");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // =========================================================
    // Load current in-progress WorkItem + Task name
    // =========================================================
    private void loadCurrentInProgressWorkItemAndTask() {
        currentAssignWorkItemId = null;
        currentWorkItemName = null;
        currentTaskName = null;

        if (assignProjectId == null) return;

        final int IN_PROGRESS = 2;
        final int DELAY = 3;

        String sqlWorkItem =
                "SELECT aw.assignWorkItemId, wi.projectWorkItemName " +
                        "FROM assignWorkItems aw " +
                        "JOIN workItems wi ON wi.projectWorkItemId = aw.projectWorkItemId " +
                        "WHERE aw.assignProjectId = ? AND aw.workItemStatus IN (?, ?) " +
                        "ORDER BY aw.assignWorkItemId ASC " +
                        "LIMIT 1";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlWorkItem)) {

            ps.setInt(1, assignProjectId);
            ps.setInt(2, IN_PROGRESS);
            ps.setInt(3, DELAY);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentAssignWorkItemId = rs.getInt("assignWorkItemId");
                    currentWorkItemName = rs.getString("projectWorkItemName");
                }
            }

            if (currentAssignWorkItemId != null) {
                String sqlTask =
                        "SELECT t.projectTaskName " +
                                "FROM assignTasks at " +
                                "JOIN tasks t ON t.projectTaskId = at.projectTaskId " +
                                "WHERE at.assignWorkItemId = ? AND at.isCancel = 0 " +
                                "  AND at.taskStatus IN (?, ?) " +
                                "ORDER BY at.assignTaskId ASC " +
                                "LIMIT 1";

                try (PreparedStatement ps2 = con.prepareStatement(sqlTask)) {
                    ps2.setInt(1, currentAssignWorkItemId);
                    ps2.setInt(2, IN_PROGRESS);
                    ps2.setInt(3, DELAY);

                    try (ResultSet rs2 = ps2.executeQuery()) {
                        if (rs2.next()) currentTaskName = rs2.getString("projectTaskName");
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // =========================================================
    // Header base
    // =========================================================
    private void renderHeaderBase() {
        if (projectName != null) projectName.setText(projectInstanceName == null ? "" : projectInstanceName);

        String taskText = "-";
        if (currentWorkItemName != null && currentTaskName != null) {
            taskText = currentWorkItemName + " - " + currentTaskName;
        } else if (currentWorkItemName != null) {
            taskText = currentWorkItemName;
        }
        if (lbCurrentTask != null) lbCurrentTask.setText(taskText);
    }

    // =========================================================
    // Project cards (EV/AC) and Status (In Time / Delay)
    // Uses your procedure: getProjectDashboard(assignProjectId, asOfDate)
    // =========================================================
    private void loadProjectCardsAndStatus() {
        if (assignProjectId == null) {
            setNoProjectUI();
            return;
        }

        Task<Void> t = new Task<>() {
            double ev = 0;
            double ac = 0;
            LocalDate baselineStart = null;
            LocalDate baselineEnd = null;

            double manHour = 0;
            boolean delay = false;

            @Override
            protected Void call() {

                // (1) Get dashboard numbers from your procedure
                String call = "{CALL getProjectDashboard(?, ?)}";
                try (Connection con = databaseConnection.getConnection();
                     CallableStatement cs = con.prepareCall(call)) {

                    cs.setInt(1, assignProjectId);
                    cs.setDate(2, Date.valueOf(LocalDate.now()));

                    try (ResultSet rs = cs.executeQuery()) {
                        if (rs.next()) {
                            ev = rs.getDouble("EV");
                            ac = rs.getDouble("AC");

                            Date bs = rs.getDate("baselineStart");
                            Date be = rs.getDate("baselineEnd");
                            if (bs != null) baselineStart = bs.toLocalDate();
                            if (be != null) baselineEnd = be.toLocalDate();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // (2) Total man-hour from start to today (tasks hours + labors hours)
                String sqlMH =
                        "SELECT IFNULL(SUM(drt.workHours),0) + IFNULL(SUM(drl.workHours),0) AS totalHours " +
                                "FROM dailyReports dr " +
                                "LEFT JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId " +
                                "LEFT JOIN dailyReportLabors drl ON dr.dailyReportId = drl.dailyReportId " +
                                "WHERE dr.assignProjectId = ? AND dr.reportDate <= CURDATE()";

                try (Connection con = databaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sqlMH)) {

                    ps.setInt(1, assignProjectId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) manHour = rs.getDouble("totalHours");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // (3) Delay logic:
                // - if DB status is delay OR today > baselineEnd
                String sqlStatus =
                        "SELECT ap.projectStatus, ps.projectStatusName " +
                                "FROM assignProjects ap " +
                                "JOIN projectStatus ps ON ps.projectStatusId = ap.projectStatus " +
                                "WHERE ap.assignProjectId = ? LIMIT 1";

                try (Connection con = databaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sqlStatus)) {

                    ps.setInt(1, assignProjectId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString("projectStatusName");
                            projectStatus st = projectStatus.fromString(name);
                            delay = (st == projectStatus.DELAY);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                if (baselineEnd != null && LocalDate.now().isAfter(baselineEnd)) delay = true;

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (lbEarnedValue != null) lbEarnedValue.setText(moneyFmt.format(ev));
                    if (lbActualCost != null) lbActualCost.setText(moneyFmt.format(ac));
                    if (lbTotalManHour != null) lbTotalManHour.setText(moneyFmt.format(manHour));

                    if (baselineEnd != null) {
                        long remaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), baselineEnd);
                        if (lbDaysRemaining != null) lbDaysRemaining.setText(remaining + " Days");
                    } else {
                        if (lbDaysRemaining != null) lbDaysRemaining.setText("-");
                    }

                    // you asked:
                    // In Time - skyBlue, Delay - purple
                    if (lblProgressStatus != null) {
                        if (delay) {
                            lblProgressStatus.setText("Delay");
                            lblProgressStatus.setStyle("-fx-text-fill: #7E57C2;");
                        } else {
                            lblProgressStatus.setText("In Time");
                            lblProgressStatus.setStyle("-fx-text-fill: #4FC3F7;");
                        }
                    }
                });
            }
        };

        Thread th = new Thread(t, "load-project-cards-status");
        th.setDaemon(true);
        th.start();
    }

    // =========================================================
    // WorkItem CPI/SPI (current work item)
    // Uses your procedure: getWorkItemDashboard(assignWorkItemId, asOfDate)
    // =========================================================
    private void loadWorkItemCpiSpi() {
        if (currentAssignWorkItemId == null) {
            setGaugeNoData();
            return;
        }

        Task<Void> t = new Task<>() {
            Double cpi = null, spi = null;
            String cpiStatus = "No Data", spiStatus = "No Data";

            @Override
            protected Void call() {
                String call = "{CALL getWorkItemDashboard(?, ?)}";

                try (Connection con = databaseConnection.getConnection();
                     CallableStatement cs = con.prepareCall(call)) {

                    cs.setInt(1, currentAssignWorkItemId);
                    cs.setDate(2, Date.valueOf(LocalDate.now()));

                    try (ResultSet rs = cs.executeQuery()) {
                        if (rs.next()) {
                            double cpiVal = rs.getDouble("CPI");
                            double spiVal = rs.getDouble("SPI");

                            // CPI/SPI can be NULL in procedure; rs.getDouble -> 0.0 so check wasNull
                            cpi = rs.getObject("CPI") == null ? null : cpiVal;
                            spi = rs.getObject("SPI") == null ? null : spiVal;
                        }
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // status text (same rule as your procedure calculateCpiSpi)
                cpiStatus = statusTextForCpi(cpi);
                spiStatus = statusTextForSpi(spi);

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (lbCPIValue != null) lbCPIValue.setText(cpi == null ? "-" : String.format("%.2f", cpi));
                    if (lbCPIStatus != null) lbCPIStatus.setText(cpiStatus);
                    if (lbCPIPercentages != null) lbCPIPercentages.setText(cpi == null ? "-" : String.format("%.0f%%", cpi * 100));

                    if (lbSPIValue != null) lbSPIValue.setText(spi == null ? "-" : String.format("%.2f", spi));
                    if (lbSPIStatus != null) lbSPIStatus.setText(spiStatus);
                    if (lbSPIPercentage != null) lbSPIPercentage.setText(spi == null ? "-" : String.format("%.0f%%", spi * 100));

                    setGauge(circleCPI, cpi);
                    setGauge(circleSPI, spi);
                });
            }
        };

        Thread th = new Thread(t, "load-workitem-cpi-spi");
        th.setDaemon(true);
        th.start();
    }

    private String statusTextForCpi(Double cpi) {
        if (cpi == null) return "No Data";
        if (cpi >= 1.05) return "Under Budget";
        if (cpi >= 0.95) return "On Budget";
        return "Over Budget";
    }

    private String statusTextForSpi(Double spi) {
        if (spi == null) return "No Data";
        if (spi >= 1.05) return "Ahead of Schedule";
        if (spi >= 0.95) return "On Schedule";
        return "Behind Schedule";
    }

    // =========================================================
    // Monthly chart (project start -> current month): EV & AC
    // Uses your procedure calculateCpiSpi(assignProjectId, asOfDate)
    // =========================================================
    private void loadMonthlyPerformance() {
        if (assignProjectId == null || lcMonthlyProjectPerformance == null) return;

        Task<Void> t = new Task<>() {
            final List<XYChart.Data<String, Number>> evPoints = new ArrayList<>();
            final List<XYChart.Data<String, Number>> acPoints = new ArrayList<>();

            @Override
            protected Void call() {
                LocalDate baselineStart = fetchBaselineStart();
                if (baselineStart == null) return null;

                LocalDate today = LocalDate.now();
                YearMonth ymStart = YearMonth.from(baselineStart);
                YearMonth ymEnd = YearMonth.from(today);

                YearMonth ym = ymStart;
                while (!ym.isAfter(ymEnd)) {
                    LocalDate asOf = ym.equals(ymEnd) ? today : ym.atEndOfMonth();

                    double ev = 0;
                    double ac = 0;

                    String call = "{CALL calculateCpiSpi(?, ?)}";
                    try (Connection con = databaseConnection.getConnection();
                         CallableStatement cs = con.prepareCall(call)) {

                        cs.setInt(1, assignProjectId);
                        cs.setDate(2, Date.valueOf(asOf));

                        try (ResultSet rs = cs.executeQuery()) {
                            if (rs.next()) {
                                ev = rs.getDouble("EV");
                                ac = rs.getDouble("AC");
                            }
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    String label = ym.toString(); // yyyy-MM
                    evPoints.add(new XYChart.Data<>(label, ev));
                    acPoints.add(new XYChart.Data<>(label, ac));

                    ym = ym.plusMonths(1);
                }

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    lcMonthlyProjectPerformance.getData().clear();

                    XYChart.Series<String, Number> sEv = new XYChart.Series<>();
                    sEv.setName("EV");
                    sEv.getData().addAll(evPoints);

                    XYChart.Series<String, Number> sAc = new XYChart.Series<>();
                    sAc.setName("AC");
                    sAc.getData().addAll(acPoints);

                    lcMonthlyProjectPerformance.getData().addAll(sEv, sAc);
                });
            }
        };

        Thread th = new Thread(t, "load-monthly-performance");
        th.setDaemon(true);
        th.start();
    }

    private LocalDate fetchBaselineStart() {
        if (assignProjectId == null) return null;

        // use getProjectDashboard because it returns baselineStart/baselineEnd correctly
        String call = "{CALL getProjectDashboard(?, ?)}";
        try (Connection con = databaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(call)) {

            cs.setInt(1, assignProjectId);
            cs.setDate(2, Date.valueOf(LocalDate.now()));

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Date d = rs.getDate("baselineStart");
                    return d == null ? null : d.toLocalDate();
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // =========================================================
    // Weekly resource usage (current week): dailyCost + dailyWage
    // =========================================================
    private void loadWeeklyResourceUsage() {
        if (assignProjectId == null || acWeeklyResourceUsage == null) return;

        Task<Void> t = new Task<>() {
            final List<XYChart.Data<String, Number>> points = new ArrayList<>();

            @Override
            protected Void call() {
                LocalDate today = LocalDate.now();
                LocalDate weekStart = today.with(DayOfWeek.MONDAY);
                LocalDate weekEnd = weekStart.plusDays(6);

                Map<LocalDate, Double> dayCost = new LinkedHashMap<>();
                for (int i = 0; i < 7; i++) dayCost.put(weekStart.plusDays(i), 0.0);

                String sql =
                        "SELECT dr.reportDate, " +
                                "       IFNULL(SUM(drt.dailyCost),0) + IFNULL(SUM(drl.dailyWage),0) AS cost " +
                                "FROM dailyReports dr " +
                                "LEFT JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId " +
                                "LEFT JOIN dailyReportLabors drl ON dr.dailyReportId = drl.dailyReportId " +
                                "WHERE dr.assignProjectId = ? " +
                                "  AND dr.reportDate BETWEEN ? AND ? " +
                                "GROUP BY dr.reportDate " +
                                "ORDER BY dr.reportDate";

                try (Connection con = databaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, assignProjectId);
                    ps.setDate(2, Date.valueOf(weekStart));
                    ps.setDate(3, Date.valueOf(weekEnd));

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Date d = rs.getDate("reportDate");
                            double c = rs.getDouble("cost");
                            if (d != null) dayCost.put(d.toLocalDate(), c);
                        }
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                for (Map.Entry<LocalDate, Double> e : dayCost.entrySet()) {
                    String label = e.getKey().getDayOfWeek().toString().substring(0, 3); // MON/TUE...
                    points.add(new XYChart.Data<>(label, e.getValue()));
                }

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    acWeeklyResourceUsage.getData().clear();
                    XYChart.Series<String, Number> s = new XYChart.Series<>();
                    s.setName("Weekly Cost");
                    s.getData().addAll(points);
                    acWeeklyResourceUsage.getData().add(s);
                });
            }
        };

        Thread th = new Thread(t, "load-weekly-resource-usage");
        th.setDaemon(true);
        th.start();
    }

    // =========================================================
    // UI helpers
    // =========================================================
    private void clearAllUI() {
        if (projectName != null) projectName.setText("");
        if (lblProgressStatus != null) { lblProgressStatus.setText(""); lblProgressStatus.setStyle(""); }
        if (lbCurrentTask != null) lbCurrentTask.setText("");

        if (lbDaysRemaining != null) lbDaysRemaining.setText("-");
        if (lbEarnedValue != null) lbEarnedValue.setText("-");
        if (lbTotalManHour != null) lbTotalManHour.setText("-");
        if (lbActualCost != null) lbActualCost.setText("-");

        setGaugeNoData();
        if (lcMonthlyProjectPerformance != null) lcMonthlyProjectPerformance.getData().clear();
        if (acWeeklyResourceUsage != null) acWeeklyResourceUsage.getData().clear();
    }

    private void setNoProjectUI() {
        if (projectName != null) projectName.setText("No Current Project");
        if (lblProgressStatus != null) { lblProgressStatus.setText("No Data"); lblProgressStatus.setStyle(""); }
        if (lbCurrentTask != null) lbCurrentTask.setText("-");

        if (lbDaysRemaining != null) lbDaysRemaining.setText("0 Days");
        if (lbEarnedValue != null) lbEarnedValue.setText("0.00");
        if (lbTotalManHour != null) lbTotalManHour.setText("0");
        if (lbActualCost != null) lbActualCost.setText("0.00");

        setGaugeNoData();
        if (lcMonthlyProjectPerformance != null) lcMonthlyProjectPerformance.getData().clear();
        if (acWeeklyResourceUsage != null) acWeeklyResourceUsage.getData().clear();
    }

    private void initGauge(Circle circle) {
        if (circle == null) return;
        double r = circle.getRadius();
        double c = 2 * Math.PI * r;
        circle.getStrokeDashArray().setAll(c);
        circle.setStrokeDashOffset(c);
    }

    private void setGaugeNoData() {
        if (lbCPIValue != null) lbCPIValue.setText("-");
        if (lbCPIStatus != null) lbCPIStatus.setText("No Data");
        if (lbCPIPercentages != null) lbCPIPercentages.setText("-");

        if (lbSPIValue != null) lbSPIValue.setText("-");
        if (lbSPIStatus != null) lbSPIStatus.setText("No Data");
        if (lbSPIPercentage != null) lbSPIPercentage.setText("-");

        setGauge(circleCPI, null);
        setGauge(circleSPI, null);
    }

    // Map CPI/SPI ratio to gauge percent:
    // clamp 0..2 => 0..100%
    private void setGauge(Circle circle, Double ratio) {
        if (circle == null) return;
        double r = circle.getRadius();
        double c = 2 * Math.PI * r;

        double pct;
        if (ratio == null || Double.isNaN(ratio) || Double.isInfinite(ratio)) {
            pct = 0;
        } else {
            pct = Math.max(0, Math.min(2.0, ratio)) / 2.0;
        }

        circle.setStrokeDashOffset(c * (1 - pct));
    }
}
