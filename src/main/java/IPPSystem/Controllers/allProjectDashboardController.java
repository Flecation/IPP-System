package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.databaseConnection;
import IPPSystem.Models.users;
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
import java.time.Year;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

/**
 * allProjectDashboardController
 *
 * Matches FXML handlers:
 *  - onAction="#clickYearAction"  (Year ComboBox)
 *  - onAction="#clickProjectSelect" (Project ComboBox)
 *
 * Role behavior:
 *  - MANAGER: shows all projects; project combo disabled
 *  - SUPERVISOR: shows his projects; project combo contains:
 *      1) "Overall Project"
 *      2) latest inProgress projectInstanceName (if exists) -> navigate to currentProjectDashboard.fxml
 *
 * Year behavior:
 *  - year list from 2020 -> current year, default current year
 *  - changing year refreshes dashboard (no navigation)
 *
 * Notes:
 *  - This controller assumes tables like:
 *      assignProjects(ap), assignProjectDetails(apd), projectStatus(ps),
 *      dailyReports(dr), dailyReportTasks(drt), dailyReportLabors(drl), labors(l)
 *  - If your column names differ, adjust the SQL in the marked sections.
 */
public class allProjectDashboardController {

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

    @FXML
    public void initialize() {
        setupYearCombo();
        setupProjectCombo();
        loadDashboardAsync();
    }

    // ======= FXML handlers =======

    /**
     * Called by FXML: onAction="#clickYearAction"
     * Refresh only (never navigate).
     */
    @FXML
    public void clickYearAction(ActionEvent event) {
        loadDashboardAsync();
    }

    /**
     * Called by FXML: onAction="#clickProjectSelect"
     * Supervisor: selecting current project navigates.
     * Manager: combo disabled, so this usually won't fire.
     */
    @FXML
    public void clickProjectSelect(ActionEvent event) {
        if (event != null && event.getSource() == comboYearList) {
            // Safety: if FXML mistakenly wires year to this handler
            loadDashboardAsync();
            return;
        }

        if (comboProjectList == null) return;

        Integer supervisorId = supervisorIdOrNull();
        if (supervisorId == null) {
            // manager or unknown -> no navigation
            loadDashboardAsync();
            return;
        }

        String selected = comboProjectList.getValue();
        if (selected == null) return;

        if (OVERALL_LABEL.equals(selected)) {
            loadDashboardAsync();
            return;
        }

        Integer projectId = currentInProgressMap.get(selected);
        if (projectId == null) {
            // no mapping -> just refresh
            loadDashboardAsync();
            return;
        }

        StackPane loadPane = findLoadPane(comboProjectList);
        if (loadPane == null) {
            utils.openFxml("currentProjectDashboard.fxml", comboProjectList);
            return;
        }

        loadPane.getProperties().put("CURRENT_PROJECT_ID", projectId);
        switchPage.getInstance(loadPane).openFxml("currentProjectDashboard.fxml");
    }

    // ======= setup =======

    private void setupYearCombo() {
        if (comboYearList == null) return;

        int start = 2020;
        int current = Year.now().getValue();

        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int y = start; y <= current; y++) years.add(y);

        comboYearList.setItems(years);
        comboYearList.setValue(current);
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
        // will populate async
        loadSupervisorComboAsync(supervisorId);
    }

    private void loadSupervisorComboAsync(int supervisorId) {
        Task<LinkedHashMap<String, Integer>> t = new Task<>() {
            @Override
            protected LinkedHashMap<String, Integer> call() {
                LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

                // If your in-progress status id is different, adjust here.
                // Prefer using projectStatusName = 'inProgress' if available.
                String sql =
                        "SELECT ap.assignProjectId, ap.projectInstanceName " +
                                "FROM assignprojects ap " +
                                "JOIN projectstatus ps ON ps.projectStatusId = ap.projectStatus " +
                                "WHERE ap.supervisorId = ? AND ps.projectStatusName IN ('inProgress', 'InProgress', 'In Progress', 'inProgressing', 'inProgressing ') " +
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
                    // fallback: try by numeric status=2 (common)
                    try (Connection con = databaseConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(
                                 "SELECT ap.assignProjectId, ap.projectInstanceName " +
                                         "FROM assignprojects ap " +
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
                comboProjectList.setDisable(true); // as you requested: if no current -> disable like manager
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
        final Integer year = (comboYearList != null && comboYearList.getValue() != null) ? comboYearList.getValue() : Year.now().getValue();

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

        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        int scheduleOnTime;
        int scheduleDelay;

        List<TopCostRow> top3 = new ArrayList<>();

        // month name -> value
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

            // 1) Projects count
            d.projectCount = queryProjectCount(con, supervisorIdOrNull);

            // 2) Revenue (sum latest baseline projectCost) + overhead
            d.revenue = queryRevenue(con, supervisorIdOrNull);
            d.overhead = queryOverhead(con, supervisorIdOrNull);

            // 3) Workers
            d.workers = queryWorkers(con, supervisorIdOrNull, year);

            // 4) Status pie counts
            d.statusCounts = queryStatusCounts(con, supervisorIdOrNull);

            // 5) Schedule pie (on-time vs delay)
            int[] sch = queryScheduleCounts(con, supervisorIdOrNull);
            d.scheduleOnTime = sch[0];
            d.scheduleDelay = sch[1];

            // 6) Top3 cost used (DESC biggest first)
            d.top3 = queryTop3UsedCost(con, supervisorIdOrNull, year);

            // 7) Monthly cost line (sum dailyCost per month for year)
            d.monthlyCost = queryMonthlyCost(con, supervisorIdOrNull, year);

            // 8) Active projects line (projects count per month by startDate for year)
            d.activeProjects = queryActiveProjects(con, supervisorIdOrNull, year);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return d;
    }

    private int queryProjectCount(Connection con, Integer supervisorIdOrNull) throws SQLException {
        String sql = "SELECT COUNT(*) AS c FROM assignprojects ap " +
                (supervisorIdOrNull == null ? "" : "WHERE ap.supervisorId = ? ");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (supervisorIdOrNull != null) ps.setInt(1, supervisorIdOrNull);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("c") : 0;
            }
        }
    }

    private double queryOverhead(Connection con, Integer supervisorIdOrNull) throws SQLException {
        String sql = "SELECT IFNULL(SUM(ap.projectOverHeadCost),0) AS s FROM assignprojects ap " +
                (supervisorIdOrNull == null ? "" : "WHERE ap.supervisorId = ? ");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (supervisorIdOrNull != null) ps.setInt(1, supervisorIdOrNull);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("s") : 0.0;
            }
        }
    }

    /**
     * Revenue = sum of latest baseline projectCost per assignProject.
     * Uses latest assignProjectDetails row for each project (by max assignProjectDetailId).
     */
    private double queryRevenue(Connection con, Integer supervisorIdOrNull) throws SQLException {
        String sql =
                "SELECT IFNULL(SUM(apd.projectCost),0) AS revenue " +
                        "FROM assignprojects ap " +
                        "JOIN assignprojectdetails apd ON apd.assignProjectId = ap.assignProjectId " +
                        "WHERE apd.assignProjectDetailId = ( " +
                        "  SELECT MAX(apd2.assignProjectDetailId) FROM assignprojectdetails apd2 " +
                        "  WHERE apd2.assignProjectId = ap.assignProjectId " +
                        ") " +
                        (supervisorIdOrNull == null ? "" : " AND ap.supervisorId = ? ");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (supervisorIdOrNull != null) ps.setInt(1, supervisorIdOrNull);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("revenue") : 0.0;
            }
        }
    }

    private int queryWorkers(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        if (supervisorIdOrNull == null) {
            // manager: active labors
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS c FROM labors WHERE isActive = TRUE");
                 ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("c") : 0;
            }
        }

        // supervisor: distinct workers in daily reports for selected year
        String sql =
                "SELECT COUNT(DISTINCT drl.laborId) AS c " +
                        "FROM dailyreports dr " +
                        "JOIN dailyreportlabors drl ON dr.dailyReportId = drl.dailyReportId " +
                        "JOIN assignprojects ap ON ap.assignProjectId = dr.assignProjectId " +
                        "WHERE ap.supervisorId = ? AND YEAR(dr.reportDate) = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supervisorIdOrNull);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("c") : 0;
            }
        }
    }

    private LinkedHashMap<String, Integer> queryStatusCounts(Connection con, Integer supervisorIdOrNull) throws SQLException {
        // manager: planning,inProgress,delay,finished
        // supervisor: delay,finished only
        String base =
                "SELECT ps.projectStatusName AS name, COUNT(*) AS c " +
                        "FROM assignprojects ap " +
                        "JOIN projectstatus ps ON ps.projectStatusId = ap.projectStatus " +
                        "WHERE 1=1 ";

        List<String> wanted;
        if (supervisorIdOrNull == null) {
            wanted = Arrays.asList("planning", "inProgress", "delay", "finished");
        } else {
            wanted = Arrays.asList("delay", "finished");
            base += " AND ap.supervisorId = ? ";
        }

        base += " GROUP BY ps.projectStatusName";

        LinkedHashMap<String, Integer> out = new LinkedHashMap<>();
        for (String w : wanted) out.put(w, 0);

        try (PreparedStatement ps = con.prepareStatement(base)) {
            if (supervisorIdOrNull != null) ps.setInt(1, supervisorIdOrNull);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int c = rs.getInt("c");
                    if (name == null) continue;
                    String key = normalizeStatus(name);
                    if (out.containsKey(key)) out.put(key, c);
                }
            }
        }

        return out;
    }


    private int[] queryScheduleCounts(Connection con, Integer supervisorIdOrNull) throws SQLException {
        /*
         * Your schema does NOT have actualEndDate/targetEndDate on assignprojects.
         * So schedule performance is derived from projectStatus:
         *   - Delay  : projectstatus.projectStatusName = 'delay'
         *   - On Time: all non-cancel projects that are NOT 'delay'
         *
         * Cancelled projects are excluded from both buckets.
         */
        String sql =
                "SELECT " +
                        "SUM(IF(ps.projectStatusName = 'delay', 1, 0)) AS delayed, " +
                        "SUM(IF(ps.projectStatusName <> 'delay' AND ps.projectStatusName <> 'cancel', 1, 0)) AS onTime " +
                        "FROM assignprojects ap " +
                        "JOIN projectstatus ps ON ps.projectStatusId = ap.projectStatus " +
                        (supervisorIdOrNull == null ? "" : "WHERE ap.supervisorId = ? ");

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (supervisorIdOrNull != null) ps.setInt(1, supervisorIdOrNull);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return new int[]{0, 0};
                return new int[]{rs.getInt("onTime"), rs.getInt("delayed")};
            }
        }
    }

    private List<TopCostRow> queryTop3UsedCost(Connection con, Integer supervisorIdOrNull, int year) throws SQLException {
        // usedCost = SUM(drt.dailyCost) per assignProject within selected year
        // totalCost = latest apd.projectCost
        // ORDER BY usedCost DESC LIMIT 3

        String sql =
                "SELECT ap.assignProjectId, ap.projectInstanceName, " +
                        "IFNULL(used.usedCost,0) AS usedCost, IFNULL(total.totalCost,0) AS totalCost " +
                        "FROM assignprojects ap " +
                        "LEFT JOIN ( " +
                        "   SELECT dr.assignProjectId, SUM(drt.dailyCost) AS usedCost " +
                        "   FROM dailyreports dr " +
                        "   JOIN dailyreporttasks drt ON drt.dailyReportId = dr.dailyReportId " +
                        "   WHERE YEAR(dr.reportDate) = ? " +
                        "   GROUP BY dr.assignProjectId " +
                        ") used ON used.assignProjectId = ap.assignProjectId " +
                        "LEFT JOIN ( " +
                        "   SELECT apd.assignProjectId, apd.projectCost AS totalCost " +
                        "   FROM assignprojectdetails apd " +
                        "   WHERE apd.assignProjectDetailId = ( " +
                        "      SELECT MAX(apd2.assignProjectDetailId) " +
                        "      FROM assignprojectdetails apd2 " +
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
        // Sum daily costs per month, across all projects (filtered by supervisor)
        String sql =
                "SELECT MONTH(dr.reportDate) AS m, IFNULL(SUM(drt.dailyCost),0) AS cost " +
                        "FROM dailyreports dr " +
                        "JOIN dailyreporttasks drt ON drt.dailyReportId = dr.dailyReportId " +
                        "JOIN assignprojects ap ON ap.assignProjectId = dr.assignProjectId " +
                        "WHERE YEAR(dr.reportDate) = ? " +
                        (supervisorIdOrNull == null ? "" : "AND ap.supervisorId = ? ") +
                        "GROUP BY MONTH(dr.reportDate) " +
                        "ORDER BY MONTH(dr.reportDate)";

        Map<Integer, Double> temp = new HashMap<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, year);
            if (supervisorIdOrNull != null) ps.setInt(2, supervisorIdOrNull);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    temp.put(rs.getInt("m"), rs.getDouble("cost"));
                }
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
        // Active projects per month by startDate (from latest assignProjectDetails row).
        String sql =
                "SELECT MONTH(apd.startDate) AS m, COUNT(DISTINCT apd.assignProjectId) AS c " +
                        "FROM assignprojectdetails apd " +
                        "JOIN assignprojects ap ON ap.assignProjectId = apd.assignProjectId " +
                        "WHERE apd.assignProjectDetailId = ( " +
                        "  SELECT MAX(apd2.assignProjectDetailId) FROM assignprojectdetails apd2 " +
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
                while (rs.next()) {
                    temp.put(rs.getInt("m"), rs.getInt("c"));
                }
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
            if (lblExpense != null) lblExpense.setText(formatMoney(d.revenue + d.overhead));
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
            String label = toDisplayStatus(e.getKey());
            data.add(new PieChart.Data(label, e.getValue()));
        }

        pieProjectStatus.setData(data);

        // Apply colors after nodes exist
        Platform.runLater(() -> applyProjectStatusColors(pieProjectStatus));
    }

    private void applyProjectStatusColors(PieChart pie) {
        // required colors:
        // planning orange, finished green, inProgress skyBlue, delay purple
        for (PieChart.Data d : pie.getData()) {
            String name = d.getName();
            Node n = d.getNode();
            if (n == null) continue;

            String color;
            String key = normalizeStatus(name);
            switch (key) {
                case "planning" -> color = "#f59e0b";     // orange
                case "inprogress" -> color = "#38bdf8";   // sky blue
                case "delay" -> color = "#7c3aed";        // purple
                case "finished" -> color = "#22c55e";     // green
                default -> color = "#94a3b8";             // gray fallback
            }
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
            if ("On Time".equalsIgnoreCase(d.getName())) {
                n.setStyle("-fx-pie-color: #22c55e;"); // green
            } else {
                n.setStyle("-fx-pie-color: #7c3aed;"); // purple
            }
        }
    }

    private void updateTop3(List<TopCostRow> top3) {
        // Ensure DESC biggest first (defensive)
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

    private users loginUser = session.getInstance().getUser();
    private boolean isManager() {
        return loginUser != null && role.MANAGER.toString().equalsIgnoreCase(loginUser.getUserRole());
    }

    private Integer supervisorIdOrNull() {
        if (loginUser == null) return null;
        if (!role.SUPERVISOR.toString().equalsIgnoreCase(loginUser.getUserRole())) return null;
        return loginUser.getUserId();
    }

    private String formatMoney(double v) {
        // simple formatting (adjust if you have a utils formatter)
        return String.format(Locale.ENGLISH, "%,.0f", v);
    }

    private String normalizeStatus(String s) {
        if (s == null) return "";
        String t = s.trim().toLowerCase(Locale.ENGLISH);
        // normalize possible variants
        if (t.contains("progress")) return "inprogress";
        if (t.contains("plan")) return "planning";
        if (t.contains("finish")) return "finished";
        if (t.contains("delay")) return "delay";
        return t.replace(" ", "");
    }

    private String toDisplayStatus(String normalized) {
        return switch (normalized) {
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
            if (p instanceof StackPane sp) {
                // if your load pane has a known fx:id, you can check it here.
                return sp;
            }
            p = p.getParent();
        }
        return null;
    }
}
