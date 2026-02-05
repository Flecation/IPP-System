package IPPSystem.Controllers;


import IPPSystem.DAO.databaseConnection;
import IPPSystem.DAO.projectDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import java.util.Map;
import java.util.LinkedHashMap;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

    public class allProjectDashboardController extends sideBarPaneController {

        @FXML
        private Label lblProjectName;

        @FXML
        private ComboBox<String> comboProjectList;

        @FXML
        private ComboBox<String> comboProjectList1;

        @FXML
        private ComboBox<?> comboProjectList2;

        @FXML
        private Label lblDayRemainPercent11;

        @FXML
        private Label lblDayRemainPercent111;

        @FXML
        private Label lblDayRemainPercent112;

        @FXML
        private Label lblDayRemainPercent113;

        @FXML
        private PieChart pieStatus;

        @FXML
        private PieChart piePerformance;

        @FXML
        private Label lblTyp1;

        @FXML
        private Label lblTyp1Cost;

        @FXML
        private ProgressBar progressbarTyp1;

        @FXML
        private Label lblTyp2;

        @FXML
        private Label lblTyp2Cost;

        @FXML
        private ProgressBar progressbarTyp2;

        @FXML
        private Label lblTyp3;

        @FXML
        private Label lblTyp3Cost;

        @FXML
        private ProgressBar progressbarTyp3;

        @FXML
        private LineChart<String, Number> linechartMonthlyCost;

        @FXML
        private LineChart<?, ?> linechartActiveProject;

        @FXML
        private Label lblTotalProjects;

        @FXML
        private Label lblTotalRevenue;

        @FXML
        private Label lblTotalExpense;

        @FXML
        private Label lblTotalWorkers;

        private Connection connection;
        private projectDatabase projectDB;

        @FXML
        void clickProjectSelect(ActionEvent event) {
            String selectedProject = comboProjectList.getValue();

        }


        public void initialize() {

        }
}
