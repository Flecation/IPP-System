package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.databaseConnection;
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
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static IPPSystem.Controllers.dashboardController.loginUser;

/**
 * allProjectDashboardController (New style – same flow idea as viewProjects/projectDetails/workItemDetails)
 *
 * IMPORTANT schema notes (from your tables.sql):
 * - assignProjects has NO startDate/endDate columns.
 * - baseline startDate/endDate are in assignProjectDetails.
 * - projectStatusName values are: planning, inProgress, delay, finished, cancel (lowercase).
 *
 * Role rules:
 * - MANAGER: show ALL projects stats, comboProjectList disabled (no action)
 * - SUPERVISOR: show ONLY supervisor's projects stats, comboProjectList enabled and contains current in-progress project name(s).
 *               Selecting opens currentProjectDashboard.fxml and passes CURRENT_PROJECT_ID through loadPane properties.
 */
public class allProjectDashboardController {

    @FXML private Label projectName, lblProject, lblRevenue, lblExpense, lblWorkers;

    @FXML private ComboBox<String> comboProjectList;
    @FXML private ComboBox<String> comboProjectList1, comboProjectList2; // keep for FXML compatibility

    @FXML private PieChart pieProjectStatus, pieSchedule;

    @FXML private VBox vboxTtlProject, vboxTtlRevenue, vboxTtlExpense, vboxTtlWorkers,
            vboxProjectStatusDistribution, vboxSchedulePerformance, vboxTop3Intensive, vboxMonthlyProject, vboxActiveProject;

    @FXML private LineChart<String, Number> linechartMonthlyCost, linechartActiveProject;

    @FXML private Label progressCostTyp1, progressCost1, progressCostTyp2, progressCost2, progressCostTyp3, progressCost3;
    @FXML private ProgressBar progressbarTyp1, progressbarTyp2, progressbarTyp3;

    // supervisor combo: projectInstanceName -> assignProjectId
    private final Map<String, Integer> supervisorInProgressMap = new LinkedHashMap<>();

    private boolean isManager() {
        return loginUser != null && role.MANAGER.toString().equalsIgnoreCase(loginUser.getUserRole());
    }

    private Integer supervisorIdOrNull() {
        if (loginUser == null) return null;
        if (!role.SUPERVISOR.toString().equalsIgnoreCase(loginUser.getUserRole())) return null;
        return loginUser.getUserId();
    }

    @FXML
    public void initialize() {
        addHoverEffect(vboxTtlProject);
        addHoverEffect(vboxTtlExpense);
        addHoverEffect(vboxTtlRevenue);
        addHoverEffect(vboxTtlWorkers);

        setupComboByRole();
        loadDashboardAsync();
    }

    private void setupComboByRole() {
        if (comboProjectList == null) return;

        comboProjectList.getItems().clear();
        comboProjectList.setOnAction(null);

        if (isManager()) {
            comboProjectList.setDisable(true);
            comboProjectList.setPromptText("All Projects");
            return;
        }

        Integer sid = supervisorIdOrNull();
        if (sid == null) {
            comboProjectList.setDisable(true);
            return;
        }

        comboProjectList.setDisable(false);
        loadSupervisorInProgressProjectsAsync(sid);
    }

    /**
     * In your DB, projectStatus names:
     *  - planning(1), inProgress(2), delay(3), finished(4), cancel(5) based on insertion order.
     * So inProgress id is usually 2. If yours differs, change IN_PROGRESS_STATUS_ID below.
     */
    private void loadSupervisorInProgressProjectsAsync(int supervisorId) {
        Task<Map<String, Integer>> t = new Task<>() {
            @Override
            protected Map<String, Integer> call() {
                final int IN_PROGRESS_STATUS_ID = 2;

                String sql =
                        "SELECT ap.assignProjectId, ap.projectInstanceName " +
                                "FROM assignProjects ap " +
                                "WHERE ap.supervisorId = ? AND ap.projectStatus = ? " +
                                "ORDER BY ap.assignProjectId DESC";

                Map<String, Integer> out = new LinkedHashMap<>();

                try (Connection con = databaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, supervisorId);
                    ps.setInt(2, IN_PROGRESS_STATUS_ID);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("assignProjectId");
                            String name = rs.getString("projectInstanceName");
                            if (name == null || name.isBlank()) name = "Project #" + id;
                            out.put(name, id);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                return out;
            }
        };

        t.setOnSucceeded(e -> {
            supervisorInProgressMap.clear();
            supervisorInProgressMap.putAll(t.getValue());

            comboProjectList.getItems().setAll(supervisorInProgressMap.keySet());
            if (!comboProjectList.getItems().isEmpty()) {
                comboProjectList.getSelectionModel().select(0);
            }
            comboProjectList.setOnAction(this::clickProjectSelect);
        });

        t.setOnFailed(e -> {
            if (t.getException() != null) t.getException().printStackTrace();
            comboProjectList.setDisable(true);
        });

        Thread th = new Thread(t, "load-supervisor-inprogress-projects");
        th.setDaemon(true);
        th.start();
    }

    @FXML
    void clickProjectSelect(ActionEvent e) {
        Integer sid = supervisorIdOrNull();
        if (sid == null) return;

        String name = comboProjectList == null ? null : comboProjectList.getValue();
        if (name == null) return;

        Integer projectId = supervisorInProgressMap.get(name);
        if (projectId == null) return;

        StackPane loadPane = findLoadPane(comboProjectList);
        if (loadPane == null) {
            utils.openFxml("currentProjectDashboard.fxml", comboProjectList);
            return;
        }

        loadPane.getProperties().put("CURRENT_PROJECT_ID", projectId);
        switchPage.getInstance(loadPane).openFxml("currentProjectDashboard.fxml");
    }

    private void loadDashboardAsync() {
        Integer sid = supervisorIdOrNull(); // null => manager/all

        Task<Void> t = new Task<>() {
            @Override
            protected Void call() {
                loadCardsAndCharts(sid);
                return null;
            }
        };

        t.setOnFailed(e -> {
            if (t.getException() != null) t.getException().printStackTrace();
        });

        Thread th = new Thread(t, "all-project-dashboard-load");
        th.setDaemon(true);
        th.start();
    }

    private void loadCardsAndCharts(Integer supervisorId) {
        // Revenue: sum of latest baseline projectCost per project (latest assignProjectDetails with status in auto/custom/extra)
        double revenue = queryDouble(
                "SELECT IFNULL(SUM(latest.projectCost), 0) " +
                        "FROM (" +
                        "   SELECT ap.assignProjectId, apd.projectCost " +
                        "   FROM assignProjects ap " +
                        "   JOIN assignProjectDetails apd ON apd.assignProjectId = ap.assignProjectId " +
                        "   JOIN assignStatus s ON apd.assignStatusId = s.assignStatusId " +
                        "   WHERE s.assignStatusName IN ('autoAssign','customAssign','extraAssign') " +
                        (supervisorId == null ? "" : " AND ap.supervisorId = ? ") +
                        "   AND apd.assignProjectDetailId = (" +
                        "       SELECT MAX(apd2.assignProjectDetailId) " +
                        "       FROM assignProjectDetails apd2 " +
                        "       JOIN assignStatus s2 ON apd2.assignStatusId = s2.assignStatusId " +
                        "       WHERE apd2.assignProjectId = ap.assignProjectId " +
                        "         AND s2.assignStatusName IN ('autoAssign','customAssign','extraAssign')" +
                        "   )" +
                        ") latest",
                supervisorId
        );

        // Expense: (revenue + overhead) just like your old screen
        double overhead = queryDouble(
                "SELECT IFNULL(SUM(ap.projectOverHeadCost), 0) FROM assignProjects ap WHERE 1=1 " +
                        (supervisorId == null ? "" : " AND ap.supervisorId = ?"),
                supervisorId
        );

        int projects = (int) queryDouble(
                "SELECT COUNT(*) FROM assignProjects ap WHERE 1=1 " +
                        (supervisorId == null ? "" : " AND ap.supervisorId = ?"),
                supervisorId
        );

        int workers;
        if (supervisorId == null) {
            workers = (int) queryDouble("SELECT COUNT(*) FROM labors", null);
        } else {
            workers = (int) queryDouble(
                    "SELECT IFNULL(COUNT(DISTINCT drl.laborId), 0) " +
                            "FROM dailyReports dr " +
                            "JOIN dailyReportLabors drl ON dr.dailyReportId = drl.dailyReportId " +
                            "JOIN assignProjects ap ON dr.assignProjectId = ap.assignProjectId " +
                            "WHERE ap.supervisorId = ?",
                    supervisorId
            );
        }

        final double fRevenue = revenue;
        final double fExpense = revenue + overhead;
        final int fProjects = projects;
        final int fWorkers = workers;

        Platform.runLater(() -> {
            if (lblRevenue != null) lblRevenue.setText(String.format("$%,.2f", fRevenue));
            if (lblExpense != null) lblExpense.setText(String.format("$%,.2f", fExpense));
            if (lblProject != null) lblProject.setText(String.valueOf(fProjects));
            if (lblWorkers != null) lblWorkers.setText(String.valueOf(fWorkers));
        });

        updateProjectStatusPie(supervisorId);
        updateSchedulePie(supervisorId);
        updateMonthlyCostLine(supervisorId);
        updateActiveProjectsLine(supervisorId);
        updateTop3CostBars(supervisorId);
    }

    private double queryDouble(String sql, Integer supervisorId) {
        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (supervisorId != null && sql.contains("?")) {
                // There may be multiple placeholders in some queries; set all to supervisorId
                int count = ps.getParameterMetaData().getParameterCount();
                for (int i = 1; i <= count; i++) ps.setInt(i, supervisorId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private void updateProjectStatusPie(Integer supervisorId) {
        if (pieProjectStatus == null) return;

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        String sql =
                "SELECT ps.projectStatusName, COUNT(ap.assignProjectId) " +
                        "FROM projectStatus ps " +
                        "LEFT JOIN assignProjects ap ON ps.projectStatusId = ap.projectStatus " +
                        "WHERE ps.projectStatusName IN ('planning','inProgress','delay','finished') " +
                        (supervisorId == null ? "" : " AND ap.supervisorId = ? ") +
                        "GROUP BY ps.projectStatusName";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (supervisorId != null) ps.setInt(1, supervisorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Platform.runLater(() -> pieProjectStatus.setData(data));
    }

    /**
     * No ap.endDate or ap.startDate exists. So schedule pie is status-based:
     * - Delayed = delay
     * - On Time = all other non-cancel statuses
     */
    private void updateSchedulePie(Integer supervisorId) {
        if (pieSchedule == null) return;

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();

        String sql =
                "SELECT " +
                        "CASE WHEN ps.projectStatusName = 'delay' THEN 'Delayed' ELSE 'On Time' END AS schedule, " +
                        "COUNT(*) " +
                        "FROM assignProjects ap " +
                        "LEFT JOIN projectStatus ps ON ap.projectStatus = ps.projectStatusId " +
                        "WHERE ps.projectStatusName IN ('planning','inProgress','delay','finished') " +
                        (supervisorId == null ? "" : " AND ap.supervisorId = ? ") +
                        "GROUP BY schedule";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (supervisorId != null) ps.setInt(1, supervisorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Platform.runLater(() -> pieSchedule.setData(data));
    }

    private void updateMonthlyCostLine(Integer supervisorId) {
        if (linechartMonthlyCost == null) return;

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Monthly Cost");

        String sql =
                "SELECT DATE_FORMAT(dr.reportDate, '%Y-%m') AS ym, IFNULL(SUM(drt.dailyCost), 0) AS cost " +
                        "FROM dailyReports dr " +
                        "JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId " +
                        "JOIN assignProjects ap ON dr.assignProjectId = ap.assignProjectId " +
                        "WHERE dr.reportDate IS NOT NULL " +
                        (supervisorId == null ? "" : " AND ap.supervisorId = ? ") +
                        "GROUP BY ym ORDER BY ym";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (supervisorId != null) ps.setInt(1, supervisorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    s.getData().add(new XYChart.Data<>(rs.getString("ym"), rs.getDouble("cost")));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Platform.runLater(() -> linechartMonthlyCost.getData().setAll(s));
    }

    /**
     * assignProjects has no startDate. Use latest assignProjectDetails.startDate as baseline start.
     */
    private void updateActiveProjectsLine(Integer supervisorId) {
        if (linechartActiveProject == null) return;

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Active Projects");

        String sql =
                "SELECT DATE_FORMAT(apd.startDate, '%Y-%m') AS ym, COUNT(*) AS cnt " +
                        "FROM assignProjects ap " +
                        "JOIN assignProjectDetails apd ON ap.assignProjectId = apd.assignProjectId " +
                        "JOIN assignStatus st ON apd.assignStatusId = st.assignStatusId " +
                        "WHERE apd.startDate IS NOT NULL " +
                        "  AND st.assignStatusName IN ('autoAssign','customAssign','extraAssign') " +
                        (supervisorId == null ? "" : " AND ap.supervisorId = ? ") +
                        "  AND apd.assignProjectDetailId = (" +
                        "      SELECT MAX(apd2.assignProjectDetailId) " +
                        "      FROM assignProjectDetails apd2 " +
                        "      JOIN assignStatus st2 ON apd2.assignStatusId = st2.assignStatusId " +
                        "      WHERE apd2.assignProjectId = ap.assignProjectId " +
                        "        AND st2.assignStatusName IN ('autoAssign','customAssign','extraAssign')" +
                        "  ) " +
                        "GROUP BY ym ORDER BY ym";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (supervisorId != null) {
                int count = ps.getParameterMetaData().getParameterCount();
                for (int i = 1; i <= count; i++) ps.setInt(i, supervisorId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    s.getData().add(new XYChart.Data<>(rs.getString("ym"), rs.getInt("cnt")));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Platform.runLater(() -> linechartActiveProject.getData().setAll(s));
    }

    /**
     * Top 3 projects by latest baseline cost.
     */
    private void updateTop3CostBars(Integer supervisorId) {
        String sql =
                "SELECT ap.projectInstanceName, apd.projectCost AS cost " +
                        "FROM assignProjects ap " +
                        "JOIN assignProjectDetails apd ON ap.assignProjectId = apd.assignProjectId " +
                        "JOIN assignStatus st ON apd.assignStatusId = st.assignStatusId " +
                        "WHERE st.assignStatusName IN ('autoAssign','customAssign','extraAssign') " +
                        (supervisorId == null ? "" : " AND ap.supervisorId = ? ") +
                        "  AND apd.assignProjectDetailId = (" +
                        "      SELECT MAX(apd2.assignProjectDetailId) " +
                        "      FROM assignProjectDetails apd2 " +
                        "      JOIN assignStatus st2 ON apd2.assignStatusId = st2.assignStatusId " +
                        "      WHERE apd2.assignProjectId = ap.assignProjectId " +
                        "        AND st2.assignStatusName IN ('autoAssign','customAssign','extraAssign')" +
                        "  ) " +
                        "ORDER BY cost DESC LIMIT 3";

        List<String> names = new ArrayList<>();
        List<Double> costs = new ArrayList<>();

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (supervisorId != null) ps.setInt(1, supervisorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString(1));
                    costs.add(rs.getDouble(2));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        double max = costs.stream().mapToDouble(d -> d).max().orElse(0);

        Platform.runLater(() -> {
            setBar(0, names, costs, max, progressCostTyp1, progressCost1, progressbarTyp1);
            setBar(1, names, costs, max, progressCostTyp2, progressCost2, progressbarTyp2);
            setBar(2, names, costs, max, progressCostTyp3, progressCost3, progressbarTyp3);
        });
    }

    private void setBar(int idx, List<String> names, List<Double> costs, double maxCost,
                        Label typLbl, Label costLbl, ProgressBar bar) {
        if (typLbl == null || costLbl == null || bar == null) return;

        if (idx >= names.size()) {
            typLbl.setText("-");
            costLbl.setText("-");
            bar.setProgress(0);
            return;
        }

        String name = names.get(idx);
        double cost = costs.get(idx);

        typLbl.setText(name == null ? "-" : name);
        costLbl.setText(String.format("$%,.2f", cost));
        bar.setProgress(maxCost <= 0 ? 0 : Math.min(1, cost / maxCost));
    }

    private void addHoverEffect(VBox box) {
        if (box == null) return;
        box.setOnMouseEntered(e -> {
            box.setScaleX(1.05);
            box.setScaleY(1.05);
            box.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");
        });
        box.setOnMouseExited(e -> {
            box.setScaleX(1.0);
            box.setScaleY(1.0);
            box.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        });
    }

    private StackPane findLoadPane(Node node) {
        if (node == null) return null;
        Node cur = node;
        while (cur != null) {
            if (cur instanceof StackPane sp) {
                if ("loadPane".equals(sp.getId()) || sp.getProperties().containsKey("SIDEBAR_CONTROLLER")) {
                    return sp;
                }
            }
            cur = cur.getParent();
        }
        return null;
    }
}
