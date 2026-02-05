package IPPSystem.Controllers;

import IPPSystem.Models.DailyReport;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TableInCardController {

    @FXML private Label reportIdLabel;
    @FXML private Label dateLabel;
    @FXML private Label projectNameLabel;
    @FXML private Label supervisorNameLabel;
    @FXML private Button viewDetailsButton;

    private DailyReport currentReport;

    @FXML
    public void initialize() {
        // Button click event
        if (viewDetailsButton != null) {
            viewDetailsButton.setOnAction(e -> viewReportDetails());
        }
    }

    public void setReportData(DailyReport report) {
        this.currentReport = report;

        if (report != null) {
            // Set report ID
            if (reportIdLabel != null) {
                reportIdLabel.setText("#RPT-" + String.format("%03d", report.getReportId()));
            }

            // Set date
            if (dateLabel != null) {
                dateLabel.setText(report.getReportDate().toString());
            }

            // Set project name
            if (projectNameLabel != null) {
                projectNameLabel.setText(report.getProjectName());
            }

            // Set supervisor name
            if (supervisorNameLabel != null) {
                supervisorNameLabel.setText("By: " + report.getSupervisorName() + " (Supervisor)");
            }
        }
    }

    private void viewReportDetails() {
        if (currentReport != null) {
            System.out.println("Viewing report details: " + currentReport.getReportId());

            // Open ViewReportNew.fxml with this report's data
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/View/ViewReportNew.fxml"));
                javafx.scene.Parent root = loader.load();

                // Get the controller and set report data
                ViewReportController controller = loader.getController();
                controller.setReportId(currentReport.getReportId());

                // Find the main loadPane and show the report
                HBox card = (HBox) viewDetailsButton.getParent().getParent();
                StackPane loadPane = (StackPane) card.getParent().getParent().getParent().lookup("#loadPane");

                if (loadPane != null) {
                    loadPane.getChildren().setAll(root);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error loading ViewReportNew.fxml");
            }
        }
    }
}