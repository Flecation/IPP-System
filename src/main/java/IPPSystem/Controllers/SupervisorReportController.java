package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class SupervisorReportController implements Initializable {

    // Project Information Components
    @FXML private TextField projectNameField;
    @FXML private ComboBox<String> assignedTaskComboBox;
    @FXML private DatePicker reportDatePicker;

    // Weather Condition Components
    @FXML private ComboBox<String> weatherTypeComboBox;
    @FXML private ComboBox<String> workAffectedComboBox;
    @FXML private TextArea weatherNotesTextArea;

    // Labor Information Components
    @FXML private ComboBox<String> laborComboBox;
    @FXML private TextField dailyWageField;
    @FXML private TextField workingHourField;
    @FXML private TextArea laborRemarkTextArea;
    @FXML private Button btnAddLabor;
    @FXML private TableView<LaborReportItem> laborTable;
    @FXML private TableColumn<LaborReportItem, String> colLaborName;
    @FXML private TableColumn<LaborReportItem, String> colSkill;
    @FXML private TableColumn<LaborReportItem, Double> colWage;
    @FXML private TableColumn<LaborReportItem, Double> colHours;
    @FXML private TableColumn<LaborReportItem, String> colRemarks;

    // Material Tracking Components
    @FXML private TextField materialCostField;

    // Project Progress Components
    @FXML private ComboBox<String> currentStageComboBox;
    @FXML private TextField projectWorkHoursField;
    @FXML private ComboBox<String> projectStatusComboBox;
    @FXML private ComboBox<String> riskLevelComboBox;
    @FXML private TextArea projectProgressRemarkTextArea;

    // Issues & Comments Components
    @FXML private TextArea issuesTextArea;
    @FXML private TextArea commentsTextArea;

    // Submit Button
    @FXML private Button submitReportBtn;

    // Data Structures
    private ObservableList<LaborReportItem> laborItems = FXCollections.observableArrayList();
    private HashMap<String, LaborInfo> laborInfoMap = new HashMap<>();
    private HashMap<String, Integer> taskIdMap = new HashMap<>();

    // Current context
    private int currentProjectId = -1;
    private int currentSupervisorId = -1;

    // Static data for dropdowns
    private final String[] WEATHER_TYPES = {
            "Sunny", "Cloudy", "Rainy", "Heavy Rain",
            "Stormy", "Windy", "Foggy", "Snowy", "Extreme Heat"
    };

    private final String[] WORK_AFFECTED = {
            "No Effect", "Minor Delay", "Moderate Delay",
            "Significant Delay", "Work Stopped"
    };

    private final String[] RISK_LEVELS = {
            "Low", "Medium", "High", "Critical"
    };

    private final String[] PROJECT_STATUSES = {
            "In Progress", "Completed", "Delayed"
    };

    // These correspond to your workitems table
    private final String[] PROJECT_STAGES = {
            "Substructure", "Superstructure", "Finishing",
            "MEP", "External"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComponents();
        setupEventHandlers();
        populateDropdowns();
        setupLaborTable();
        setDefaultValues();
    }

    private void setupComponents() {
        // Set default date to today
        reportDatePicker.setValue(LocalDate.now());

        // Add listener for project name typing
        projectNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 2) {
                searchProjectByName(newValue);
            } else {
                assignedTaskComboBox.getItems().clear();
                taskIdMap.clear();
                currentProjectId = -1;
            }
        });
    }

    private void setupEventHandlers() {
        // Add Labor button handler
        btnAddLabor.setOnAction(event -> addLaborToTable());

        // Submit Report button handler
        submitReportBtn.setOnAction(event -> submitDailyReport());

        // Labor selection handler
        laborComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.isEmpty()) {
                        LaborInfo laborInfo = laborInfoMap.get(newValue);
                        if (laborInfo != null) {
                            dailyWageField.setText(String.valueOf(laborInfo.getStandardWage()));
                        }
                    }
                }
        );

        // Assigned task selection handler
        assignedTaskComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.isEmpty() && currentProjectId > 0) {
                        // Task selected, nothing special needed
                    }
                }
        );
    }

    private void populateDropdowns() {
        // Populate weather types
        weatherTypeComboBox.getItems().addAll(WEATHER_TYPES);

        // Populate work affected options
        workAffectedComboBox.getItems().addAll(WORK_AFFECTED);

        // Populate risk levels
        riskLevelComboBox.getItems().addAll(RISK_LEVELS);

        // Populate project statuses
        projectStatusComboBox.getItems().addAll(PROJECT_STATUSES);

        // Populate project stages (work items)
        currentStageComboBox.getItems().addAll(PROJECT_STAGES);

        // Load labors from database using direct query
        loadLaborsFromDatabase();
    }

    private void setDefaultValues() {
        // Set default values for dropdowns
        if (!weatherTypeComboBox.getItems().isEmpty()) {
            weatherTypeComboBox.getSelectionModel().select(0);
        }

        if (!workAffectedComboBox.getItems().isEmpty()) {
            workAffectedComboBox.getSelectionModel().select(0);
        }

        if (!riskLevelComboBox.getItems().isEmpty()) {
            riskLevelComboBox.getSelectionModel().select(0);
        }

        if (!projectStatusComboBox.getItems().isEmpty()) {
            projectStatusComboBox.getSelectionModel().select(0);
        }

        if (!currentStageComboBox.getItems().isEmpty()) {
            currentStageComboBox.getSelectionModel().select(0);
        }
    }

    private void searchProjectByName(String searchText) {
        assignedTaskComboBox.getItems().clear();
        taskIdMap.clear();

        try {
            // Get all projects from database
            ArrayList<projects> allProjects = database.getAllProjects();

            for (projects project : allProjects) {
                String projectName = project.getProjectInstanceName();
                if (projectName != null && projectName.toLowerCase().contains(searchText.toLowerCase())) {
                    currentProjectId = project.getAssignProjectId();
                    projectNameField.setText(projectName);
                    loadTasksForProject(currentProjectId);
                    return; // Found first match
                }
            }

            // If no project found, clear and show message
            projectNameField.setStyle("-fx-border-color: red;");
            currentProjectId = -1;

        } catch (Exception e) {
            showAlert("Error", "Failed to search projects: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadTasksForProject(int projectId) {
        try {
            // Get all work items for this project
            ArrayList<workItems> workItems = database.getAllWorkItemsByAssignProject(projectId);

            for (workItems workItem : workItems) {
                // Get all tasks for this work item
                ArrayList<tasks> tasks = database.getAllTasksByAssignWorkItem(workItem.getAssignWorkItemId());

                for (tasks task : tasks) {
                    String displayName = workItem.getWorkItemName() + " - " + task.getTaskName();
                    assignedTaskComboBox.getItems().add(displayName);
                    taskIdMap.put(displayName, task.getAssignTaskId());
                }
            }

            if (!assignedTaskComboBox.getItems().isEmpty()) {
                assignedTaskComboBox.getSelectionModel().selectFirst();
                projectNameField.setStyle("-fx-border-color: green;");
            }

        } catch (Exception e) {
            showAlert("Error", "Failed to load tasks: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadLaborsFromDatabase() {
        try {
            // Get all labors from database using the direct method
            ArrayList<labors> allLabors = database.getAllLabors();

            // Clear existing data
            laborComboBox.getItems().clear();
            laborInfoMap.clear();

            // Populate labor combo box
            for (labors labor : allLabors) {
                if (labor.getLaborName() != null && !labor.getLaborName().isEmpty()) {
                    String displayName = labor.getLaborName() + " (" + labor.getLaborNRC() + ")";
                    laborComboBox.getItems().add(displayName);

                    // Get skill name
                    String skillName = labor.getSkillName();
                    if (skillName == null || skillName.isEmpty()) {
                        skillName = "General Laborer";
                    }

                    // Calculate standard wage based on skill
                    double standardWage = calculateStandardWage(skillName);

                    // Store labor info
                    laborInfoMap.put(displayName, new LaborInfo(
                            labor.getLaborId(),
                            labor.getLaborName(),
                            skillName,
                            standardWage
                    ));
                }
            }

        } catch (Exception e) {
            showAlert("Error", "Failed to load labors: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private double calculateStandardWage(String skillName) {
        // Standard daily wages in MMK based on skill
        switch (skillName) {
            case "General Laborer": return 8000;
            case "Mason": return 15000;
            case "Carpenter": return 12000;
            case "Electrician": return 18000;
            case "Plumber": return 16000;
            case "Welder": return 17000;
            case "Steel Fixer": return 14000;
            case "Concrete Finisher": return 13000;
            case "Heavy Equipment Operator": return 25000;
            case "Foreman/Supervisor": return 30000;
            case "Surveyor": return 20000;
            case "Scaffolder": return 9000;
            case "Tile Setter": return 14000;
            case "Painter": return 11000;
            case "Plasterer": return 12000;
            case "HVAC Technician": return 19000;
            case "Glazier": return 15000;
            case "Roofer": return 13000;
            case "Landscaper": return 10000;
            case "Paving Specialist": return 16000;
            case "Bridge Specialist": return 22000;
            case "Pipe Layer": return 14000;
            case "Dam Construction Specialist": return 25000;
            case "Religious Art Specialist": return 20000;
            default: return 10000;
        }
    }

    private void setupLaborTable() {
        // Initialize table columns
        colLaborName.setCellValueFactory(new PropertyValueFactory<>("laborName"));
        colSkill.setCellValueFactory(new PropertyValueFactory<>("skill"));
        colWage.setCellValueFactory(new PropertyValueFactory<>("dailyWage"));
        colHours.setCellValueFactory(new PropertyValueFactory<>("workHours"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remark"));

        // Set table data
        laborTable.setItems(laborItems);

        // Add context menu for removing items
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Remove Labor");
        removeItem.setOnAction(event -> {
            LaborReportItem selected = laborTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                laborItems.remove(selected);
            }
        });
        contextMenu.getItems().add(removeItem);
        laborTable.setContextMenu(contextMenu);
    }

    private void addLaborToTable() {
        try {
            // Get values from input fields
            String selectedLabor = laborComboBox.getValue();
            String wageText = dailyWageField.getText();
            String hoursText = workingHourField.getText();
            String remark = laborRemarkTextArea.getText();

            // Validate inputs
            if (selectedLabor == null || selectedLabor.isEmpty()) {
                showAlert("Validation Error", "Please select a labor", Alert.AlertType.WARNING);
                return;
            }

            if (wageText.isEmpty() || hoursText.isEmpty()) {
                showAlert("Validation Error", "Please enter wage and hours", Alert.AlertType.WARNING);
                return;
            }

            double wage = Double.parseDouble(wageText);
            double hours = Double.parseDouble(hoursText);

            if (wage <= 0 || hours <= 0) {
                showAlert("Validation Error", "Wage and hours must be positive numbers", Alert.AlertType.WARNING);
                return;
            }

            // Get labor info
            LaborInfo laborInfo = laborInfoMap.get(selectedLabor);
            if (laborInfo == null) {
                showAlert("Error", "Labor information not found", Alert.AlertType.ERROR);
                return;
            }

            // Check if labor is already in the table
            for (LaborReportItem item : laborItems) {
                if (item.getLaborId() == laborInfo.getLaborId()) {
                    showAlert("Duplicate Labor", "This labor is already added to the report", Alert.AlertType.WARNING);
                    return;
                }
            }

            // Extract labor name (remove NRC from display)
            String laborName = selectedLabor.split("\\(")[0].trim();

            // Create labor report item
            LaborReportItem laborItem = new LaborReportItem(
                    laborInfo.getLaborId(),
                    laborName,
                    laborInfo.getSkillName(),
                    wage,
                    hours,
                    remark
            );

            // Add to table
            laborItems.add(laborItem);

            // Clear input fields
            laborComboBox.getSelectionModel().clearSelection();
            dailyWageField.clear();
            workingHourField.clear();
            laborRemarkTextArea.clear();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for wage and hours", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to add labor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void submitDailyReport() {
        try {
            // Validate required fields
            if (currentProjectId <= 0) {
                showAlert("Validation Error", "Please select a valid project by typing the project name", Alert.AlertType.ERROR);
                return;
            }

            if (reportDatePicker.getValue() == null) {
                showAlert("Validation Error", "Please select report date", Alert.AlertType.ERROR);
                return;
            }

            if (laborItems.isEmpty()) {
                showAlert("Validation Error", "Please add at least one labor", Alert.AlertType.WARNING);
                return;
            }

            if (assignedTaskComboBox.getValue() == null || assignedTaskComboBox.getValue().isEmpty()) {
                showAlert("Validation Error", "Please select an assigned task", Alert.AlertType.WARNING);
                return;
            }

            // Collect report data
            Date reportDate = Date.valueOf(reportDatePicker.getValue());
            String weather = weatherTypeComboBox.getValue();
            String workAffected = workAffectedComboBox.getValue();
            String weatherNotes = weatherNotesTextArea.getText();
            String generalRemark = commentsTextArea.getText();
            String issue = issuesTextArea.getText();

            String projectStage = currentStageComboBox.getValue();
            String projectStatus = projectStatusComboBox.getValue();
            String riskLevel = riskLevelComboBox.getValue();
            String progressRemark = projectProgressRemarkTextArea.getText();

            // Get selected task ID
            int selectedTaskId = taskIdMap.get(assignedTaskComboBox.getValue());

            // Calculate material cost
            double materialCost = 0.0;
            if (!materialCostField.getText().isEmpty()) {
                materialCost = Double.parseDouble(materialCostField.getText());
            }

            // Calculate total project work hours
            double totalProjectHours = 0.0;
            if (!projectWorkHoursField.getText().isEmpty()) {
                totalProjectHours = Double.parseDouble(projectWorkHoursField.getText());
            }

            // Combine weather information
            String fullWeatherInfo = weather;
            if (workAffected != null && !workAffected.equals("No Effect")) {
                fullWeatherInfo += " - " + workAffected;
            }
            if (weatherNotes != null && !weatherNotes.isEmpty()) {
                fullWeatherInfo += " | Notes: " + weatherNotes;
            }

            // Combine progress information
            String progressDescription = String.format(
                    "Stage: %s | Overall Status: %s | Risk Level: %s | Total Hours: %.1f\n%s",
                    projectStage, projectStatus, riskLevel, totalProjectHours, progressRemark
            );

            // Create a DailyReportModel object
            DailyReportModel dailyReport = new DailyReportModel();
            dailyReport.setAssignProjectId(currentProjectId);
            dailyReport.setReportDate(reportDate);
            dailyReport.setSupervisorId(currentSupervisorId);
            dailyReport.setWeather(fullWeatherInfo);
            dailyReport.setGeneralRemark(generalRemark);
            dailyReport.setIssue(issue);

            // Save to database
            int dailyReportId = database.saveDailyReport(dailyReport);

            if (dailyReportId > 0) {
                // Save labor reports
                boolean allLaborsSaved = true;
                for (LaborReportItem labor : laborItems) {
                    DailyReportLaborModel laborReport = new DailyReportLaborModel();
                    laborReport.setDailyReportId(dailyReportId);
                    laborReport.setLaborId(labor.getLaborId());
                    laborReport.setWorkHours(labor.getWorkHours());
                    laborReport.setDailyWage(labor.getDailyWage());
                    laborReport.setRemark(labor.getRemark());

                    if (!database.saveDailyReportLabor(laborReport)) {
                        allLaborsSaved = false;
                    }
                }

                // Save task report
                DailyReportTaskModel taskReport = new DailyReportTaskModel();
                taskReport.setDailyReportId(dailyReportId);
                taskReport.setAssignTaskId(selectedTaskId);
                taskReport.setProgressDescription(progressDescription);
                taskReport.setWorkHours(totalProjectHours);
                taskReport.setCompletedQty(0); // You might want to get this from the form
                taskReport.setCompleted(projectStatus.equals("Completed"));

                boolean taskSaved = database.saveDailyReportTask(taskReport);

                if (allLaborsSaved && taskSaved) {
                    showAlert("Success", "Daily report saved successfully! Report ID: " + dailyReportId, Alert.AlertType.INFORMATION);
                    showReportSummary(dailyReport, materialCost, totalProjectHours, selectedTaskId);
                    clearForm();
                } else {
                    showAlert("Partial Success", "Report saved but some details might not have been saved correctly.", Alert.AlertType.WARNING);
                }
            } else {
                showAlert("Error", "Failed to save daily report", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for cost and hours", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Submission Error", "Failed to submit report: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showReportSummary(DailyReportModel report, double materialCost, double totalHours, int taskId) {
        StringBuilder summary = new StringBuilder();
        summary.append("DAILY SITE REPORT SUMMARY\n");
        summary.append("=========================\n");
        summary.append("Project: ").append(projectNameField.getText()).append("\n");
        summary.append("Date: ").append(report.getReportDate()).append("\n");
        summary.append("Weather: ").append(report.getWeather()).append("\n");
        summary.append("Task: ").append(assignedTaskComboBox.getValue()).append("\n");
        summary.append("Material Cost: ").append(materialCost).append(" MMK\n");
        summary.append("Total Project Hours: ").append(totalHours).append("\n");
        summary.append("\nLABOR DETAILS:\n");

        double totalLaborCost = 0;
        for (LaborReportItem labor : laborItems) {
            double laborCost = labor.getDailyWage() * (labor.getWorkHours() / 8.0); // Assuming 8-hour day
            totalLaborCost += laborCost;
            summary.append(String.format("- %s (%s): %.1f hrs @ %,.0f MMK = %,.0f MMK - %s\n",
                    labor.getLaborName(), labor.getSkill(), labor.getWorkHours(),
                    labor.getDailyWage(), laborCost, labor.getRemark()));
        }

        summary.append("\nTOTAL LABOR COST: ").append(totalLaborCost).append(" MMK\n");
        summary.append("TOTAL ESTIMATED COST: ").append(materialCost + totalLaborCost).append(" MMK\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Summary");
        alert.setHeaderText("Daily Site Report Summary");
        alert.setContentText(summary.toString());
        alert.showAndWait();
    }

    private void clearForm() {
        // Clear all input fields
        projectNameField.clear();
        projectNameField.setStyle("");
        assignedTaskComboBox.getItems().clear();
        weatherTypeComboBox.getSelectionModel().clearSelection();
        workAffectedComboBox.getSelectionModel().clearSelection();
        weatherNotesTextArea.clear();
        laborItems.clear();
        laborComboBox.getSelectionModel().clearSelection();
        dailyWageField.clear();
        workingHourField.clear();
        laborRemarkTextArea.clear();
        materialCostField.clear();
        currentStageComboBox.getSelectionModel().clearSelection();
        projectWorkHoursField.clear();
        projectStatusComboBox.getSelectionModel().clearSelection();
        riskLevelComboBox.getSelectionModel().clearSelection();
        projectProgressRemarkTextArea.clear();
        issuesTextArea.clear();
        commentsTextArea.clear();

        // Reset date to today
        reportDatePicker.setValue(LocalDate.now());

        // Clear maps
        taskIdMap.clear();
        currentProjectId = -1;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Methods to set context data from main controller
    public void setProjectData(int projectId, String projectName) {
        this.currentProjectId = projectId;
        this.projectNameField.setText(projectName);
        loadTasksForProject(projectId);
    }

    public void setSupervisorId(int supervisorId) {
        this.currentSupervisorId = supervisorId;
    }

    // Helper classes
    private static class LaborInfo {
        private int laborId;
        private String laborName;
        private String skillName;
        private double standardWage;

        public LaborInfo(int laborId, String laborName, String skillName, double standardWage) {
            this.laborId = laborId;
            this.laborName = laborName;
            this.skillName = skillName;
            this.standardWage = standardWage;
        }

        public int getLaborId() { return laborId; }
        public String getLaborName() { return laborName; }
        public String getSkillName() { return skillName; }
        public double getStandardWage() { return standardWage; }
    }

    // Model class for labor table items
    public static class LaborReportItem {
        private int laborId;
        private String laborName;
        private String skill;
        private double dailyWage;
        private double workHours;
        private String remark;

        public LaborReportItem(int laborId, String laborName, String skill,
                               double dailyWage, double workHours, String remark) {
            this.laborId = laborId;
            this.laborName = laborName;
            this.skill = skill;
            this.dailyWage = dailyWage;
            this.workHours = workHours;
            this.remark = remark;
        }

        // Getters (required for TableView PropertyValueFactory)
        public int getLaborId() { return laborId; }
        public String getLaborName() { return laborName; }
        public String getSkill() { return skill; }
        public double getDailyWage() { return dailyWage; }
        public double getWorkHours() { return workHours; }
        public String getRemark() { return remark; }
    }
}