package IPPSystem.Controllers;

import IPPSystem.Models.DailyReport;
import IPPSystem.Models.projects;
import IPPSystem.Constants.role;
import IPPSystem.DAO.databaseConnection;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.util.*;

public class reportController extends sideBarPaneController {

    // FXML Components
    @FXML private VBox reportContainer;
    @FXML private VBox sidebarProjectList;
    @FXML private ComboBox<String> projectSidebarFilter;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button btnAddReport;
    @FXML private Label reportCountLabel;
    @FXML private Button btnResetFilters;
    @FXML private ScrollPane mainScrollPane;
    @FXML private ScrollPane sidebarScrollPane;

    // Database connection
    private Connection connection = null;

    // Data
    private final ObservableList<DailyReport> allReports = FXCollections.observableArrayList();
    private final ObservableList<DailyReport> currentDisplayReports = FXCollections.observableArrayList();
    private final ObservableList<projects> projectList = FXCollections.observableArrayList();
    private final ObservableList<projects> filteredProjectList = FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private int currentProjectId = 0;
    private String currentProjectName = "";

    // Duplicate မဖြစ်အောင် ထိန်းချုပ်မယ့် variable
    private boolean isInitialized = false;
    private boolean isLoadingProjects = false;

    @FXML
    public void initialize() {
        if (isInitialized) {
            System.out.println("⚠️ Controller already initialized, skipping...");
            return;
        }

        System.out.println("🚀 Initializing Report Controller...");

        // Initialize database connection
        try {
            connection = databaseConnection.getConnection();
            System.out.println("✅ Database connection established successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Cannot connect to database: " + e.getMessage());
            System.out.println("⚠️ Using empty data mode due to database connection failure");
        }

        setupDatePickers();
        setupFilters();
        setupButtonActions();
        setupScrollPanes();

        // Check user role to show/hide add report button
        if (user != null) {
            String userRole = user.getUserRole();
            if (userRole.equals(role.MANAGER.toString())) {
                btnAddReport.setVisible(false);
                btnAddReport.setManaged(false);
                System.out.println("👔 Manager logged in - Add Report button hidden");
            } else {
                btnAddReport.setVisible(true);
                btnAddReport.setManaged(true);
                System.out.println("👷 Supervisor logged in - Add Report button shown");
            }
        } else {
            System.out.println("⚠️ No user found, defaulting to Supervisor view");
            btnAddReport.setVisible(true);
            btnAddReport.setManaged(true);
        }

        if (connection != null) {
            loadProjectsFromDatabase();
            loadReportsFromDatabase();
        } else {
            showEmptyState();
        }

        displayProjectSidebar();
        displayAllReports();
        updateReportCount();

        System.out.println("✅ Loaded: " + allReports.size() + " reports from database");
        System.out.println("✅ Loaded: " + projectList.size() + " unique projects from database");

        isInitialized = true;
        System.out.println("🎯 Controller initialization COMPLETE");
    }

    private void setupScrollPanes() {
        if (mainScrollPane != null) {
            mainScrollPane.setFitToWidth(true);
            mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }

        if (sidebarScrollPane != null) {
            sidebarScrollPane.setFitToWidth(true);
            sidebarScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sidebarScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
    }

    private void setupDatePickers() {
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());

        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupFilters() {
        projectSidebarFilter.getItems().clear();

        // ✅ "All Assigned" ကို ဦးဆုံးထည့်
        projectSidebarFilter.getItems().add("All Assigned");

        // ✅ Database ကနေ project status တွေကို ယူပြီး combobox ထဲထည့်
        try {
            String query = "SELECT DISTINCT projectStatusName FROM projectstatus ORDER BY projectStatusName";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String status = rs.getString("projectStatusName");
                projectSidebarFilter.getItems().add(status);
            }

            rs.close();
            stmt.close();

            // ✅ Default အနေနဲ့ "All Assigned" ကို ရွေးထားပေး
            projectSidebarFilter.setValue("All Assigned");

            // ✅ Combobox ရွေးလိုက်တိုင်း filter လုပ်မယ်
            projectSidebarFilter.setOnAction(e -> {
                System.out.println("🔽 Combobox changed to: " + projectSidebarFilter.getValue());
                filterProjectSidebar();
                applyFilters();
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load status filters: " + e.getMessage());
        }
    }

    private void setupButtonActions() {
        btnAddReport.setOnAction(e -> showNewReportDialog());
        btnResetFilters.setOnAction(e -> resetAllFilters());
    }

    private void showEmptyState() {
        projectList.clear();
        allReports.clear();

        Label noDataLabel = new Label("No database connection available");
        noDataLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 20;");
        reportContainer.getChildren().add(noDataLabel);

        Label noProjectsLabel = new Label("No projects available");
        noProjectsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 20;");
        sidebarProjectList.getChildren().add(noProjectsLabel);
    }

    private void loadProjectsFromDatabase() {
        if (isLoadingProjects) {
            System.out.println("⚠️ Already loading projects, skipping...");
            return;
        }

        isLoadingProjects = true;
        projectList.clear();
        filteredProjectList.clear();

        System.out.println("📥 Starting to load projects from database...");

        try {
            String query;

            if (user != null && user.getUserRole().equals(role.SUPERVISOR.toString())) {
                // For supervisors: load assigned projects
                query = "SELECT " +
                        "ap.assignProjectId, " +
                        "ap.projectInstanceName, " +
                        "pt.typeName as projectTypeName, " +
                        "ps.projectStatusName as projectStatus " +
                        "FROM assignprojects ap " +
                        "JOIN projecttypes pt ON ap.projectTypeId = pt.projectTypeId " +
                        "JOIN projectstatus ps ON ap.projectStatus = ps.projectStatusId " +
                        "WHERE ap.supervisorId = ? " +
                        "GROUP BY ap.assignProjectId, ap.projectInstanceName, pt.typeName, ps.projectStatusName " +
                        "ORDER BY ap.projectInstanceName";

                System.out.println("👷 Loading projects for supervisor ID: " + user.getUserId());
            } else {
                // For managers: load all projects
                query = "SELECT " +
                        "ap.assignProjectId, " +
                        "ap.projectInstanceName, " +
                        "pt.typeName as projectTypeName, " +
                        "ps.projectStatusName as projectStatus " +
                        "FROM assignprojects ap " +
                        "JOIN projecttypes pt ON ap.projectTypeId = pt.projectTypeId " +
                        "JOIN projectstatus ps ON ap.projectStatus = ps.projectStatusId " +
                        "GROUP BY ap.assignProjectId, ap.projectInstanceName, pt.typeName, ps.projectStatusName " +
                        "ORDER BY ap.projectInstanceName";

                System.out.println("👔 Loading all projects for manager");
            }

            PreparedStatement pstmt = connection.prepareStatement(query);

            if (user != null && user.getUserRole().equals(role.SUPERVISOR.toString())) {
                pstmt.setInt(1, user.getUserId());
            }

            ResultSet rs = pstmt.executeQuery();

            int rowCount = 0;
            Set<Integer> duplicateCheck = new HashSet<>();

            while (rs.next()) {
                rowCount++;
                int projectId = rs.getInt("assignProjectId");

                if (duplicateCheck.contains(projectId)) {
                    System.out.println("🚨 DUPLICATE in ResultSet! Project ID: " + projectId);
                    continue;
                }

                duplicateCheck.add(projectId);

                projects project = new projects();
                project.setAssignProjectId(projectId);
                project.setProjectInstanceName(rs.getString("projectInstanceName"));
                project.setProjectTypeName(rs.getString("projectTypeName"));
                project.setProjectStatus(rs.getString("projectStatus"));

                projectList.add(project);
            }

            System.out.println("📊 Database returned " + rowCount + " rows");

            // Remove duplicates in Java list
            removeDuplicatesFromProjectList();

            filteredProjectList.setAll(projectList);
            rs.close();
            pstmt.close();

            System.out.println("✅ Final unique projects in memory: " + projectList.size());

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load projects: " + e.getMessage());
        } finally {
            isLoadingProjects = false;
        }
    }

    private void removeDuplicatesFromProjectList() {
        Map<Integer, projects> uniqueProjects = new HashMap<>();
        List<projects> duplicateList = new ArrayList<>();

        for (projects project : projectList) {
            int projectId = project.getAssignProjectId();

            if (uniqueProjects.containsKey(projectId)) {
                duplicateList.add(project);
                System.out.println("❌ FOUND DUPLICATE: Project ID " + projectId);
            } else {
                uniqueProjects.put(projectId, project);
            }
        }

        if (!duplicateList.isEmpty()) {
            System.out.println("🚨 Total duplicates found in Java list: " + duplicateList.size());
            projectList.clear();
            projectList.addAll(uniqueProjects.values());
            System.out.println("✅ Removed duplicates. Now have " + projectList.size() + " unique projects");
        } else {
            System.out.println("✅ No duplicates found in Java list");
        }
    }

    private void loadReportsFromDatabase() {
        allReports.clear();
        currentDisplayReports.clear();

        try {
            String query;
            if (user != null && user.getUserRole().equals(role.SUPERVISOR.toString())) {
                // For supervisors: load their own reports
                query = "SELECT " +
                        "dr.dailyReportId, " +
                        "ap.assignProjectId, " + // 🔥 ADD THIS
                        "ap.projectInstanceName, " +
                        "dr.reportDate, " +
                        "dr.issue, " +
                        "u.userName as supervisorName " +
                        "FROM dailyreports dr " +
                        "JOIN assignprojects ap ON dr.assignProjectId = ap.assignProjectId " +
                        "JOIN users u ON dr.supervisorId = u.userId " +
                        "WHERE dr.supervisorId = ? " +
                        "ORDER BY dr.dailyReportId DESC"; // 🔥 Changed to ID DESC for RPT-001, 002 order
            } else {
                // For managers: load all reports
                query = "SELECT " +
                        "dr.dailyReportId, " +
                        "ap.assignProjectId, " + // 🔥 ADD THIS
                        "ap.projectInstanceName, " +
                        "dr.reportDate, " +
                        "dr.issue, " +
                        "u.userName as supervisorName " +
                        "FROM dailyreports dr " +
                        "JOIN assignprojects ap ON dr.assignProjectId = ap.assignProjectId " +
                        "JOIN users u ON dr.supervisorId = u.userId " +
                        "ORDER BY dr.dailyReportId DESC"; // 🔥 Changed to ID DESC for RPT-001, 002 order
            }

            PreparedStatement pstmt = connection.prepareStatement(query);

            if (user != null && user.getUserRole().equals(role.SUPERVISOR.toString())) {
                pstmt.setInt(1, user.getUserId());
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DailyReport report = new DailyReport(
                        rs.getInt("dailyReportId"),
                        rs.getString("projectInstanceName"),
                        rs.getDate("reportDate").toLocalDate(),
                        rs.getString("issue"),
                        rs.getString("supervisorName")
                );

                // 🔥 Assign Project ID ကို ထပ်ထည့်မယ်
                report.setAssignProjectId(rs.getInt("assignProjectId"));

                allReports.add(report);
            }

            // 🔥 Sort by Report ID (RPT-001, 002, 003)
            currentDisplayReports.setAll(allReports);
            rs.close();
            pstmt.close();

            System.out.println("✅ Total reports loaded: " + allReports.size());

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load reports: " + e.getMessage());
        }
    }

    private void displayProjectSidebar() {
        System.out.println("🖥️ Starting to display projects in sidebar...");
        System.out.println("📊 Filtered project list size: " + filteredProjectList.size());

        sidebarProjectList.getChildren().clear();

        if (filteredProjectList.isEmpty()) {
            Label noProjects = new Label("No projects found");
            noProjects.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 20;");
            sidebarProjectList.getChildren().add(noProjects);
            System.out.println("📭 No projects to display");
            return;
        }

        Set<Integer> displayedIds = new HashSet<>();
        int cardCount = 0;

        for (projects project : filteredProjectList) {
            int projectId = project.getAssignProjectId();

            if (displayedIds.contains(projectId)) {
                continue;
            }

            displayedIds.add(projectId);
            cardCount++;

            VBox card = createProjectCard(project);
            sidebarProjectList.getChildren().add(card);

            card.setOnMouseClicked(e -> {
                currentProjectId = project.getAssignProjectId();
                currentProjectName = project.getProjectInstanceName();
                System.out.println("👉 Selected project: " + currentProjectName +
                        " (ID: " + currentProjectId +
                        ", Status: " + project.getProjectStatus() + ")");
                applyFilters();
            });
        }

        System.out.println("✅ Displayed " + cardCount + " unique project cards in sidebar");
    }

    private VBox createProjectCard(projects project) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;" +
                "-fx-padding: 15; -fx-cursor: hand;");

        String projectName = project.getProjectInstanceName();
        String typeName = project.getProjectTypeName();
        String status = project.getProjectStatus();

        Label nameLabel = new Label(projectName != null ? projectName : "Unnamed Project");
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        String detailsText = (typeName != null ? typeName : "Unknown") + " - " +
                (status != null ? status : "Unknown");
        Label detailsLabel = new Label(detailsText);

        String statusColor = getStatusColor(status);
        detailsLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 12px;");

        card.getChildren().addAll(nameLabel, detailsLabel);

        // Add hover effect for better UX
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #3498db; -fx-border-width: 1; -fx-border-radius: 5;" +
                    "-fx-padding: 15; -fx-cursor: hand;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;" +
                    "-fx-padding: 15; -fx-cursor: hand;");
        });

        return card;
    }

    private String getStatusColor(String status) {
        if (status == null) return "#95a5a6";

        String statusLower = status.toLowerCase();
        if (statusLower.contains("planning")) return "#9b59b6";
        if (statusLower.contains("inprogress") || statusLower.contains("ongoing")) return "#3498db";
        if (statusLower.contains("delay")) return "#e67e22";
        if (statusLower.contains("finished") || statusLower.contains("completed")) return "#27ae60";
        if (statusLower.contains("cancel")) return "#e74c3c";
        return "#95a5a6";
    }

    private void filterProjectSidebar() {
        System.out.println("🔍 Filtering project sidebar...");

        String selectedStatus = projectSidebarFilter.getValue();
        System.out.println("   Selected status: " + selectedStatus);

        filteredProjectList.clear();

        if (selectedStatus == null || selectedStatus.equals("All Assigned")) {
            filteredProjectList.setAll(projectList);
            System.out.println("   Showing all " + projectList.size() + " projects");
        } else {
            for (projects project : projectList) {
                if (project.getProjectStatus() != null &&
                        project.getProjectStatus().equals(selectedStatus)) {
                    filteredProjectList.add(project);
                }
            }
            System.out.println("   Showing " + filteredProjectList.size() + " projects with status: " + selectedStatus);
        }

        displayProjectSidebar();
    }

    private void displayAllReports() {
        reportContainer.getChildren().clear();

        if (currentDisplayReports.isEmpty()) {
            Label noData = new Label("No reports found");
            noData.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px; -fx-padding: 20;");
            reportContainer.getChildren().add(noData);
            return;
        }

        // 🔥 Sort reports by Report ID (RPT-001, 002, 003...) in DESCENDING order (newest first)
        List<DailyReport> sortedReports = new ArrayList<>(currentDisplayReports);
        sortedReports.sort((r1, r2) -> Integer.compare(r2.getReportId(), r1.getReportId())); // DESC order

        for (DailyReport report : sortedReports) {
            HBox card = createReportCard(report);
            reportContainer.getChildren().add(card);
        }

        System.out.println("📊 Displayed " + sortedReports.size() + " reports, sorted by Report ID DESC");
    }

    private HBox createReportCard(DailyReport report) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;" +
                "-fx-cursor: hand; -fx-padding: 15;");
        card.setAlignment(Pos.CENTER_LEFT);

        // Left: ID & Date
        VBox left = new VBox(5);
        // 🔥 RPT-001, RPT-002 format နဲ့ပြမယ်
        String reportIdFormatted = String.format("RPT-%03d", report.getReportId());
        Label id = new Label("#" + reportIdFormatted);
        id.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label date = new Label(report.getReportDate().format(dateFormatter));
        date.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");
        left.getChildren().addAll(id, date);

        // Middle: Project & Supervisor
        VBox middle = new VBox(5);
        HBox.setHgrow(middle, Priority.ALWAYS);
        Label project = new Label(report.getProjectName());
        project.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        Label supervisor = new Label("By: " + report.getSupervisorName());
        supervisor.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
        middle.getChildren().addAll(project, supervisor);

        // Issues indicator
        HBox issuesBox = new HBox(5);
        Circle dot = new Circle(4);
        Label issuesLabel = new Label();

        boolean hasIssues = report.getIssues() != null && !report.getIssues().trim().isEmpty();

        if (hasIssues) {
            dot.setFill(Color.RED);
            int count = report.getIssues().split(",").length;
            issuesLabel.setText(count + (count > 1 ? " Issues" : " Issue"));
            issuesLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
        } else {
            dot.setFill(Color.GREEN);
            issuesLabel.setText("No Issues");
            issuesLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
        issuesBox.getChildren().addAll(dot, issuesLabel);

        // Button
        Button viewBtn = new Button("View Details →");
        viewBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #bdc3c7;" +
                "-fx-border-radius: 5; -fx-text-fill: #7f8c8d;");
        viewBtn.setOnAction(e -> showReportDetails(report));

        card.getChildren().addAll(left, middle, issuesBox, new Region(), viewBtn);
        HBox.setHgrow(card.getChildren().get(3), Priority.ALWAYS);

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #3498db; -fx-border-width: 1; -fx-border-radius: 5;" +
                    "-fx-cursor: hand; -fx-padding: 15;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;" +
                    "-fx-cursor: hand; -fx-padding: 15;");
        });

        card.setOnMouseClicked(e -> showReportDetails(report));
        return card;
    }

    private void applyFilters() {
        System.out.println("🎛️ Applying filters...");
        System.out.println("   Current project ID: " + currentProjectId);
        System.out.println("   Current project name: " + currentProjectName);

        List<DailyReport> filtered = new ArrayList<>();

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        String selectedStatus = projectSidebarFilter.getValue();
        System.out.println("   Selected status in filter: " + selectedStatus);

        for (DailyReport report : allReports) {
            // 🔥 1. Project Match - Project ID နဲ့စစ်မယ်
            boolean projectMatch = currentProjectId <= 0 ||
                    report.getAssignProjectId() == currentProjectId;

            // 🔥 2. Date Match
            boolean dateMatch = true;
            if (start != null && end != null && !start.isAfter(end)) {
                dateMatch = !(report.getReportDate().isBefore(start) || report.getReportDate().isAfter(end));
            }

            // 🔥 3. Status Match
            boolean statusMatch = true;
            if (selectedStatus != null && !selectedStatus.equals("All Assigned")) {
                // Status filter က "All Assigned" မဟုတ်ရင် စစ်မယ်
                statusMatch = false;
                for (projects proj : filteredProjectList) {
                    if (proj.getAssignProjectId() == report.getAssignProjectId() &&
                            proj.getProjectStatus() != null &&
                            proj.getProjectStatus().equals(selectedStatus)) {
                        statusMatch = true;
                        break;
                    }
                }
            }

            if (projectMatch && dateMatch && statusMatch) {
                filtered.add(report);
            }
        }

        currentDisplayReports.setAll(filtered);
        displayAllReports();
        updateReportCount();

        System.out.println("✅ After filtering: " + filtered.size() + " reports displayed");

        // Debug info
        if (currentProjectId > 0) {
            int countForThisProject = 0;
            for (DailyReport r : allReports) {
                if (r.getAssignProjectId() == currentProjectId) {
                    countForThisProject++;
                }
            }
            System.out.println("📊 Total reports for this project in database: " + countForThisProject);
        }
    }

    private void resetAllFilters() {
        System.out.println("🔄 Resetting all filters...");

        currentProjectId = 0;
        currentProjectName = "";
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());
        projectSidebarFilter.setValue("All Assigned");

        // Reset project sidebar
        filteredProjectList.setAll(projectList);
        displayProjectSidebar();

        // Reset reports
        currentDisplayReports.setAll(allReports);
        displayAllReports();
        updateReportCount();

        showAlert("Reset Complete", "All filters have been reset. Showing all " + allReports.size() + " reports.");
    }

    private void updateReportCount() {
        if (reportCountLabel != null) {
            int withIssues = 0;

            for (DailyReport r : currentDisplayReports) {
                if (r.getIssues() != null && !r.getIssues().trim().isEmpty()) {
                    withIssues++;
                }
            }

            String filterText = currentProjectId > 0 ? " for " + currentProjectName : "";
            String countText = String.format("Showing %d reports%s - %d with issues",
                    currentDisplayReports.size(), filterText, withIssues);

            reportCountLabel.setText(countText);
        }
    }

    private void showReportDetails(DailyReport report) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Details");

        // 🔥 RPT-001 format နဲ့ပြမယ်
        String reportIdFormatted = String.format("RPT-%03d", report.getReportId());
        alert.setHeaderText(report.getProjectName() + " - " + reportIdFormatted);

        String issues = (report.getIssues() == null || report.getIssues().trim().isEmpty()) ?
                "No issues reported" : report.getIssues();

        alert.setContentText("Report ID: #" + reportIdFormatted +
                "\nSupervisor: " + report.getSupervisorName() +
                "\nDate: " + report.getReportDate().format(dateFormatter) +
                "\nProject ID: " + report.getAssignProjectId() +
                "\nIssues: " + issues);

        alert.getDialogPane().setPrefSize(400, 200);
        alert.showAndWait();
    }

    private void showNewReportDialog() {
        if (user != null && user.getUserRole().equals(role.MANAGER.toString())) {
            showAlert("Access Denied", "Only supervisors can add new daily reports.");
            return;
        }

        showAlert("New Report", "Report creation feature will be implemented here.\n" +
                "Will connect to database to save new daily reports.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to refresh data from database
    public void refreshData() {
        System.out.println("🔄 Manual refresh requested...");
        if (connection != null) {
            loadProjectsFromDatabase();
            loadReportsFromDatabase();
            displayProjectSidebar();
            applyFilters();
            updateReportCount();
            System.out.println("✅ Data refreshed from database");
        } else {
            showAlert("Connection Error", "Cannot refresh data - no database connection.");
        }
    }
}