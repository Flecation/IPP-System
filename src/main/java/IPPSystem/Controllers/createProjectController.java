package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Utils.createProjectDraft;
import IPPSystem.Utils.loadPaneAware;
import IPPSystem.Utils.AddOverlayForm;
import IPPSystem.Utils.messageBoxService;
import IPPSystem.Constants.notificationType;
import IPPSystem.Utils.storage;
import IPPSystem.Utils.utils;
import IPPSystem.Models.users;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Map;

public class createProjectController implements loadPaneAware, AddOverlayForm {

    private javafx.scene.layout.StackPane loadPane;


    @FXML private Button closeBtn, denyBtn, approveBtn;

    @FXML private TextField instanceNameTxt;
    @FXML private Label instanceNameLbl;
    @FXML private ComboBox<String> siteEngineerBox;

    @FXML private ComboBox<String> projectTypeBox;
    @FXML private ComboBox<String> buildingBox;
    @FXML private ComboBox<String> levelBox;

    @FXML private TextField contractValueTxt;
    @FXML private Label contractValueLbl;
    @FXML private TextField addressTxt;
    @FXML private Label addressLbl;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField durationTxt;
    @FXML private Label durationLbl;
    @FXML private ComboBox<String> durationUnitCombo;

    private final storage data = storage.getInstance();

    @Override
    public void setLoadPane(javafx.scene.layout.StackPane loadPane) {
        this.loadPane = loadPane;
    }

    private sideBarPaneController parent() {
        // Resolve per-tab loadPane dynamically so this controller can work without injection
        javafx.scene.layout.StackPane lp = (loadPane != null) ? loadPane : utils.findTabLoadPane(closeBtn);
        if (lp == null) return null;

        Object p = lp.getProperties().get("SIDEBAR_CONTROLLER");
        return (p instanceof sideBarPaneController) ? (sideBarPaneController) p : null;
    }


    @FXML
    public void initialize() {
        // ===== floating label style (StackPane label + textfield) =====
        // (your utils method name is setFloatTextFieldStyle)
        if (instanceNameLbl != null && instanceNameTxt != null) utils.setFloatTextFieldStyle(instanceNameLbl, instanceNameTxt);
        if (contractValueLbl != null && contractValueTxt != null) utils.setFloatTextFieldStyle(contractValueLbl, contractValueTxt);
        if (addressLbl != null && addressTxt != null) utils.setFloatTextFieldStyle(addressLbl, addressTxt);
        if (durationLbl != null && durationTxt != null) utils.setFloatTextFieldStyle(durationLbl, durationTxt);

        // 1) supervisors list (your DB has getAllSupervisors)
        siteEngineerBox.getItems().clear();
        for (users u : database.getAllSupervisors()) {
            if (u != null && u.getUserName() != null) siteEngineerBox.getItems().add(u.getUserName());
        }

        // 2) project types
        projectTypeBox.getItems().clear();
        for (String t : data.getProjectTypes().values()) {
            if (t != null) projectTypeBox.getItems().add(t);
        }

        // duration unit (Day/Month/Year)
        if (durationUnitCombo != null) {
            durationUnitCombo.getItems().setAll("Day", "Month", "Year");
            durationUnitCombo.getSelectionModel().select("Day");
        }

        // Initially: user must choose project type first
        buildingBox.getItems().clear();
        levelBox.getItems().clear();
        buildingBox.setDisable(true);
        levelBox.setDisable(true);

        // 3) when project type changes, enable + reload building & level by projectTypeId
        projectTypeBox.setOnAction(e -> {
            String v = projectTypeBox.getValue();
            boolean hasType = v != null && !v.trim().isEmpty();
            buildingBox.setDisable(!hasType);
            levelBox.setDisable(!hasType);
            if (hasType) {
                reloadBuildingAndLevel();
            } else {
                buildingBox.getItems().clear();
                levelBox.getItems().clear();
            }
        });
    }

    private void reloadBuildingAndLevel() {
        buildingBox.getItems().clear();
        levelBox.getItems().clear();

        Integer typeId = findKeyByValue(data.getProjectTypes(), projectTypeBox.getValue());
        if (typeId == null) return;

        Map<Integer, String> buildings = database.getAllBuildingByProjectTypeId(typeId);
        for (String b : buildings.values()) if (b != null) buildingBox.getItems().add(b);

        Map<Integer, String> levels = database.getAllLevelByProjectTypeId(typeId);
        for (String l : levels.values()) if (l != null) levelBox.getItems().add(l);

        if (!buildingBox.getItems().isEmpty()) buildingBox.getSelectionModel().selectFirst();
        if (!levelBox.getItems().isEmpty()) levelBox.getSelectionModel().selectFirst();
    }

    private Integer findKeyByValue(Map<Integer, String> map, String value) {
        if (value == null) return null;
        for (var e : map.entrySet()) {
            if (e.getValue() != null && e.getValue().equalsIgnoreCase(value.trim())) return e.getKey();
        }
        return null;
    }

    @FXML
    private void onClose() {
        sideBarPaneController p = parent();
        if (p != null) {
            p.closeAddOverlay();
        }
    }

    @FXML
    private void onDeny() {
        createProjectDraft.getInstance().clear();
        sideBarPaneController p = parent();
        if (p != null) {
            p.closeAddOverlay();
        }
    }

    @FXML
    private void onApprove() {
        try {
            // validate
            String instanceName = req(instanceNameTxt.getText(), "Instance Name");
            String supervisor = req(siteEngineerBox.getValue(), "Site Engineer");
            String type = req(projectTypeBox.getValue(), "Project Type");
            String building = req(buildingBox.getValue(), "Building");
            String level = req(levelBox.getValue(), "Finishing Level");

            String address = req(addressTxt.getText(), "Address");
            double contract = parseDouble(req(contractValueTxt.getText(), "Contract Value"), "Contract Value");

            LocalDate s = startDatePicker.getValue();
            LocalDate e = endDatePicker.getValue();
            if (s == null) throw new IllegalArgumentException("Planned Start Date is required.");
            if (e == null) throw new IllegalArgumentException("Planned End Date is required.");
            if (e.isBefore(s)) throw new IllegalArgumentException("End date must be after start date.");

            double duration = parseDouble(req(durationTxt.getText(), "Duration"), "Duration");
            String durationUnit = (durationUnitCombo == null) ? null : durationUnitCombo.getValue();
            if (durationUnit == null || durationUnit.trim().isEmpty()) {
                throw new IllegalArgumentException("Duration Unit is required.");
            }

            // store draft
            createProjectDraft d = createProjectDraft.getInstance();
            d.instanceName = instanceName;
            d.supervisorName = supervisor;
            d.projectTypeName = type;
            d.buildingName = building;
            d.levelName = level;
            d.address = address;
            d.contractValue = contract;
            d.startDate = s;
            d.endDate = e;
            d.duration = duration;
            // NOTE: if your createProjectDraft has a durationUnit field, uncomment and use it.
            // d.durationUnit = durationUnit;

            // go to createViewProject
            sideBarPaneController p = parent();
            if (p != null) {
                p.openAddOverlay("createViewProject.fxml");
            }

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private String req(String v, String field) {
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        return v.trim();
    }

    private double parseDouble(String v, String field) {
        try { return Double.parseDouble(v.trim()); }
        catch (Exception e) { throw new IllegalArgumentException(field + " must be a number."); }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    // ===== AddOverlayForm =====
    @Override
    public boolean hasUnsavedChanges() {
        return (instanceNameTxt != null && instanceNameTxt.getText() != null && !instanceNameTxt.getText().trim().isEmpty())
                || (siteEngineerBox != null && siteEngineerBox.getValue() != null)
                || (projectTypeBox != null && projectTypeBox.getValue() != null)
                || (buildingBox != null && buildingBox.getValue() != null)
                || (levelBox != null && levelBox.getValue() != null)
                || (contractValueTxt != null && contractValueTxt.getText() != null && !contractValueTxt.getText().trim().isEmpty())
                || (addressTxt != null && addressTxt.getText() != null && !addressTxt.getText().trim().isEmpty())
                || (startDatePicker != null && startDatePicker.getValue() != null)
                || (endDatePicker != null && endDatePicker.getValue() != null);
    }

    @Override
    public boolean isFormValid() {
        return instanceNameTxt != null && instanceNameTxt.getText() != null && !instanceNameTxt.getText().trim().isEmpty()
                && siteEngineerBox != null && siteEngineerBox.getValue() != null
                && projectTypeBox != null && projectTypeBox.getValue() != null
                && buildingBox != null && buildingBox.getValue() != null
                && levelBox != null && levelBox.getValue() != null
                && contractValueTxt != null && contractValueTxt.getText() != null && !contractValueTxt.getText().trim().isEmpty()
                && addressTxt != null && addressTxt.getText() != null && !addressTxt.getText().trim().isEmpty()
                && startDatePicker != null && startDatePicker.getValue() != null
                && endDatePicker != null && endDatePicker.getValue() != null
                && durationTxt != null && durationTxt.getText() != null && !durationTxt.getText().trim().isEmpty()
                && durationUnitCombo != null && durationUnitCombo.getValue() != null;
    }

    @Override
    public String getValidationMessage() {
        return "Please fill all required fields before closing (Instance Name, Site Engineer, Type, Building, Level, Contract Value, Address, Start/End Dates, Duration, Duration Unit).";
    }
}
