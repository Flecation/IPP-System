package IPPSystem.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Circle;

public class dashboardController {

    @FXML
    private BarChart<?, ?> bcWeeklyResourceUsage;

    @FXML
    private AreaChart<?, ?> chartProfit;

    @FXML
    private Circle circleCPI;

    @FXML
    private Circle circleCost;

    @FXML
    private Circle circleRevenue;

    @FXML
    private Circle circleSPI;

    @FXML
    private ComboBox<?> comboProjectList;

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
    private Label lblCompletedTaskPercent;

    @FXML
    private Label lblDayRemainPercent;

    @FXML
    private Label lblEarnedValuePercent;

    @FXML
    private Label lblManpower;

    @FXML
    private Label lblProgressPercent;

    @FXML
    private Label lblWeeklySPI;

    @FXML
    private LineChart<?, ?> lcProjectValueAnalysis;

    @FXML
    private Label lblLocation;

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
    private PieChart pieCost;

    @FXML
    private PieChart pieProj;

    @FXML
    private Label projectName;

    @FXML
    private Label revenueDisplay;

    @FXML
    private ComboBox<?> status;

    @FXML
    private Label totalProj;

    @FXML
    private ComboBox<?> year;

    @FXML
    void clickProjectSelect(ActionEvent event) {

    }

}
