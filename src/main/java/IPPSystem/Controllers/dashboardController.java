package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.Models.users;
import IPPSystem.Utils.session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

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
    private ComboBox<?> comboOverview;

    @FXML
    private ComboBox<String> comboProjectList;

    @FXML
    private ComboBox<?> comboStatus;

    @FXML
    private ComboBox<?> comboYear;

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
    private LineChart<?, ?> linechartSiteProgress;

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

// Map of project name to location (used by clickProjectSelect)
    private java.util.Map<String, String> projectLocations = new java.util.HashMap<>();

    protected static users loginUser = session.getInstance().getUser();

    @FXML 
    public void initialize(){

//        hide all pages
        oneProjectView.setVisible(false);
        allProjectView.setVisible(false);

        if (loginUser.getUserRole().equals(role.MANAGER.toString())){
            allProjectView.setVisible(true);
        }else {
            oneProjectView.setVisible(true);
        }

        //1.Project list
        comboProjectList.getItems().setAll("Project A","Project B","Project C");
        comboProjectList.getSelectionModel().selectFirst();

        //2. Project -> Location mapping   
        projectLocations.put("Project A","Yangon");
        projectLocations.put("Project B","Manadalay");
        projectLocations.put("Project C","Naypyidaw");

        //Set initial location for default selection
        String first=comboProjectList.getValue();
        if(first!=null){
            lbLocation.setText(projectLocations.getOrDefault(first,"-"));
        }
    }
    @FXML
    void clickProjectSelect(ActionEvent event) {
        String selected=comboProjectList.getValue();
        if(selected==null);
        lbLocation.setText(projectLocations.getOrDefault(selected,"-"));

    }



}
