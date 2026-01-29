package IPPSystem.Controllers;

import IPPSystem.DAO.reportDatabase;
import IPPSystem.Models.DailyReport;
import IPPSystem.Models.ReportLabor;
import IPPSystem.Utils.session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateReportNewController {

    // FXML fields
    @FXML private TextField projectNameField;
    @FXML private DatePicker reportDatePicker;
    @FXML private ComboBox<String> weatherTypeComboBox;
    @FXML private ComboBox<String> workAffectedComboBox;
    @FXML private TextArea weatherNotesArea;

    @FXML private TableView<ReportLabor> laborTable;
    @FXML private TableColumn<ReportLabor, String> colId;
    @FXML private TableColumn<ReportLabor, String> colSkill;
    @FXML private TableColumn<ReportLabor, Double> colWage;
    @FXML private TableColumn<ReportLabor, Double> colHours;
    @FXML private TableColumn<ReportLabor, String> colRemarks;

    @FXML private TextField laborNameField;
    @FXML private ComboBox<String> laborSkillComboBox;
    @FXML private TextField laborWageField;
    @FXML private TextField laborHoursField;
    @FXML private Button btnAddLabor;
    @FXML private Button submitReportBtn;

    @FXML private TextArea issuesTextArea;
    @FXML private TextArea commentsTextArea;

    @FXML private ComboBox<String> projectComboBox;

    private ObservableList<ReportLabor> laborList;
    private int currentSupervisorId;
    private String currentUserRole;

    @FXML
    public void initialize() {
        System.out.println("CreateReportNewController initialized");

        // Initialize labor list and table
        laborList = FXCollections.observableArrayList();
        initializeLaborTable();

        // Set current date
        reportDatePicker.setValue(LocalDate.now());

        // Populate combo boxes
        populateComboBoxes();

        // Setup button actions
        setupButtonActions();

        // Get current user from session
        if (session.getInstance().getUser() != null) {
            currentSupervisorId = session.getInstance().getUser().getUserId();
            currentUserRole = session.getInstance().getUser().getUserRole();
            System.out.println("Current Supervisor ID: " + currentSupervisorId);

            // Load projects for this supervisor
            loadProjectsForSupervisor();
        }
    }

    private void initializeLaborTable() {
        if (laborTable != null) {
            laborTable.setItems(laborList);

            // Set up cell value factories
            colId.setCellValueFactory(new PropertyValueFactory<>("name"));
            colSkill.setCellValueFactory(new PropertyValueFactory<>("skill"));
            colWage.setCellValueFactory(new PropertyValueFactory<>("wage"));
            colHours.setCellValueFactory(new PropertyValueFactory<>("hours"));

            // For total column (calculated)
            colRemarks.setCellValueFactory(cellData -> {
                ReportLabor labor = cellData.getValue();
                double total = labor.getWage() * labor.getHours();
                return new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", total));
            });
        }
    }

    private void populateComboBoxes() {
        // Weather types
        ObservableList<String> weatherTypes = FXCollections.observableArrayList(
                "Sunny", "Cloudy", "Rainy", "Stormy", "Windy", "Foggy", "Clear"
        );
        if (weatherTypeComboBox != null) {
            weatherTypeComboBox.setItems(weatherTypes);
            weatherTypeComboBox.setValue("Sunny");
        }

        // Work affect
        ObservableList<String> workAffect = FXCollections.observableArrayList(
                "Normal", "Slightly Affected", "Moderately Affected", "Heavily Affected", "Work Stopped"
        );
        if (workAffectedComboBox != null) {
            workAffectedComboBox.setItems(workAffect);
            workAffectedComboBox.setValue("Normal");
        }

        // Labor skills
        ObservableList<String> skills = FXCollections.observableArrayList(
                "General Worker", "Carpenter", "Electrician", "Plumber", "Mason", "Welder", "Operator", "Foreman"
        );
        if (laborSkillComboBox != null) {
            laborSkillComboBox.setItems(skills);
            laborSkillComboBox.setValue("General Worker");
        }
    }

    private void loadProjectsForSupervisor() {
        if (projectComboBox != null && currentSupervisorId > 0) {
            ArrayList<String> projects = reportDatabase.getProjectListForSupervisor(currentSupervisorId);
            projectComboBox.setItems(FXCollections.observableArrayList(projects));
            if (!projects.isEmpty()) {
                projectComboBox.setValue(projects.get(0));
            }
        }
    }

    private void setupButtonActions() {
        // Add Labor button
        if (btnAddLabor != null) {
            btnAddLabor.setOnAction(e -> addLabor());
        }

        // Submit Report button
        if (submitReportBtn != null) {
            submitReportBtn.setOnAction(e -> submitReport());
        }
    }

    @FXML
    private void addLabor() {
        try {
            String name = laborNameField.getText().trim();
            String skill = laborSkillComboBox.getValue();
            String wageText = laborWageField.getText().trim();
            String hoursText = laborHoursField.getText().trim();

            if (name.isEmpty() || skill == null || wageText.isEmpty() || hoursText.isEmpty()) {
                showAlert("Error", "Missing Information", "Please fill all labor fields");
                return;
            }

            double wage = Double.parseDouble(wageText);
            double hours = Double.parseDouble(hoursText);

            if (wage <= 0 || hours <= 0) {
                showAlert("Error", "Invalid Input", "Wage and hours must be positive numbers");
                return;
            }

            double total = wage * hours;
            ReportLabor labor = new ReportLabor(name, skill, wage, hours, total);
            laborList.add(labor);

            // Clear fields
            laborNameField.clear();
            laborSkillComboBox.setValue("General Worker");
            laborWageField.clear();
            laborHoursField.clear();

            System.out.println("Added labor: " + name);

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Input", "Please enter valid numbers for wage and hours");
        }
    }

    @FXML
    private void submitReport() {
        // Validate required fields
        if (projectComboBox.getValue() == null || projectComboBox.getValue().isEmpty()) {
            showAlert("Error", "Missing Information", "Please select a project");
            return;
        }

        if (reportDatePicker.getValue() == null) {
            showAlert("Error", "Missing Information", "Please select report date");
            return;
        }

        // Get project ID
        int projectId = reportDatabase.getProjectIdByName(projectComboBox.getValue(), currentSupervisorId);
        if (projectId == -1) {
            showAlert("Error", "Project Error", "Could not find selected project");
            return;
        }

        // Create DailyReport object
        DailyReport report = new DailyReport(
                projectId,
                reportDatePicker.getValue(),
                weatherTypeComboBox.getValue(),
                workAffectedComboBox.getValue(),
                weatherNotesArea.getText(),
                issuesTextArea.getText(),
                commentsTextArea.getText(),
                currentSupervisorId
        );

        // Convert laborList to List
        List<ReportLabor> labors = new ArrayList<>(laborList);

        // Save to database
        boolean success = reportDatabase.createReport(report, labors);

        if (success) {
            showAlert("Success", "Report Submitted", "Daily report has been submitted successfully");

            // Go back to report view
            goBackToReportView();
        } else {
            showAlert("Error", "Submission Failed", "Failed to submit report. Please try again.");
        }
    }

    private void goBackToReportView() {
        try {
            // Load SupervisorReport view
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/View/SupervisorReport.fxml"));
            javafx.scene.Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) submitReportBtn.getScene().getWindow();
            javafx.scene.Scene scene = stage.getScene();

            // Find and replace in loadPane
            javafx.scene.layout.BorderPane borderPane = (javafx.scene.layout.BorderPane) scene.lookup("#basePane");
            if (borderPane != null) {
                javafx.scene.layout.StackPane loadPane = (javafx.scene.layout.StackPane) borderPane.lookup("#loadPane");
                if (loadPane != null) {
                    loadPane.getChildren().setAll(root);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Setter for supervisor ID
    public void setSupervisorId(int supervisorId) {
        this.currentSupervisorId = supervisorId;
        loadProjectsForSupervisor();
    }

    // Setter for user role
    public void setCurrentUserRole(String userRole) {
        this.currentUserRole = userRole;
    }
}