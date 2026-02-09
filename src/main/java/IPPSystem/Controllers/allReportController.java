package IPPSystem.Controllers;

import IPPSystem.DAO.projectDatabase;
import IPPSystem.DAO.reportDatabase;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Models.DailyReport;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.session;
import javafx.collections.FXCollections;
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

public class allReportController  extends sideBarPaneController implements loadPaneAware {

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


    private StackPane loadPane;

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }

    @FXML
    void clickAddNewReport(ActionEvent event) {
        openInLoadPane("CreateReportNew.fxml");

    }

    private void openInLoadPane(String fxml) {
        if (loadPane == null) {
            showAlert("Navigation Error", "Cannot open page because loadPane is not available.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/" + fxml));
            Parent view = loader.load();
            Object controller = loader.getController();

            if (controller instanceof loadPaneAware aware) {
                aware.setLoadPane(loadPane);
            }

            loadPane.getChildren().setAll(view);
            view.toFront();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Navigation Error", "Failed to open: " + fxml + "\n" + ex.getMessage());
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private boolean isManager;

    //        // Load projects (filtered by status and supervisor if manager)
//        applyProjectFilter();
//
//        // Load all reports first (today → older)
//        loadAllReports();


    @FXML
    public void initialize() {
        // Load project status filter
        filterByProjectStatus.setItems(projectDatabase.getAllProjectStatus());
        filterByProjectStatus.setPromptText("Filter By Project Status");
        filterByProjectStatus.setValue("All");

        isManager = session.getInstance()
                .getUser()
                .getUserRole()
                .equalsIgnoreCase("Manager");

        if (isManager) {
            filterBySupervisor.setItems(projectDatabase.getAllSupervisors());
            filterBySupervisor.setPromptText("Choose Engineer");
            filterBySupervisor.setValue("All");

            filterBySupervisor.setVisible(true);
            filterBySupervisor.setManaged(true);

            addNewReport.setVisible(false);
            addNewReport.setManaged(false);

            // Unified supervisor filter logic
            filterBySupervisor.setOnAction(e -> {
                applyProjectFilter(); // refresh project cards

                String selectedSupervisor = filterBySupervisor.getValue();

                List<DailyReport> reportsToShow;

                if (selectedSupervisor == null || "All".equalsIgnoreCase(selectedSupervisor)) {
                    // Show all reports
                    reportsToShow = isManager ? reportDatabase.getAllReports(null)
                            : reportDatabase.getAllReports(session.getInstance().getUser().getUserId());
                } else {
                    // Show all reports of selected supervisor
                    List<projects> supervisorProjects = projectDatabase.getAllProjects().stream()
                            .filter(p -> p.getUserName() != null &&
                                    p.getUserName().equalsIgnoreCase(selectedSupervisor))
                            .collect(Collectors.toList());

                    reportsToShow = new ArrayList<>();
                    for (projects p : supervisorProjects) {
                        reportsToShow.addAll(reportDatabase.getReportsByProjectId(p.getAssignProjectId()));
                    }
                }

                // Apply date filter if set
                LocalDate start = startDate.getValue();
                LocalDate end = endDate.getValue();

                if (start != null) {
                    reportsToShow = reportsToShow.stream()
                            .filter(r -> !r.getReportDate().isBefore(start))
                            .collect(Collectors.toList());
                }
                if (end != null) {
                    reportsToShow = reportsToShow.stream()
                            .filter(r -> !r.getReportDate().isAfter(end))
                            .collect(Collectors.toList());
                }

                // Sort descending
                reportsToShow.sort((r1, r2) -> r2.getReportDate().compareTo(r1.getReportDate()));

                // Load reports
                loadReportsToUI(reportsToShow);
            });
        } else {
            // Supervisor sees add button
            addNewReport.setVisible(true);
            addNewReport.setManaged(true);

            filterBySupervisor.setVisible(false);
            filterBySupervisor.setManaged(false);
        }

        // Set project status filter
        filterByProjectStatus.setOnAction(event -> applyProjectFilter());

        // Date picker filters
        startDate.setOnAction(event -> applyDateFilter());
        endDate.setOnAction(event -> applyDateFilter());

        // ===== LOAD INITIAL DATA =====
        applyProjectFilter(); // load project cards
        loadAllReports();     // load all reports initially
    }




//
//        if (isManager) {
//            filterBySupervisor.setItems(projectDatabase.getAllSupervisors());
//            filterBySupervisor.setValue("All");
//            filterBySupervisor.setPromptText("Choose Engineer...");
//            filterBySupervisor.setValue(null);
//
//
//
//
//            filterBySupervisor.setVisible(true);
//            filterBySupervisor.setManaged(true);
//
//            addNewReport.setVisible(false);
//            addNewReport.setManaged(false);
//
//            filterBySupervisor.setOnAction(e -> applyProjectFilter());
//
//        } else {
//            // Supervisor sees add button
//            addNewReport.setVisible(true);
//            addNewReport.setManaged(true);
//
//            filterBySupervisor.setVisible(false);
//            filterBySupervisor.setManaged(false);
//        }
//




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

            // ===== NEW PART: load all reports of this supervisor automatically =====
            List<DailyReport> allSupervisorReports = new ArrayList<>();
            for (projects p : projectsList) {
                allSupervisorReports.addAll(reportDatabase.getReportsByProjectId(p.getAssignProjectId()));
            }

            // Apply date filter if any
            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();

            if (start != null) {
                allSupervisorReports = allSupervisorReports.stream()
                        .filter(r -> !r.getReportDate().isBefore(start))
                        .collect(Collectors.toList());
            }

            if (end != null) {
                allSupervisorReports = allSupervisorReports.stream()
                        .filter(r -> !r.getReportDate().isAfter(end))
                        .collect(Collectors.toList());
            }

            // Sort descending
            allSupervisorReports.sort((r1, r2) -> r2.getReportDate().compareTo(r1.getReportDate()));

            // Load reports to UI
            loadReportsToUI(allSupervisorReports);
        }

        // Load projects to UI (highlight project cards)
        loadProjectsToUI(projectsList);
    }


//
//    private void applyProjectFilter() {
//        String selectedStatus = filterByProjectStatus.getValue();
//        String selectedSupervisor = isManager ? filterBySupervisor.getValue() : null;
//
//        List<projects> projectsList;
//
//        if (isManager) {
//            projectsList = new ArrayList<>(projectDatabase.getAllProjects());
//        } else {
//            int engineerId = session.getInstance().getUser().getUserId();
//            projectsList = projectDatabase.getProjectsByEngineer(engineerId);
//        }
//
//        // Filter by project status
//        if (!"All".equalsIgnoreCase(selectedStatus)) {
//            projectsList = projectsList.stream()
//                    .filter(p -> p.getProjectStatus() != null &&
//                            p.getProjectStatus().equalsIgnoreCase(selectedStatus))
//                    .collect(Collectors.toList());
//        }
//
//        // Filter by supervisor (only for managers)
//        if (isManager && selectedSupervisor != null && !"All".equalsIgnoreCase(selectedSupervisor)) {
//            projectsList = projectsList.stream()
//                    .filter(p -> p.getUserName() != null &&
//                            p.getUserName().equalsIgnoreCase(selectedSupervisor))
//                    .collect(Collectors.toList());
//        }
//
//        loadProjectsToUI(projectsList);
//    }



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