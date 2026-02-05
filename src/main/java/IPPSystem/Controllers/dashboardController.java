package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.databaseConnection;
import IPPSystem.Models.users;
import IPPSystem.Utils.session;
import com.zaxxer.hikari.HikariDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.ResourceBundle;

public class dashboardController extends sideBarPaneController{

    @FXML
    private AreaChart<?, ?> aerachartProfit;

    @FXML
    private BorderPane allProjectView;

    @FXML
    private BarChart<?, ?> bcWeeklyResourceUsage;

    @FXML
    private Circle circleCost;

    @FXML
    private Circle circleRevenue;

    @FXML
    private Circle circleRevenueProgress;

    @FXML
    private ComboBox<String> comboOverview;

    @FXML
    private ComboBox<String> comboProjectList;

    @FXML
    private ComboBox<String> comboStatus;

    @FXML
    private ComboBox<String> comboYear;

    @FXML
    private Label costDisplay;

    @FXML
    private Label lbCPIPVValue;

    @FXML
    private Label lbCPIValue;

    @FXML
    private Label lbLocation;

    @FXML
    private Label lbPeakWorkforce;

    @FXML
    private Label lbSPIEVvalue;

    @FXML
    private Label lbSPIPVvalue;

    @FXML
    private Label lbSPIValue;

    @FXML
    private Label lbTotalManHour;

    @FXML
    private Label lblCPIEVValue;

    @FXML
    private Label lblCompleted;

    @FXML
    private Label lblCompletedTaskPercent;

    @FXML
    private Label lblCost;

    @FXML
    private Label lblDayRemainPercent;

    @FXML
    private Label lblDelayStatus;

    @FXML
    private Label lblEarnedValuePercent;

    @FXML
    private Label lblHanoverDate;

    @FXML
    private ScrollPane lblLaborBreakdown;

    @FXML
    private Label lblManpower;

    @FXML
    private Label lblPending;

    @FXML
    private Label lblProgress;

    @FXML
    private Label lblProgressPercent;

    @FXML
    private Label lblProgressStatus;

    @FXML
    private Label lblRevenue;

    @FXML
    private Label lblTotalExpense;

    @FXML
    private Label lblTotalProject;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private Label lblTotalWorker;

    @FXML
    private Label lblWeeklySPI;

    @FXML
    private LineChart<?, ?> lcProjectValueAnalysis;

    @FXML
    private LineChart<String, Number> linechartSiteProgress;

    @FXML
    private BorderPane oneProjectView;

    @FXML
    private ProgressBar pbActualDuration;

    @FXML
    private ProgressBar pbCompletedTask;

    @FXML
    private ProgressBar pbDayRemaining;

    @FXML
    private ProgressBar pbEarnedValue;

    @FXML
    private ProgressBar pbPlanDuration;

    @FXML
    private PieChart pieCostBreakdown;

    @FXML
    private PieChart pieOngoingProj;

    @FXML
    private Label projectName;

    @FXML
    private Label revenueDisplay;

    @FXML
    private ScrollPane scrollpaneKeyIssue;

//    Map of project name to location (used by clickProjectSelect)
    private java.util.Map<String, String> projectLocations = new java.util.HashMap<>();

    protected static users loginUser = session.getInstance().getUser();

    @FXML
    public void initialize() {
        loadSummaryCards();
        loadOngoingProjects();
//        loadCostBreakdown();
//        loadSiteProgress();
//        loadProfit();
//        loadProgressCircles();
    }
    // ---------- SUMMARY CARDS ----------
    private void loadSummaryCards() {
        try (Connection con = databaseConnection.getConnection();
             Statement st = con.createStatement()) {

            ResultSet rs1 = st.executeQuery(
                    "SELECT Count(assignProjectID) FROM assignprojectdetails");
            if (rs1.next())
                lblTotalProject.setText(rs1.getInt(1) + "");

            ResultSet rs2 = st.executeQuery(
                    "SELECT COUNT(assignWorkerID) FROM assignworkers");
            if (rs2.next())
                lblTotalWorker.setText(rs2.getInt(1) + "+");

            ResultSet rs3 = st.executeQuery(
                    "SELECT SUM(dailyWage), SUM(cost) FROM dailyreportlabors");
            if (rs3.next()) {
                double revenue = rs3.getDouble("dailyWage");
                double cost = rs3.getDouble("dailyWage");

//                double total = revenue + cost;
//
//                int revenuePercent = (int) (() * 100);
//                int costPercent = (int) (() * 100);
//
//                lblTotalRevenue.setText(revenuePercent + "%");
//                lblTotalExpense.setText(costPercent + "%");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ---------- ONGOING PROJECT PIE ----------
    private void loadOngoingProjects() {
        pieOngoingProj.getData().clear();

        try (Connection con = databaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT  COUNT(detailStatus)  FROM assignworkitemdetails")) {

            while (rs.next()) {
                pieOngoingProj.getData().add(
                        new PieChart.Data(
                                rs.getString("detailStatus"),
                                rs.getInt("detailStatus")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ---------- COST BREAKDOWN ----------
    private void loadCostBreakdown() {
        pieCostBreakdown.getData().clear();

        try (Connection con = databaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT " +
                             "l.dailyWage," +
                             "w.workItemCost" +
                             "FROM dailyreportlabors l " +
                             "INNER JOIN assignworkitemdetails w" +
                             "on l.")) {
//have to research this.....................................................
            while (rs.next()) {
                pieCostBreakdown.getData().add(
                        new PieChart.Data(
                                rs.getString(""),
                                rs.getDouble("")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ---------- SITE PROGRESS ----------
    private void loadSiteProgress() {
        linechartSiteProgress.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Progress");

        try (Connection con = databaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "")) {

//            while (rs.next()) {
//                series.getData().add(
//                        new XYChart.Data<>(
//                                rs.getString(""),
//                                rs.getInt("")));
//            }
//            linechartSiteProgress.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
