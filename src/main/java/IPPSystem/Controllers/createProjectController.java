package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Utils.createProjectDraft;
import IPPSystem.Utils.storage;
import IPPSystem.Models.users;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Map;

public class createProjectController extends sideBarPaneController {

    @FXML private Button closeBtn, denyBtn, approveBtn;

    @FXML private TextField instanceNameTxt;
    @FXML private ComboBox<String> siteEngineerBox;

    @FXML private ComboBox<String> projectTypeBox;
    @FXML private ComboBox<String> buildingBox;
    @FXML private ComboBox<String> levelBox;

    @FXML private TextField contractValueTxt;
    @FXML private TextField addressTxt;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField durationTxt;

    private final storage data = storage.getInstance();

    @FXML
    public void initialize() {
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

        // 3) when project type changes, reload building + level by projectTypeId
        projectTypeBox.setOnAction(e -> reloadBuildingAndLevel());

        // if you want default select first
        if (!projectTypeBox.getItems().isEmpty()) {
            projectTypeBox.getSelectionModel().selectFirst();
            reloadBuildingAndLevel();
        }
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
        // just go back to projects list
        openInnerView("viewProjects.fxml");
    }

    @FXML
    private void onDeny() {
        // clear and return
        createProjectDraft.getInstance().clear();
        openInnerView("viewProjects.fxml");
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

            // go to createViewProject
            openInnerView("createViewProject.fxml");

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
}
