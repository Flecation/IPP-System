package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.databaseConnection;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Models.users;
import IPPSystem.Utils.linkButton;
import IPPSystem.Utils.session;
import IPPSystem.Utils.switchPage;
import IPPSystem.Utils.utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.*;

/**
 * allProjectDashboardController
 *
 * FXML handlers:
 *  - onAction="#clickYearAction"       (Year ComboBox)
 *  - onAction="#clickProjectSelect"    (Project ComboBox)
 *
 * IMPORTANT (matches your zip schema):
 *  - startDate is in assignProjectDetails (NOT in assignProjects)
 *  - table names are: assignProjects, assignProjectDetails, dailyReports, dailyReportTasks, dailyReportLabors, projectStatus
 */
public class allProjectDashboardController implements loadPaneAware {

    // ======= FXML: combos =======
    @FXML private ComboBox<String> comboProjectList;   // Overall / Current Project
    @FXML private ComboBox<Integer> comboYearList;     // 2020..current

    // ======= FXML: cards =======
    @FXML private Label lblProject;
    @FXML private Label lblRevenue;
    @FXML private Label lblExpense;
    @FXML private Label lblWorkers;

    // ======= FXML: charts =======
    @FXML private PieChart pieProjectStatus;
    @FXML private PieChart pieSchedule;
    @FXML private LineChart<String, Number> linechartMonthlyCost;
    @FXML private LineChart<String, Number> linechartActiveProject;

    // ======= FXML: top3 intensive =======
    @FXML private Label progressCostTyp1, progressCostTyp2, progressCostTyp3;
    @FXML private Label progressCost1, progressCost2, progressCost3;
    @FXML private ProgressBar progressbarTyp1, progressbarTyp2, progressbarTyp3;

    private static final String OVERALL_LABEL = "Overall Project";

    // supervisor: current project mapping (instanceName -> assignProjectId)
    private final LinkedHashMap<String, Integer> currentInProgressMap = new LinkedHashMap<>();

    private final users loginUser = session.getInstance().getUser();

    private StackPane loadPane;
    private sideBarPaneController nav;

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
        if (this.loadPane != null) {
            this.nav = (sideBarPaneController) this.loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        }
    }

    private sideBarPaneController requireNav() {
        if (nav != null) return nav;
        if (loadPane != null) nav = (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        return nav;
    }


    @FXML
    public void initialize() {
        setupYearCombo();        // fixed: selects latest year with data
        setupProjectCombo();     // fixed: uses correct table names
        loadDashboardAsync();
    }

    // ======= FXML handlers =======

    @FXML
    public void clickYearAction(ActionEvent event) {
        loadDashboardAsync();
    }

    @FXML
    public void clickProjectSelect(ActionEvent event) {
        if (comboProjectList == null) return;

        Integer supervisorId = supervisorIdOrNull();
        if (supervisorId == null) return; // only supervisor can open current project dashboard

        String selected = comboProjectList.getValue();
        if (selected == null) return;

        sideBarPaneController n = requireNav();
        if (n == null) return;

        if (OVERALL_LABEL.equals(selected)) {
            // stay / reload this page
            n.openInnerView("allProjectDashboard.fxml", ctrl -> {});
            linkButton.getInstance().setTabButtonName("All Dashboard");
            return;
        }

        Integer projectId = currentInProgressMap.get(selected);
        if (projectId == null) return;

        n.openInnerView("currentProjectDashboard.fxml", ctrl -> {
            if (ctrl instanceof currentProjectDashboardController c) {
                c.setAssignProject(projectId, selected); // <-- add this setter in currentProjectDashboardController
            }
        });

        linkButton.getInstance().setTabButtonName(selected + " Dashboard");
    }

    // ======= setup =======

    private void setupYearCombo() {
        if (comboYearList == null) return;

        int start = 2023;
        int current = Year.now().getValue();

        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int y = start; y <= current; y++) years.add(y);
        comboYearList.setItems(years);

        // FIX: select latest year that exists in DB (so dashboard shows data)
        comboYearList.setValue(detectLatestYearWithData(current));
    }

    private int detectLatestYearWithData(int fallbackYear) {
        try (Connection con = databaseConnection.getConnection()) {

            // 1) dailyReports year
            try (PreparedStatement ps = con.prepareStatement("SELECT MAX(YEAR(reportDate)) AS y FROM dailyReports");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int y = rs.getInt("y");
                    if (!rs.wasNull() && y > 0) return y;
                }
            }

            // 2) assignProjectDetails.startDate year
            try (PreparedStatement ps = con.prepareStatement("SELECT MAX(YEAR(startDate)) AS y FROM assignProjectDetails WHERE startDate IS NOT NULL");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int y = rs.getInt("y");
                    if (!rs.wasNull() && y > 0) return y;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return fallbackYear;
    }

    private void setupProjectCombo() {
        if (comboProjectList == null) return;

        comboProjectList.getItems().clear();

        if (isManager()) {
            comboProjectList.setDisable(true);
            comboProjectList.setPromptText("All Projects");
            return;
        }

        Integer supervisorId = supervisorIdOrNull();
        if (supervisorId == null) {
            comboProjectList.setDisable(true);
            return;
        }

        comboProjectList.setDisable(false);
        loadSupervisorComboAsync(supervisorId);
    }

    private void loadSupervisorComboAsync(int supervisorId) {
        Task<LinkedHashMap<String, Integer>> t = new Task<>() {
            @Override
            protected LinkedHashMap<String, Integer> call() {
                LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

                // FIX: correct table names + safer matching for in-progress
                String sql =
                        "SELECT ap.assignProjectId, ap.projectInstanceName " +
                                "FROM assignProjects ap " +
                                "JOIN projectStatus ps ON ps.projectStatusId = ap.projectStatus " +
                                "WHERE ap.supervisorId = ? " +
                                "  AND (LOWER(ps.projectStatusName) LIKE '%progress%' OR LOWER(ps.projectStatusName) LIKE '%ongo%') " +
                                "ORDER BY ap.assignProjectId DESC LIMIT 1";

                try (Connection con = databaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, supervisorId);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("assignProjectId");
                            String name = rs.getString("projectInstanceName");
                            if (name == null || name.isBlank()) name = "Project #" + id;
                            map.put(name, id);
                        }
                    }
                } catch (SQLException ex) {
                    // Fallback: if your status ids are numeric and you use 2 = inProgress
                    try (Connection con = databaseConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(
                                 "SELECT ap.assignProjectId, ap.projectInstanceName " +
                                         "FROM assignProjects ap " +
                                         "WHERE ap.supervisorId = ? AND ap.projectStatus = 2 " +
                                         "ORDER BY ap.assignProjectId DESC LIMIT 1"
                         )) {
                        ps.setInt(1, supervisorId);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                int id = rs.getInt("assignProjectId");
                                String name = rs.getString("projectInstanceName");
                                if (name == null || name.isBlank()) name = "Project #" + id;
                                map.put(name, id);
                            }
                        }
                    } catch (SQLException ex2) {
                        ex2.printStackTrace();
                    }
                }

                return map;
            }
        };

        t.setOnSucceeded(e -> {
            currentInProgressMap.clear();
            currentInProgressMap.putAll(t.getValue());

            comboProjectList.getItems().clear();

            if (currentInProgressMap.isEmpty()) {
                // your requested behavior: if no current -> disable like manager
                comboProjectList.setDisable(true);
                comboProjectList.setPromptText("All Projects");
                return;
            }

            comboProjectList.setDisable(false);
            comboProjectList.getItems().add(OVERALL_LABEL);
            comboProjectList.getItems().addAll(currentInProgressMap.keySet());
            comboProjectList.getSelectionModel().select(OVERALL_LABEL);
        });

        t.setOnFailed(e -> {
            if (t.getException() != null) t.getException().printStackTrace();
            comboProjectList.setDisable(true);
        });

        Thread th = new Thread(t, "load-supervisor-current-project");
        th.setDaemon(true);
        th.start();
    }

    // ======= main load =======

    private void loadDashboardAsync() {
        final Integer supervisorId = supervisorIdOrNull(); // null => manager
        final int year = (comboYearList != null && comboYearList.getValue() != null)
                ? comboYearList.getValue()
                : Year.now().getValue();

        Task<DashboardData> t = new Task<>() {
            @Override
            protected DashboardData call() {
                return fetchDashboardData(supervisorId, year);
            }
        };

        t.setOnSucceeded(e -> applyDashboardData(t.getValue()));
        t.setOnFailed(e -> {
            if (t.getException() != null) t.getException().printStackTrace();
        });

        Thread th = new Thread(t, "load-allProjectDashboard");
        th.setDaemon(true);
        th.start();
    }

    // ======= data model =======

    private static class DashboardData {
        int projectCount;
        double revenue;
        double overhead;
        int workers;
        double expense;

        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        int scheduleOnTime;
        int scheduleDelay;

        List<TopCostRow> top3 = new ArrayList<>();

        Map<String, Double> monthlyCost = new LinkedHashMap<>();
        Map<String, Integer> activeProjects = new LinkedHashMap<>();
    }

    private static class TopCostRow {
        String projectName;
        double usedCost;
        double totalCost;

        TopCostRow(String projectName, double usedCost, double totalCost) {
            this.projectName = projectName;
            this.usedCost = usedCost;
            this.totalCost = totalCost;
        }
    }

    // ======= SQL fetching =======

    private DashboardData fetchDashboardData(Integer supervisorIdOrNull, int year) {
        DashboardData d = new DashboardData();

        try (Connection con = databaseConnection.getConnection()) {

            d.projectCount = queryProjectCount(con, supervisorIdOrNull, year);
            d.revenue      = queryRevenue(con, supervisorIdOrNull, year);
            d.expense = queryExpense(con, supervisorIdOrNull, year);
            d.workers      = queryWorkers(con, supervisorIdOrNull, year);

// If you still want overhead, you can ADD it, but overhead is not per-year in your DB.
// Best: show Expense = real daily cost only
            d.overhead = 0;
            d.statusCounts = queryStatusCounts(con, supervisorIdOrNull);

            // FIX: schedule counts use assignProjectDetails.startDate + year filter + CASE WHEN (no syntax error)
            int[] sch = queryScheduleCounts(con, supervisorIdOrNull, year);
            d.scheduleOnTime = sch[0];
            d.scheduleDelay = sch[1];

            d.top3 = queryTop3UsedCost(con, supervisorIdOrNull, year);
            d.monthlyCost = queryMonthlyCost(con, supervisorIdOrNull, year);
            d.activeProjects = queryActiveProjects(con, supervisorIdOrNull, year);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return d;
    }

    private int queryProjectCount(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        String sql =
                "SELECT COUNT(*) AS c " +
                        "FROM assignProjects ap " +
                        "JOIN assignProjectDetails apd ON apd.assignProjectId = ap.assignProjectId " +
                        "WHERE apd.assignProjectDetailId = ( " +
                        "   SELECT MAX(apd2.assignProjectDetailId) " +
                        "   FROM assignProjectDetails apd2 " +
                        "   WHERE apd2.assignProjectId = ap.assignProjectId " +
                        ") " +
                        "AND apd.startDate IS NOT NULL " +
                        "AND YEAR(apd.startDate) = ? " +
                        (supervisorIdOrNull == null ? "" : "AND ap.supervisorId = ? ");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("c") : 0;
            }
        }
    }

    private double queryOverhead(Connection con, Integer supervisorIdOrNull) throws SQLException {
        String sql = "SELECT IFNULL(SUM(ap.projectOverHeadCost),0) AS s FROM assignProjects ap " +
                (supervisorIdOrNull == null ? "" : "WHERE ap.supervisorId = ? ");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (supervisorIdOrNull != null) ps.setInt(1, supervisorIdOrNull);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("s") : 0.0;
            }
        }
    }

    private double queryRevenue(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        // revenue = total projectCost for projects that have any daily report in that year
        String sql =
                "SELECT IFNULL(SUM(apd.projectCost),0) AS revenue " +
                        "FROM assignProjects ap " +
                        "JOIN (" +
                        "   SELECT DISTINCT assignProjectId " +
                        "   FROM dailyReports " +
                        "   WHERE YEAR(reportDate) = ? " +
                        ") drp ON drp.assignProjectId = ap.assignProjectId " +
                        "JOIN assignProjectDetails apd ON apd.assignProjectId = ap.assignProjectId " +
                        "WHERE apd.assignProjectDetailId = (" +
                        "   SELECT MAX(apd2.assignProjectDetailId) " +
                        "   FROM assignProjectDetails apd2 " +
                        "   WHERE apd2.assignProjectId = ap.assignProjectId" +
                        ") " +
                        (supervisorIdOrNull == null ? "" : "AND ap.supervisorId = ? ");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("revenue") : 0.0;
            }
        }
    }


    private double queryExpense(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        String sql =
                "SELECT IFNULL(SUM(drt.dailyCost),0) AS expense " +
                        "FROM dailyReports dr " +
                        "JOIN dailyReportTasks drt ON drt.dailyReportId = dr.dailyReportId " +
                        "JOIN assignProjects ap ON ap.assignProjectId = dr.assignProjectId " +
                        "WHERE YEAR(dr.reportDate) = ? " +
                        (supervisorIdOrNull == null ? "" : "AND ap.supervisorId = ? ");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("expense") : 0.0;
            }
        }
    }

    private int queryWorkers(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        String sql =
                "SELECT COUNT(DISTINCT drl.laborId) AS c " +
                        "FROM dailyReports dr " +
                        "JOIN dailyReportLabors drl ON drl.dailyReportId = dr.dailyReportId " +
                        "JOIN assignProjects ap ON ap.assignProjectId = dr.assignProjectId " +
                        "WHERE YEAR(dr.reportDate) = ? " +
                        (supervisorIdOrNull == null ? "" : "AND ap.supervisorId = ? ");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("c") : 0;
            }
        }
    }

    private LinkedHashMap<String, Integer> queryStatusCounts(Connection con, Integer supervisorIdOrNull) throws SQLException {
        String sql =
                "SELECT ps.projectStatusName AS name, COUNT(*) AS c " +
                        "FROM assignProjects ap " +
                        "JOIN projectStatus ps ON ps.projectStatusId = ap.projectStatus " +
                        "WHERE 1=1 " +
                        (supervisorIdOrNull == null ? "" : " AND ap.supervisorId = ? ") +
                        "GROUP BY ps.projectStatusName";

        List<String> wanted = (supervisorIdOrNull == null)
                ? Arrays.asList("planning", "inprogress", "delay", "finished")
                : Arrays.asList("delay", "finished");

        LinkedHashMap<String, Integer> out = new LinkedHashMap<>();
        for (String w : wanted) out.put(w, 0);

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (supervisorIdOrNull != null) ps.setInt(1, supervisorIdOrNull);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int c = rs.getInt("c");
                    String key = normalizeStatus(name);
                    if (out.containsKey(key)) out.put(key, c);
                }
            }
        }

        return out;
    }

    /**
     * FIXED (your error happened here):
     * - uses CASE WHEN (no missing bracket)
     * - filters by YEAR(assignProjectDetails.startDate)
     * - startDate is in assignProjectDetails (your schema)
     */
    private int[] queryScheduleCounts(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {

        String sql =
                "SELECT " +
                        "  SUM(CASE WHEN LOWER(ps.projectStatusName) = 'delay' THEN 1 ELSE 0 END) AS delayedCount, " +
                        "  SUM(CASE WHEN LOWER(ps.projectStatusName) <> 'delay' " +
                        "            AND LOWER(ps.projectStatusName) <> 'cancel' THEN 1 ELSE 0 END) AS onTime " +
                        "FROM assignProjects ap " +
                        "JOIN assignProjectDetails apd ON apd.assignProjectId = ap.assignProjectId " +
                        "LEFT JOIN projectStatus ps ON ps.projectStatusId = ap.projectStatus " +
                        "WHERE apd.assignProjectDetailId = ( " +
                        "  SELECT MAX(apd2.assignProjectDetailId) " +
                        "  FROM assignProjectDetails apd2 " +
                        "  WHERE apd2.assignProjectId = ap.assignProjectId " +
                        ") " +
                        "AND apd.startDate IS NOT NULL " +
                        "AND YEAR(apd.startDate) = ? " +
                        (supervisorIdOrNull == null ? "" : "AND ap.supervisorId = ? ");



        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return new int[]{0, 0};
                int delayed = rs.getInt("delayedCount");
                int onTime  = rs.getInt("onTime");
                return new int[]{onTime, delayed};
            }
        }
    }

    private List<TopCostRow> queryTop3UsedCost(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        String sql =
                "SELECT ap.assignProjectId, ap.projectInstanceName, " +
                        "IFNULL(used.usedCost,0) AS usedCost, IFNULL(total.totalCost,0) AS totalCost " +
                        "FROM assignProjects ap " +
                        "LEFT JOIN ( " +
                        "   SELECT dr.assignProjectId, SUM(drt.dailyCost) AS usedCost " +
                        "   FROM dailyReports dr " +
                        "   JOIN dailyReportTasks drt ON drt.dailyReportId = dr.dailyReportId " +
                        "   WHERE YEAR(dr.reportDate) = ? " +
                        "   GROUP BY dr.assignProjectId " +
                        ") used ON used.assignProjectId = ap.assignProjectId " +
                        "LEFT JOIN ( " +
                        "   SELECT apd.assignProjectId, apd.projectCost AS totalCost " +
                        "   FROM assignProjectDetails apd " +
                        "   WHERE apd.assignProjectDetailId = ( " +
                        "      SELECT MAX(apd2.assignProjectDetailId) " +
                        "      FROM assignProjectDetails apd2 " +
                        "      WHERE apd2.assignProjectId = apd.assignProjectId " +
                        "   ) " +
                        ") total ON total.assignProjectId = ap.assignProjectId " +
                        (supervisorIdOrNull == null ? "" : "WHERE ap.supervisorId = ? ") +
                        "ORDER BY IFNULL(used.usedCost,0) DESC " +
                        "LIMIT 3";

        List<TopCostRow> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("projectInstanceName");
                    double usedCost = rs.getDouble("usedCost");
                    double totalCost = rs.getDouble("totalCost");
                    if (name == null || name.isBlank()) name = "Project #" + rs.getInt("assignProjectId");
                    list.add(new TopCostRow(name, usedCost, totalCost));
                }
            }
        }
        return list;
    }

    private LinkedHashMap<String, Double> queryMonthlyCost(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        String sql =
                "SELECT MONTH(dr.reportDate) AS m, IFNULL(SUM(drt.dailyCost),0) AS cost " +
                        "FROM dailyReports dr " +
                        "JOIN dailyReportTasks drt ON drt.dailyReportId = dr.dailyReportId " +
                        "JOIN assignProjects ap ON ap.assignProjectId = dr.assignProjectId " +
                        "WHERE YEAR(dr.reportDate) = ? " +
                        (supervisorIdOrNull == null ? "" : "AND ap.supervisorId = ? ") +
                        "GROUP BY MONTH(dr.reportDate) " +
                        "ORDER BY MONTH(dr.reportDate)";

        Map<Integer, Double> temp = new HashMap<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) temp.put(rs.getInt("m"), rs.getDouble("cost"));
            }
        }

        LinkedHashMap<String, Double> out = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) {
            String label = Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            out.put(label, temp.getOrDefault(m, 0.0));
        }
        return out;
    }

    private LinkedHashMap<String, Integer> queryActiveProjects(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        String sql =
                "SELECT MONTH(apd.startDate) AS m, COUNT(DISTINCT apd.assignProjectId) AS c " +
                        "FROM assignProjectDetails apd " +
                        "JOIN assignProjects ap ON ap.assignProjectId = apd.assignProjectId " +
                        "WHERE apd.assignProjectDetailId = ( " +
                        "  SELECT MAX(apd2.assignProjectDetailId) FROM assignProjectDetails apd2 " +
                        "  WHERE apd2.assignProjectId = apd.assignProjectId " +
                        ") " +
                        "AND apd.startDate IS NOT NULL AND YEAR(apd.startDate) = ? " +
                        (supervisorIdOrNull == null ? "" : "AND ap.supervisorId = ? ") +
                        "GROUP BY MONTH(apd.startDate) " +
                        "ORDER BY MONTH(apd.startDate)";

        Map<Integer, Integer> temp = new HashMap<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) temp.put(rs.getInt("m"), rs.getInt("c"));
            }
        }

        LinkedHashMap<String, Integer> out = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) {
            String label = Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            out.put(label, temp.getOrDefault(m, 0));
        }
        return out;
    }

    // ======= UI apply =======

    private void applyDashboardData(DashboardData d) {
        Platform.runLater(() -> {
            if (lblProject != null) lblProject.setText(String.valueOf(d.projectCount));
            if (lblRevenue != null) lblRevenue.setText(formatMoney(d.revenue));
            if (lblExpense != null) lblExpense.setText(formatMoney(d.expense)); // year-based expense
            if (lblWorkers != null) lblWorkers.setText(String.valueOf(d.workers));

            updatePieProjectStatus(d.statusCounts);
            updatePieSchedule(d.scheduleOnTime, d.scheduleDelay);
            updateTop3(d.top3);
            updateLineMonthlyCost(d.monthlyCost);
            updateLineActiveProjects(d.activeProjects);
        });
    }

    private void updatePieProjectStatus(Map<String, Integer> statusCounts) {
        if (pieProjectStatus == null) return;

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> e : statusCounts.entrySet()) {
            data.add(new PieChart.Data(toDisplayStatus(e.getKey()), e.getValue()));
        }
        pieProjectStatus.setData(data);

        Platform.runLater(() -> applyProjectStatusColors(pieProjectStatus));
    }

    private void applyProjectStatusColors(PieChart pie) {
        for (PieChart.Data d : pie.getData()) {
            Node n = d.getNode();
            if (n == null) continue;

            String key = normalizeStatus(d.getName());
            String color = switch (key) {
                case "planning" -> "#f59e0b";
                case "inprogress" -> "#38bdf8";
                case "delay" -> "#7c3aed";
                case "finished" -> "#22c55e";
                default -> "#94a3b8";
            };
            n.setStyle("-fx-pie-color: " + color + ";");
        }
    }

    private void updatePieSchedule(int onTime, int delay) {
        if (pieSchedule == null) return;

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        data.add(new PieChart.Data("On Time", onTime));
        data.add(new PieChart.Data("Delay", delay));
        pieSchedule.setData(data);

        Platform.runLater(() -> applyScheduleColors(pieSchedule));
    }

    private void applyScheduleColors(PieChart pie) {
        for (PieChart.Data d : pie.getData()) {
            Node n = d.getNode();
            if (n == null) continue;
            if ("On Time".equalsIgnoreCase(d.getName())) n.setStyle("-fx-pie-color: #22c55e;");
            else n.setStyle("-fx-pie-color: #7c3aed;");
        }
    }

    private void updateTop3(List<TopCostRow> top3) {
        top3.sort((a, b) -> Double.compare(b.usedCost, a.usedCost));
        applyTopRow(0, top3, progressCostTyp1, progressCost1, progressbarTyp1);
        applyTopRow(1, top3, progressCostTyp2, progressCost2, progressbarTyp2);
        applyTopRow(2, top3, progressCostTyp3, progressCost3, progressbarTyp3);
    }

    private void applyTopRow(int index, List<TopCostRow> top3, Label nameLbl, Label costLbl, ProgressBar bar) {
        if (nameLbl == null && costLbl == null && bar == null) return;

        if (index >= top3.size()) {
            if (nameLbl != null) nameLbl.setText("-");
            if (costLbl != null) costLbl.setText("-");
            if (bar != null) bar.setProgress(0);
            return;
        }

        TopCostRow r = top3.get(index);

        if (nameLbl != null) nameLbl.setText(r.projectName);
        if (costLbl != null) costLbl.setText(formatMoney(r.usedCost) + " / " + formatMoney(r.totalCost));
        if (bar != null) {
            double p = (r.totalCost <= 0) ? 0 : Math.min(1.0, r.usedCost / r.totalCost);
            bar.setProgress(p);
        }
    }

    private void updateLineMonthlyCost(Map<String, Double> monthlyCost) {
        if (linechartMonthlyCost == null) return;
        linechartMonthlyCost.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Cost");

        for (Map.Entry<String, Double> e : monthlyCost.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        linechartMonthlyCost.getData().add(series);
    }

    private void updateLineActiveProjects(Map<String, Integer> activeProjects) {
        if (linechartActiveProject == null) return;
        linechartActiveProject.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Active Projects");

        for (Map.Entry<String, Integer> e : activeProjects.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        linechartActiveProject.getData().add(series);
    }

    // ======= helpers =======

    private boolean isManager() {
        return loginUser != null && role.MANAGER.toString().equalsIgnoreCase(loginUser.getUserRole());
    }

    private Integer supervisorIdOrNull() {
        if (loginUser == null) return null;
        if (!role.SUPERVISOR.toString().equalsIgnoreCase(loginUser.getUserRole())) return null;
        return loginUser.getUserId();
    }

    private String formatMoney(double v) {
        return String.format(Locale.ENGLISH, "%,.0f", v);
    }

    private String normalizeStatus(String s) {
        if (s == null) return "";
        String t = s.trim().toLowerCase(Locale.ENGLISH);
        if (t.contains("progress")) return "inprogress";
        if (t.contains("plan")) return "planning";
        if (t.contains("finish")) return "finished";
        if (t.contains("delay")) return "delay";
        return t.replace(" ", "");
    }

    private String toDisplayStatus(String normalized) {
        String n = normalizeStatus(normalized);
        return switch (n) {
            case "planning" -> "Planning";
            case "inprogress" -> "In Progressing";
            case "delay" -> "Delay";
            case "finished" -> "Finished";
            default -> normalized;
        };
    }

    private StackPane findLoadPane(Node any) {
        if (any == null) return null;
        Node p = any;
        while (p != null) {
            if (p instanceof StackPane sp) return sp;
            p = p.getParent();
        }
        return null;
    }
}
