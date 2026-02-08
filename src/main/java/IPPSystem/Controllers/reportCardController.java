package IPPSystem.Controllers;

import IPPSystem.Models.DailyReport;
import IPPSystem.Utils.loadPaneAware;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class reportCardController {


    @FXML
    private Label issueOrSolveTxt;

    @FXML
    private Label reportId;

    @FXML
    private Label reportDate;

    @FXML
    private Label rpPrjectName;

    @FXML
    private Label rpSupervisorName;

    @FXML
    private Button viewDetailBtn;

    @FXML
    private Circle statusCircle;


    @FXML
    private HBox cardRoot;



    private DailyReport report;



    @FXML
    void clickViewDetailBtn(ActionEvent event) {
        System.out.println(report.getReportId());
    }




    // Set data to this card
    public void setData(DailyReport report) {
        this.report = report;

        if (report == null) {
            rpPrjectName.setText("-");
            rpSupervisorName.setText("-");
            reportDate.setText("-");
            issueOrSolveTxt.setText("-");
            reportId.setText("-");
            return;
        }

        rpPrjectName.setText(report.getProjectName());
        rpSupervisorName.setText(report.getSupervisorName());
        reportDate.setText(report.getFormattedDate("dd/MM/yyyy"));
        if (report.getIssues() != null && !report.getIssues().isEmpty()) {
            // There is an issue
            issueOrSolveTxt.setText("Issue");
            statusCircle.setFill(javafx.scene.paint.Color.RED);
        } else {
            // No issue
            issueOrSolveTxt.setText("None"); // or "-"
            statusCircle.setFill(javafx.scene.paint.Color.GREEN);
        }

        reportId.setText(String.format("#RP-%03d", report.getReportId()));

    }
}
