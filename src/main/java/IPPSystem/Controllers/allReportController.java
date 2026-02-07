package IPPSystem.Controllers;

import IPPSystem.DAO.projectDatabase;
import IPPSystem.DAO.reportDatabase;
import IPPSystem.Models.DailyReport;
import IPPSystem.Models.projects;
import IPPSystem.Utils.session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class allReportController {

    @FXML
    private Button addNewReport;

    @FXML
    private DatePicker endDate;

    @FXML
    private ComboBox<String> filterByProjectStatus;

    @FXML
    private ComboBox<String> filterBySupervisor;


    @FXML
    private HBox paginationBox;

    @FXML
    private VBox reportCardContainer;

    @FXML
    private VBox reportProjectScrollPane;

    @FXML
    private DatePicker startDate;

    @FXML
    private Label countrepotsandissues;

    @FXML
    void clickAddNewReport(ActionEvent event) {

    }


    private boolean isManager;


    @FXML
    public void initialize() {
        // Load project status filter
        filterByProjectStatus.setItems(projectDatabase.getAllProjectStatus());
        filterByProjectStatus.setValue("All");

        isManager = session.getInstance()
                .getUser()
                .getUserRole()
                .equalsIgnoreCase("Manager");

        if (isManager) {
            filterBySupervisor.setItems(projectDatabase.getAllSupervisors());
            filterBySupervisor.setValue("All");
            addNewReport.setVisible(false);

        } else {
            filterBySupervisor.setVisible(false);
        }

        // Load projects (filtered by status and supervisor if manager)
        applyProjectFilter();

        // Load all reports first (today → older)
        loadAllReports();

        // Set filter actions
        filterByProjectStatus.setOnAction(event -> applyProjectFilter());
        if (isManager) {
            filterBySupervisor.setOnAction(event -> applyProjectFilter());
        }

        // ===== Add Date Pickers filter =====
        startDate.setOnAction(event -> applyDateFilter());
        endDate.setOnAction(event -> applyDateFilter());


    }

// Track currently selected project card
    private Parent selectedProjectCard = null;
    private Integer selectedProjectId = null; // store selected project id for date filtering

    private void loadSingleProject(projects project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/reportProjects.fxml"));
            Parent row = loader.load();

            reportProjectController controller = loader.getController();
            controller.setData(project);

            reportProjectScrollPane.getChildren().add(row);

            row.setOnMouseClicked(event -> {
                selectedProjectId = project.getAssignProjectId(); // track selected project

                // Apply reports filter: project + dates
                applyDateFilter();

                // Highlight selected project
                if (selectedProjectCard != null) {
                    selectedProjectCard.setStyle("");
                }
                row.setStyle("-fx-background-color: #FDCB90;");
                selectedProjectCard = row;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ===================== REPORTS =====================
// Load all reports initially (manager: all, supervisor: own)
    private void loadAllReports() {
        List<DailyReport> allReports;

        if (isManager) {
            allReports = new ArrayList<>(reportDatabase.getAllReports(null));
        } else {
            int engineerId = session.getInstance().getUser().getUserId();
            allReports = new ArrayList<>(reportDatabase.getAllReports(engineerId));
        }

        allReports.sort((r1, r2) -> r2.getReportDate().compareTo(r1.getReportDate()));

        loadReportsToUI(allReports);
    }

    // Load reports for a single project (on click)
    private void loadReportsByProject(int assignProjectId) {
        List<DailyReport> reports = new ArrayList<>(reportDatabase.getReportsByProjectId(assignProjectId));
        reports.sort((r1, r2) -> r2.getReportDate().compareTo(r1.getReportDate()));

        loadReportsToUI(reports);
    }

    // ===== New method to apply date filter =====
    private void applyDateFilter() {
        LocalDate start = startDate.getValue();
        java.time.LocalDate end = endDate.getValue();

        List<DailyReport> reports;

        if (selectedProjectId != null) {
            // Filter reports of the selected project
            reports = new ArrayList<>(reportDatabase.getReportsByProjectId(selectedProjectId));
        } else {
            // Filter all reports
            if (isManager) {
                reports = new ArrayList<>(reportDatabase.getAllReports(null));
            } else {
                int engineerId = session.getInstance().getUser().getUserId();
                reports = new ArrayList<>(reportDatabase.getAllReports(engineerId));
            }
        }

        // Apply date range filter
        if (start != null) {
            reports = reports.stream()
                    .filter(r -> !r.getReportDate().isBefore(start))
                    .collect(Collectors.toList());
        }

        if (end != null) {
            reports = reports.stream()
                    .filter(r -> !r.getReportDate().isAfter(end))
                    .collect(Collectors.toList());
        }

        // Sort descending by date
        reports.sort((r1, r2) -> r2.getReportDate().compareTo(r1.getReportDate()));

        loadReportsToUI(reports);
    }



//
//    // ===================== PROJECTS =====================
    private void applyProjectFilter() {
        String selectedStatus = filterByProjectStatus.getValue();
        String selectedSupervisor = isManager ? filterBySupervisor.getValue() : null;

        List<projects> projectsList;

        if (isManager) {
            projectsList = new ArrayList<>(projectDatabase.getAllProjects());
        } else {
            int engineerId = session.getInstance().getUser().getUserId();
            projectsList = projectDatabase.getProjectsByEngineer(engineerId);
        }

        // Filter by project status
        if (!"All".equalsIgnoreCase(selectedStatus)) {
            projectsList = projectsList.stream()
                    .filter(p -> p.getProjectStatus() != null &&
                            p.getProjectStatus().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
        }

        // Filter by supervisor (only for managers)
        if (isManager && selectedSupervisor != null && !"All".equalsIgnoreCase(selectedSupervisor)) {
            projectsList = projectsList.stream()
                    .filter(p -> p.getUserName() != null &&
                            p.getUserName().equalsIgnoreCase(selectedSupervisor))
                    .collect(Collectors.toList());
        }

        loadProjectsToUI(projectsList);
    }


    private void loadProjectsToUI(List<projects> projectsList) {
        reportProjectScrollPane.getChildren().clear();

        for (projects p : projectsList) {
            loadSingleProject(p);
        }
    }
    private void loadReportsToUI(List<DailyReport> reports) {
        reportCardContainer.getChildren().clear();

        if (reports == null || reports.isEmpty()) {
            Label emptyLabel = new Label("Reports are not here yet");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");

            StackPane placeholder = new StackPane(emptyLabel);
            placeholder.setPrefHeight(reportCardContainer.getHeight());
            placeholder.setAlignment(javafx.geometry.Pos.CENTER);

            reportCardContainer.getChildren().add(placeholder);
            updateReportAndIssueCount(reports); // update count even if empty
            return;
        }

        for (DailyReport report : reports) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/reportCard.fxml"));
                Parent rRow = loader.load();

                reportCardController rController = loader.getController();
                rController.setData(report);

                reportCardContainer.getChildren().add(rRow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Update counts after loading all report cards
        updateReportAndIssueCount(reports);
    }
    private void updateReportAndIssueCount(List<DailyReport> reports) {
        if (reports == null || reports.isEmpty()) {
            countrepotsandissues.setText("No reports yet");
            return;
        }

        long reportCount = reports.size();
        long issueCount = reports.stream()
                .filter(r -> r.getIssues() != null && !r.getIssues().isEmpty())
                .count();

        countrepotsandissues.setText("Show " + reportCount + " reports and " + issueCount + " with issues");
        countrepotsandissues.setStyle("-fx-text-fill: #E18600;");
    }


}