package IPPSystem.Controllers;

import IPPSystem.Models.DailyReport;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class reportReportController {

    @FXML
    private Label projectName;

    @FXML
    private Label reportId;

    private DailyReport report;

    public void setData(DailyReport report) {
        this.report = report;

        if (report != null) {
            projectName.setText(report.getProjectName());

            // Show report ID with leading zeros, e.g., #RP-0001
            reportId.setText(String.format("#RP-%03d", report.getReportId()));
        } else {
            projectName.setText("-");
            reportId.setText("-");
        }
    }

    // Optional: getter if needed
    public DailyReport getReport() {
        return report;
    }
}
