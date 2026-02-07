package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.databaseConnection;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Utils.session;
import IPPSystem.Utils.switchPage;
import IPPSystem.Utils.utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * New style for comboProjectList:
 * - Combo contains 2 items:
 *   1) "Overall Project"
 *   2) current in-progress project's instance name
 * - Selecting "Overall Project" goes to allProjectDashboard.fxml
 * - Selecting current project reloads current dashboard
 *
 * NOTE: This file keeps your existing UI fields so your FXML won't break.
 * You can later plug your EVM calculations here (procedure or SQL) after you confirm correctness.
 */
public class currentProjectDashboardController implements loadPaneAware {

    @FXML private ComboBox<String> comboProjectList;

    @FXML private Label projectName, lblProgressStatus, lbDate, lbCurrentTask;
    @FXML private Label lbDaysRemaining, lbEarnedValue, lbActualCost, lbTotalManHour;
    @FXML private Label lbSPIPercentage, lbSPIValue, lbSPIStatus, lbCPIPercentages, lbCPIValue, lbCPIStatus;

    @FXML private Circle circleSPI, circleCPI;

    @FXML private LineChart<String, Number> lcMonthlyProjectPerformance;
    @FXML private AreaChart<String, Number> acWeeklyResourceUsage;

    private StackPane loadPane;

    private Integer currentAssignProjectId;
    private String currentProjectInstanceName;

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }

    @FXML
    public void initialize() {
        if (lbDate != null) lbDate.setText(LocalDate.now().toString());

        initGauge(circleSPI);
        initGauge(circleCPI);

        // Load current in-progress project name then fill combo with:
        // 1) Overall Project
        // 2) Current in-progress project name
        loadCurrentInProgressProjectAndSetupCombo();

        // Load dashboard (minimal safe default)
        reloadCurrentProjectBasicInfo();
    }

    private void initGauge(Circle circle) {
        if (circle == null) return;
        double r = circle.getRadius();
        double c = 2 * Math.PI * r;
        circle.getStrokeDashArray().setAll(c);
        circle.setStrokeDashOffset(c); // 0%
    }

    private void loadCurrentInProgressProjectAndSetupCombo() {
        Task<Void> t = new Task<>() {
            @Override
            protected Void call() {
                loadCurrentInProgressProject();
                return null;
            }
        };

        t.setOnSucceeded(e -> Platform.runLater(() -> {
            if (comboProjectList == null) return;

            comboProjectList.getItems().clear();
            comboProjectList.getItems().add("Overall Project");

            if (currentProjectInstanceName != null && !currentProjectInstanceName.isBlank()) {
                comboProjectList.getItems().add(currentProjectInstanceName);
                comboProjectList.getSelectionModel().select(currentProjectInstanceName);
            } else {
                comboProjectList.getSelectionModel().select("Overall Project");
            }

            comboProjectList.setOnAction(this::clickProjectSelect);
        }));

        t.setOnFailed(e -> {
            if (t.getException() != null) t.getException().printStackTrace();
            Platform.runLater(() -> {
                if (comboProjectList != null) {
                    comboProjectList.getItems().setAll("Overall Project");
                    comboProjectList.getSelectionModel().select(0);
                    comboProjectList.setOnAction(this::clickProjectSelect);
                }
            });
        });

        Thread th = new Thread(t, "load-current-project-name");
        th.setDaemon(true);
        th.start();
    }

    /**
     * Loads current in-progress project for:
     * - SUPERVISOR: by supervisorId
     * - MANAGER (or others): latest in-progress project (global)
     *
     * IMPORTANT: your DAO uses projectStatus=2 as "inProgressing". Change if needed.
     */
    private void loadCurrentInProgressProject() {
        int IN_PROGRESS_STATUS = 2;

        boolean isSupervisor = session.getInstance().getUser() != null
                && role.SUPERVISOR.toString().equalsIgnoreCase(session.getInstance().getUser().getUserRole());

        String sql;
        if (isSupervisor) {
            sql = "SELECT assignProjectId, projectInstanceName " +
                    "FROM assignProjects WHERE supervisorId = ? AND projectStatus = ? " +
                    "ORDER BY assignProjectId DESC LIMIT 1";
        } else {
            sql = "SELECT assignProjectId, projectInstanceName " +
                    "FROM assignProjects WHERE projectStatus = ? " +
                    "ORDER BY assignProjectId DESC LIMIT 1";
        }

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (isSupervisor) {
                ps.setInt(1, session.getInstance().getUser().getUserId());
                ps.setInt(2, IN_PROGRESS_STATUS);
            } else {
                ps.setInt(1, IN_PROGRESS_STATUS);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentAssignProjectId = rs.getInt("assignProjectId");
                    currentProjectInstanceName = rs.getString("projectInstanceName");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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

        // current in-progress project selected
        reloadCurrentProjectBasicInfo();
    }

    /**
     * Minimal safe loading (no procedure):
     * - project name
     * - days remaining
     * - progress percentage if available (progress_percentage column)
     * - show "-" for CPI/SPI until you decide final calculation approach
     */
    private void reloadCurrentProjectBasicInfo() {
        Task<Void> t = new Task<>() {
            @Override
            protected Void call() {
                if (currentAssignProjectId == null) return null;

                String sql =
                        "SELECT projectInstanceName, targetEndDate, progress_percentage, actualCost " +
                                "FROM assignProjects WHERE assignProjectId = ? LIMIT 1";

                try (Connection con = databaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, currentAssignProjectId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString("projectInstanceName");
                            java.sql.Date tEnd = rs.getDate("targetEndDate");
                            double progress = rs.getDouble("progress_percentage");
                            double ac = rs.getDouble("actualCost");

                            Platform.runLater(() -> {
                                if (projectName != null) projectName.setText(name == null ? "" : name);

                                if (tEnd != null && lbDaysRemaining != null) {
                                    long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), tEnd.toLocalDate());
                                    lbDaysRemaining.setText(days + " Days");
                                } else if (lbDaysRemaining != null) {
                                    lbDaysRemaining.setText("0 Days");
                                }

                                if (lblProgressStatus != null) {
                                    lblProgressStatus.setText(String.format("%.0f%%", progress));
                                }

                                if (lbActualCost != null) lbActualCost.setText(String.format("$%,.2f", ac));

                                // not calculating CPI/SPI here yet
                                if (lbSPIValue != null) lbSPIValue.setText("-");
                                if (lbSPIStatus != null) lbSPIStatus.setText("No Data");
                                if (lbSPIPercentage != null) lbSPIPercentage.setText("-");

                                if (lbCPIValue != null) lbCPIValue.setText("-");
                                if (lbCPIStatus != null) lbCPIStatus.setText("No Data");
                                if (lbCPIPercentages != null) lbCPIPercentages.setText("-");
                            });
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };

        Thread th = new Thread(t, "reload-current-project-basic");
        th.setDaemon(true);
        th.start();
    }
}
