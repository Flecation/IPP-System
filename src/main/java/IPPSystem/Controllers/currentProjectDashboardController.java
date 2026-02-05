package IPPSystem.Controllers;

import IPPSystem.DAO.databaseConnection;
import IPPSystem.Utils.utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

public class currentProjectDashboardController {

    @FXML private ComboBox<String> comboProjectList;
    @FXML private Label projectName, lblProgressStatus, lbDate;
    @FXML private Label lbDaysRemaining, lbEarnedValue, lbActualCost;
    @FXML private Label lbSPIPercentage, lbSPIValue, lbSPIStatus, lbCPIPercentages, lbCPIValue, lbCPIStatus;
    @FXML private Circle circleSPI, circleCPI;
    @FXML private LineChart<String, Number> lcMonthlyProjectPerformance;
    @FXML private AreaChart<String, Number> acWeeklyResourceUsage;

    @FXML
    public void initialize() {
        lbDate.setText(LocalDate.now().toString());
        comboProjectList.getItems().setAll("Project Overview", "Active Project");
        comboProjectList.getSelectionModel().select("Active Project");
        initGauge(circleSPI);
        initGauge(circleCPI);
        refreshDashboardData();
    }

    private void initGauge(Circle circle) {
        circle.setStrokeType(javafx.scene.shape.StrokeType.CENTERED);
        circle.setRotate(-90);
        circle.getStrokeDashArray().clear();
    }

    @FXML
    void clickProjectSelect(ActionEvent event) {
        if ("Project Overview".equals(comboProjectList.getValue())) {
            utils.openFxml("allProjectDashboard.fxml", null);
        } else {
            refreshDashboardData();
        }
    }

    private void refreshDashboardData() {
        try (Connection con = databaseConnection.getConnection()) {
            String query = "SELECT assignProjectId, projectInstanceName, projectOverHeadCost as budget, " +
                    "actualCost as ac, progress_percentage as prog, targetEndDate " +
                    "FROM assignprojects WHERE projectStatus = 1 LIMIT 1";
            ResultSet rs = con.prepareStatement(query).executeQuery();
            if (rs.next()) {
                int pId = rs.getInt("assignProjectId");
                double budget = rs.getDouble("budget");
                double ac = rs.getDouble("ac");
                double progressPercent = rs.getDouble("prog");
                double ev = budget * (progressPercent / 100.0);
                double spi = progressPercent / 100.0;
                double cpi = (ac > 0) ? (ev / ac) : 1.0;

                projectName.setText(rs.getString("projectInstanceName"));
                lbEarnedValue.setText(String.format("$%,.0f", ev));
                lbActualCost.setText(String.format("$%,.0f", ac));
                Date target = rs.getDate("targetEndDate");
                lbDaysRemaining.setText(target != null ? ChronoUnit.DAYS.between(LocalDate.now(), target.toLocalDate()) + " Days" : "0 Days");

                loadCharts(con, pId);
                updateGaugeUI(spi, cpi);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadCharts(Connection con, int pId) throws SQLException {
        lcMonthlyProjectPerformance.getData().clear();
        XYChart.Series<String, Number> seriesEV = new XYChart.Series<>(); seriesEV.setName("EV");
        XYChart.Series<String, Number> seriesAC = new XYChart.Series<>(); seriesAC.setName("AC");
        XYChart.Series<String, Number> seriesPV = new XYChart.Series<>(); seriesPV.setName("PV");
        String sql = "SELECT MONTHNAME(reportDate) as m, SUM(actualCost) as total FROM dailyreports WHERE assignProjectId = ? GROUP BY MONTH(reportDate) ORDER BY MONTH(reportDate)";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, pId);
        ResultSet rs = pstmt.executeQuery();
        while(rs.next()) {
            String month = rs.getString("m").substring(0, 3);
            double valAC = rs.getDouble("total");
            seriesAC.getData().add(new XYChart.Data<>(month, valAC));
            seriesPV.getData().add(new XYChart.Data<>(month, valAC * 0.95));
            seriesEV.getData().add(new XYChart.Data<>(month, valAC * 1.05));
        }
        lcMonthlyProjectPerformance.getData().addAll(seriesEV, seriesPV, seriesAC);

        acWeeklyResourceUsage.getData().clear();
        XYChart.Series<String, Number> seriesLabor = new XYChart.Series<>(); seriesLabor.setName("Labor Hours");
        Map<String, Double> dayMap = new LinkedHashMap<>();
        dayMap.put("Mon", 0.0); dayMap.put("Tue", 0.0); dayMap.put("Wed", 0.0); dayMap.put("Thu", 0.0); dayMap.put("Fri", 0.0); dayMap.put("Sat", 0.0);
        String sqlLabor = "SELECT DAYNAME(dr.reportDate) as d, SUM(dl.workHours) as hrs FROM dailyreportlabors dl JOIN dailyreports dr ON dl.dailyReportId = dr.dailyReportId WHERE dr.assignProjectId = ? AND DAYNAME(dr.reportDate) != 'Sunday' GROUP BY DAYNAME(dr.reportDate), DAYOFWEEK(dr.reportDate) ORDER BY DAYOFWEEK(dr.reportDate)";
        PreparedStatement pstmt2 = con.prepareStatement(sqlLabor);
        pstmt2.setInt(1, pId);
        ResultSet rs2 = pstmt2.executeQuery();
        while(rs2.next()) {
            String dayKey = rs2.getString("d").substring(0, 3);
            if (dayMap.containsKey(dayKey)) dayMap.put(dayKey, rs2.getDouble("hrs"));
        }
        for (Map.Entry<String, Double> entry : dayMap.entrySet()) seriesLabor.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        acWeeklyResourceUsage.getData().add(seriesLabor);
    }

    private void updateGaugeUI(double spi, double cpi) {
        drawProgress(circleSPI, spi, lbSPIPercentage, lbSPIValue, lbSPIStatus, true);
        drawProgress(circleCPI, cpi, lbCPIPercentages, lbCPIValue, lbCPIStatus, false);
        if (spi >= 1.0 && cpi >= 1.0) {
            lblProgressStatus.setText("Good Progress");
            lblProgressStatus.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 5;");
        } else if (spi < 0.8) {
            lblProgressStatus.setText("Delay");
            lblProgressStatus.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-background-radius: 5;");
        } else {
            lblProgressStatus.setText("Needs Attention");
            lblProgressStatus.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-background-radius: 5;");
        }
    }

    private void drawProgress(Circle circle, double value, Label lbPercent, Label lbVal, Label lbStat, boolean isSPI) {
        double circumference = 2 * Math.PI * circle.getRadius();
        double clampedValue = Math.max(0, Math.min(value, 1.0));
        Platform.runLater(() -> {
            circle.getStrokeDashArray().setAll(clampedValue * circumference, circumference);
            lbPercent.setText(String.format("%.0f%%", value * 100));
            lbVal.setText(String.format("%.2f", value));
            if (value >= 1.0) {
                circle.setStroke(Color.LIMEGREEN);
                lbStat.setText(isSPI ? "On Track" : "Under Budget");
            } else if (value >= 0.85) {
                circle.setStroke(Color.GOLD);
                lbStat.setText(isSPI ? "Warning" : "Budget Alert");
            } else {
                circle.setStroke(Color.RED);
                lbStat.setText(isSPI ? "Behind Schedule" : "Over Budget");
            }
        });
    }
}