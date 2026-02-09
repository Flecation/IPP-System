//package IPPSystem.Controllers;
//
//import IPPSystem.Models.DailyReport;
//import IPPSystem.Interfaces.*;
//import IPPSystem.Utils.session;
//import IPPSystem.Utils.utils;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.Node;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.StackPane;
//import javafx.scene.shape.Circle;
//
//public class reportCardController {
//
//
//    @FXML
//    private Label issueOrSolveTxt;
//
//    @FXML
//    private Label reportId;
//
//    @FXML
//    private Label reportDate;
//
//    @FXML
//    private Label rpPrjectName;
//
//    @FXML
//    private Label rpSupervisorName;
//
//    @FXML
//    private Button viewDetailBtn;
//
//    @FXML
//    private Circle statusCircle;
//
//
//    @FXML
//    private HBox cardRoot;
//
//
//
//    private DailyReport report;
//
//
//
//    @FXML
//    void clickViewDetailBtn(ActionEvent event) {
//
//    }
//
//
//
//
//    // Set data to this card
//    public void setData(DailyReport report) {
//        this.report = report;
//
//        if (report == null) {
//            rpPrjectName.setText("-");
//            rpSupervisorName.setText("-");
//            reportDate.setText("-");
//            issueOrSolveTxt.setText("-");
//            reportId.setText("-");
//            return;
//        }
//
//        rpPrjectName.setText(report.getProjectName());
//        rpSupervisorName.setText(report.getSupervisorName());
//        reportDate.setText(report.getFormattedDate("dd/MM/yyyy"));
//        if (report.getIssues() != null && !report.getIssues().isEmpty()) {
//            // There is an issue
//            issueOrSolveTxt.setText("Issue");
//            statusCircle.setFill(javafx.scene.paint.Color.RED);
//        } else {
//            // No issue
//            issueOrSolveTxt.setText("None"); // or "-"
//            statusCircle.setFill(javafx.scene.paint.Color.GREEN);
//        }
//
//        reportId.setText(String.format("#RP-%03d", report.getReportId()));
//
//    }
//}




package IPPSystem.Controllers;

import IPPSystem.Models.DailyReport;
import IPPSystem.Utils.utils;
import IPPSystem.Utils.linkButton;
import IPPSystem.Controllers.sideBarPaneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class reportCardController {

    @FXML private Label issueOrSolveTxt;
    @FXML private Label reportId;
    @FXML private Label reportDate;
    @FXML private Label rpPrjectName;
    @FXML private Label rpSupervisorName;
    @FXML private Button viewDetailBtn;
    @FXML private Circle statusCircle;
    @FXML private HBox cardRoot;

    private DailyReport report;

    @FXML
    void clickViewDetailBtn(ActionEvent event) {
        if (report == null) return;

        // reportCard is embedded, so discover the current tab loadPane from this node
        var loadPane = utils.findTabLoadPane(cardRoot);
        if (loadPane == null) return;

        sideBarPaneController nav =
                (sideBarPaneController) loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        if (nav == null) return;

        // Open report detail page and pass the selected report
        nav.openInnerView("reportViewDetail.fxml", ctrl -> {
            if (ctrl instanceof reportViewDetailController c) {
                c.setNav(nav);
                c.openReport(report);
            }
        });

        try {
            linkButton.getInstance()
                    .setTabButtonName("Report #RP-" + String.format("%03d", report.getReportId()));
        } catch (Exception ignore) {}
    }

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
            issueOrSolveTxt.setText("Issue");
            statusCircle.setFill(javafx.scene.paint.Color.RED);
        } else {
            issueOrSolveTxt.setText("None");
            statusCircle.setFill(javafx.scene.paint.Color.GREEN);
        }

        reportId.setText(String.format("#RP-%03d", report.getReportId()));
    }
}
