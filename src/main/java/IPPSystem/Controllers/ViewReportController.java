package IPPSystem.Controllers;

import IPPSystem.DAO.reportDatabase;
import IPPSystem.Models.DailyReport;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ViewReportController {

    @FXML private Label lblProjectName;
    @FXML private Label lblDate;
    @FXML private Label lblWeather;
    @FXML private TextArea txtIssues;
    @FXML private TextArea txtComments;
    // Add other fields from ViewReportNew.fxml

    public void setReportId(int reportId) {
        // Database ကနေ Data အပြည့်အစုံပြန်ဆွဲ
        DailyReport report = reportDatabase.getReportById(reportId);

        if (report != null) {
            if(lblProjectName != null) lblProjectName.setText(report.getProjectName());
            if(lblDate != null) lblDate.setText(report.getReportDate().toString());
            if(lblWeather != null) lblWeather.setText(report.getWeatherType());
            if(txtIssues != null) txtIssues.setText(report.getIssues());
            if(txtComments != null) txtComments.setText(report.getComments());

            // Labor Table Load Logic here...
        }
    }
}