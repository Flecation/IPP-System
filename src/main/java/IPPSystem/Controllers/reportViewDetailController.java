
package IPPSystem.Controllers;

import IPPSystem.DAO.projectDatabase;
import IPPSystem.DAO.reportDatabase;
import IPPSystem.Models.DailyReport;
import IPPSystem.Models.DailyReportLaborView;
import IPPSystem.Models.projects;
import IPPSystem.Utils.session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class reportViewDetailController {


    @FXML
    private TableView<DailyReportLaborView> todayLaborTable;

    @FXML
    private TableColumn<DailyReportLaborView, String> laborNameCol;

    @FXML
    private TableColumn<DailyReportLaborView, String> laborSkillCol;

    @FXML
    private TableColumn<DailyReportLaborView, Double> dailyWadgeCol;

    @FXML
    private TableColumn<DailyReportLaborView, Double> workHourCol;

    @FXML
    private TableColumn<DailyReportLaborView, String> remarkCol;


    @FXML
    private Button backBtn;

    @FXML
    private Label completeQty;

    @FXML
    private Label dailyCostQty;

    @FXML
    private ComboBox<String> filterBySupervisor;



    @FXML
    private DatePicker endDate;





    @FXML
    private Label remainQty;


    @FXML
    private Label reportDate;

    @FXML
    private Label reportIdBySupervisor;

    @FXML
    private Label rpProjectName;
    @FXML
    private Label rpProjectType;

    @FXML
    private DatePicker startDate;

    @FXML
    private Label workedHourQty;


    @FXML
    private Label generalCommentLabel;

    @FXML
    private Label weatherConditionLabel;

    @FXML
    private TextArea issuesLabel;



    @FXML
    private VBox reportProjectScrollPane;
    private List<DailyReport> allReports; // store all reports


    //    @FXML
    //    private Label reportCountLabel;

    @FXML
    void clickBack(ActionEvent event) {
//        try {
//            Parent root = FXMLLoader.load(
//                    getClass().getResource("/View/allReports.fxml")
//            );
//
//            Stage stage = (Stage) backBtn.getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }



    private boolean isManager;
    private Parent selectedReportRow = null; // Track currently selected report

    @FXML
    public void initialize() {
        isManager = session.getInstance()
                .getUser()
                .getUserRole()
                .equalsIgnoreCase("Manager");

        if (isManager) {
            filterBySupervisor.setItems(FXCollections.observableArrayList(
                    projectDatabase.getAllSupervisors()
            ).sorted());
            filterBySupervisor.setValue("All");
            filterBySupervisor.setOnAction(e -> applyFilters());
        } else {
            filterBySupervisor.setVisible(false);
        }

        startDate.setOnAction(e -> applyFilters());
        endDate.setOnAction(e -> applyFilters());

        loadReports();


        laborNameCol.setCellValueFactory(new PropertyValueFactory<>("laborName"));
        laborSkillCol.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        dailyWadgeCol.setCellValueFactory(new PropertyValueFactory<>("dailyWage"));
        workHourCol.setCellValueFactory(new PropertyValueFactory<>("workHours"));
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));

    }

    private void loadReports() {
        if (isManager) {
            allReports = new ArrayList<>(reportDatabase.getAllReports(null));
        } else {
            int supervisorId = session.getInstance().getUser().getUserId();
            allReports = new ArrayList<>(reportDatabase.getAllReports(supervisorId));
        }

        allReports.sort((r1, r2) -> r2.getReportDate().compareTo(r1.getReportDate()));
        loadReportsToUI(allReports);
    }

    private void applyFilters() {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        String selectedSupervisor = isManager ? filterBySupervisor.getValue() : null;

        List<DailyReport> filtered = new ArrayList<>(allReports);

        if (isManager && selectedSupervisor != null && !"All".equalsIgnoreCase(selectedSupervisor)) {
            filtered = filtered.stream()
                    .filter(r -> r.getSupervisorName() != null &&
                            r.getSupervisorName().equalsIgnoreCase(selectedSupervisor))
                    .collect(Collectors.toList());
        }

        if (start != null) {
            filtered = filtered.stream()
                    .filter(r -> !r.getReportDate().isBefore(start))
                    .collect(Collectors.toList());
        }

        if (end != null) {
            filtered = filtered.stream()
                    .filter(r -> !r.getReportDate().isAfter(end))
                    .collect(Collectors.toList());
        }

        filtered.sort((r1, r2) -> r2.getReportDate().compareTo(r1.getReportDate()));

        loadReportsToUI(filtered);
    }

    private void loadReportsToUI(List<DailyReport> reports) {
        reportProjectScrollPane.getChildren().clear();

        if (reports == null || reports.isEmpty()) {
            Label emptyLabel = new Label("Reports are not here yet");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            reportProjectScrollPane.getChildren().add(emptyLabel);
            return;
        }

        for (DailyReport report : reports) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/reportReport.fxml"));
                Parent reportRow = loader.load();

                reportReportController controller = loader.getController();
                controller.setData(report);

                reportRow.setOnMouseClicked(e -> {

                    if (selectedReportRow != null) {
                        selectedReportRow.setStyle("");
                    }
                    reportRow.setStyle("-fx-background-color: #FDCB90;");
                    selectedReportRow = reportRow;

                    rpProjectName.setText(report.getProjectName() != null ? report.getProjectName() : "-");
                    rpProjectType.setText(report.getProjectTypeName() != null ? report.getProjectTypeName() : "-");

                    reportIdBySupervisor.setText(
                            String.format("#RP-%03d", report.getReportId()) +
                                    " By " + report.getSupervisorName()
                    );

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    reportDate.setText(
                            report.getReportDate() != null
                                    ? report.getReportDate().format(formatter)
                                    : "-"
                    );

                    todayLaborTable.setItems(
                            FXCollections.observableArrayList(
                                    reportDatabase.getDailyReportLabors(report.getReportId())
                            )
                    );

//                    // -------- Totals --------
//                    double workedHours = reportDatabase.getTotalWorkedHours(report.getReportId());
//                    double dailyCost = reportDatabase.getTotalDailyLaborCost(report.getReportId());
//                    double completedQty = reportDatabase.getCompletedQty(report.getReportId());
//                    double remainQtyVal = reportDatabase.getRemainQty(report.getAssignWorkItemId());
//
//                    workedHourQty.setText(String.format("%.1f hrs", workedHours));
//                    dailyCostQty.setText(String.format("%.2f", dailyCost));
//                    completeQty.setText(String.format("%.2f", completedQty));
//                    remainQty.setText(String.format("%.2f", remainQtyVal));

                    // ---- TOTALS ----
                    workedHourQty.setText(
                            String.format("%.1f hrs",
                                    reportDatabase.getTotalWorkedHours(report.getReportId()))
                    );

                    dailyCostQty.setText(
                            String.format("%.2f",
                                    reportDatabase.getTotalDailyLaborCost(report.getReportId()))
                    );

                    completeQty.setText(
                            String.format("%.2f",
                                    reportDatabase.getCompletedQty(report.getReportId()))
                    );

                    remainQty.setText(
                            String.format("%.2f",
                                    reportDatabase.getRemainQty(report.getAssignWorkItemId()))
                    );

                    // ---- GENERAL COMMENT ----
                    generalCommentLabel.setText(
                            report.getComments() != null
                                    ? report.getComments()
                                    : "-"
                    );

                    // ---- WEATHER ----
                    weatherConditionLabel.setText(
                            report.getWeatherNote() != null
                                    ? report.getWeatherNote()
                                    : "-"
                    );

                    // ---- ISSUES (line by line) ----
                    String issues = report.getIssues();

                    if (issues == null || issues.trim().isEmpty()) {
                        issuesLabel.setText("-");
                    } else {
                        issuesLabel.setText(
                                Arrays.stream(issues.split("//"))
                                        .map(String::trim)
                                        .filter(s -> !s.isEmpty())
                                        .map(s -> "• " + s)
                                        .collect(Collectors.joining("\n"))
                        );
                    }



                });



                reportProjectScrollPane.getChildren().add(reportRow);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
