package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.DAO.databaseConnection;
import IPPSystem.Interfaces.AddOverlayForm;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Models.users;
import IPPSystem.Utils.createProjectDraft;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * createProjectController
 *
 * Requirements implemented:
 * 1) Project Type MUST be selected first.
 *    - buildingBox & levelBox are disabled until a type is selected.
 * 2) NO repeated DB calls for filtering.
 *    - We load ALL needed lookup data once in initialize():
 *         - project types, buildings, levels
 *         - valid combinations from projectDetails table
 *    - After that, we only FILTER in-memory.
 * 3) building & level are linked:
 *    - After type selected: building list & level list show only values available for that type.
 *    - Selecting building filters levels; selecting level filters buildings (still under same type).
 * 4) Digits-only input for numeric fields, but stored as double.
 * 5) Duration DAYS-only with auto sync between start/end/duration.
 */
public class createProjectController implements loadPaneAware, AddOverlayForm {

    private javafx.scene.layout.StackPane loadPane;

    // ===== Buttons =====
    @FXML private Button closeBtn, denyBtn, approveBtn;

    // ===== Fields =====
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

    // Optional numeric fields (won't crash if missing in FXML)
    @FXML private TextField areaTxt;
    @FXML private Label areaLbl;
    @FXML private TextField unitTxt;
    @FXML private Label unitLbl;
    @FXML private TextField storyTxt;
    @FXML private Label storyLbl;
    @FXML private TextField heightTxt;
    @FXML private Label heightLbl;

    // ===== state =====
    private boolean syncingDates = false;
    private boolean updatingCombos = false;

    // Lookups loaded once
    private final Map<Integer, String> typeIdToName = new HashMap<>();
    private final Map<String, Integer> typeNameToId = new HashMap<>();

    private final Map<Integer, String> buildingIdToName = new HashMap<>();
    private final Map<String, Integer> buildingNameToId = new HashMap<>();

    private final Map<Integer, String> levelIdToName = new HashMap<>();
    private final Map<String, Integer> levelNameToId = new HashMap<>();

    /** Valid combos loaded once from projectDetails: (typeId, buildingId, levelId) */
    private final Set<Combo> validCombos = new HashSet<>();

    private static final class Combo {
        final int typeId;
        final int buildingId;
        final int levelId;

        Combo(int typeId, int buildingId, int levelId) {
            this.typeId = typeId;
            this.buildingId = buildingId;
            this.levelId = levelId;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Combo c)) return false;
            return typeId == c.typeId && buildingId == c.buildingId && levelId == c.levelId;
        }

        @Override public int hashCode() {
            return Objects.hash(typeId, buildingId, levelId);
        }
    }

    @Override
    public void setLoadPane(javafx.scene.layout.StackPane loadPane) {
        this.loadPane = loadPane;
    }

    private sideBarPaneController parent() {
        javafx.scene.layout.StackPane lp = loadPane;
        if (lp == null) lp = utils.findTabLoadPane(closeBtn);
        if (lp == null) return null;

        Object p = lp.getProperties().get("SIDEBAR_CONTROLLER");
        return (p instanceof sideBarPaneController sb) ? sb : null;
    }

    @FXML
    public void initialize() {

        // Float labels
        if (instanceNameLbl != null && instanceNameTxt != null) utils.setFloatTextFieldStyle(instanceNameLbl, instanceNameTxt);
        if (contractValueLbl != null && contractValueTxt != null) utils.setFloatTextFieldStyle(contractValueLbl, contractValueTxt);
        if (addressLbl != null && addressTxt != null) utils.setFloatTextFieldStyle(addressLbl, addressTxt);
        if (durationLbl != null && durationTxt != null) utils.setFloatTextFieldStyle(durationLbl, durationTxt);

        if (areaLbl != null && areaTxt != null) utils.setFloatTextFieldStyle(areaLbl, areaTxt);
        if (unitLbl != null && unitTxt != null) utils.setFloatTextFieldStyle(unitLbl, unitTxt);
        if (storyLbl != null && storyTxt != null) utils.setFloatTextFieldStyle(storyLbl, storyTxt);
        if (heightLbl != null && heightTxt != null) utils.setFloatTextFieldStyle(heightLbl, heightTxt);


        // Digits-only inputs
        applyDigitsOnly(contractValueTxt);
        applyDigitsOnly(durationTxt);
        applyDigitsOnly(areaTxt);
        applyDigitsOnly(unitTxt);
        applyDigitsOnly(storyTxt);
        applyDigitsOnly(heightTxt);

        // Supervisor list
        if (siteEngineerBox != null) {
            siteEngineerBox.getItems().clear();
            for (users u : database.getAllSupervisors()) {
                if (u != null && u.getUserName() != null) siteEngineerBox.getItems().add(u.getUserName());
            }
        }

        // Duration unit fixed to Day
        if (durationUnitCombo != null) {
            durationUnitCombo.getItems().setAll("Day");
            durationUnitCombo.getSelectionModel().select("Day");
            durationUnitCombo.setDisable(true);
        }

        // Load DB data ONCE
        loadLookupsOnce();
        loadValidCombosOnceFromProjectDetails();

        // Setup combos: ProjectType must be selected first
        setupTypeBuildingLevel();

        // Date-duration sync
        setupDateDurationSync();
    }

    private void applyDigitsOnly(TextField tf) {
        if (tf == null) return;
        tf.setTextFormatter(new TextFormatter<String>(change -> {
            String t = change.getControlNewText();
            return t.matches("\\d*") ? change : null;
        }));
    }

    private void loadLookupsOnce() {
        typeIdToName.clear(); typeNameToId.clear();
        buildingIdToName.clear(); buildingNameToId.clear();
        levelIdToName.clear(); levelNameToId.clear();

        Map<Integer, String> types = database.getAllProjectTypes();
        if (types != null) {
            for (var e : types.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    typeIdToName.put(e.getKey(), e.getValue());
                    typeNameToId.put(e.getValue(), e.getKey());
                }
            }
        }

        Map<Integer, String> buildings = database.getAllBuildings();
        if (buildings != null) {
            for (var e : buildings.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    buildingIdToName.put(e.getKey(), e.getValue());
                    buildingNameToId.put(e.getValue(), e.getKey());
                }
            }
        }

        Map<Integer, String> levels = database.getAllLevels();
        if (levels != null) {
            for (var e : levels.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    levelIdToName.put(e.getKey(), e.getValue());
                    levelNameToId.put(e.getValue(), e.getKey());
                }
            }
        }
    }

    /**
     * Reads valid (projectTypeId, projectBuildingId, projectLevelId) combinations once.
     * This matches your requirement: use "projectDetails" rows for filtering (auto-assign source).
     */
    private void loadValidCombosOnceFromProjectDetails() {
        validCombos.clear();

        String sql = "SELECT projectTypeId, projectBuildingId, projectLevelId " +
                "FROM projectDetails " +
                "WHERE projectTypeId IS NOT NULL AND projectBuildingId IS NOT NULL AND projectLevelId IS NOT NULL";

        try (Connection con = databaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int t = rs.getInt("projectTypeId");
                int b = rs.getInt("projectBuildingId");
                int l = rs.getInt("projectLevelId");
                validCombos.add(new Combo(t, b, l));
            }

        } catch (Exception e) {
            // If it fails, keep empty -> UI will show no items.
            e.printStackTrace();
        }
    }

    private void setupTypeBuildingLevel() {
        if (projectTypeBox == null || buildingBox == null || levelBox == null) return;

        // ProjectType items: only those that appear in validCombos
        Set<Integer> validTypeIds = validCombos.stream().map(c -> c.typeId).collect(Collectors.toSet());
        List<String> typeNames = validTypeIds.stream()
                .map(typeIdToName::get)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        projectTypeBox.getItems().setAll(typeNames);

        // Disable building & level until type selected
        buildingBox.getItems().clear();
        levelBox.getItems().clear();
        buildingBox.setDisable(true);
        levelBox.setDisable(true);

        // Type change -> fill building & level lists for that type
        projectTypeBox.valueProperty().addListener((obs, ov, nv) -> {
            if (updatingCombos) return;
            onTypeSelected();
        });

        // After type selected, building/level changes filter each other (still no DB calls)
        buildingBox.valueProperty().addListener((obs, ov, nv) -> {
            if (updatingCombos) return;
            if (projectTypeBox.getValue() == null) return;
            filterLevelByBuilding();
        });

        levelBox.valueProperty().addListener((obs, ov, nv) -> {
            if (updatingCombos) return;
            if (projectTypeBox.getValue() == null) return;
            filterBuildingByLevel();
        });
    }

    private void onTypeSelected() {
        String typeName = projectTypeBox.getValue();
        Integer typeId = typeNameToId.get(typeName);
        if (typeId == null) return;

        updatingCombos = true;
        try {
            // Valid buildings + levels for this type
            Set<Integer> buildingIds = validCombos.stream()
                    .filter(c -> c.typeId == typeId)
                    .map(c -> c.buildingId)
                    .collect(Collectors.toSet());

            Set<Integer> levelIds = validCombos.stream()
                    .filter(c -> c.typeId == typeId)
                    .map(c -> c.levelId)
                    .collect(Collectors.toSet());

            List<String> buildingNames = buildingIds.stream()
                    .map(buildingIdToName::get)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();

            List<String> levelNames = levelIds.stream()
                    .map(levelIdToName::get)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();

            buildingBox.getItems().setAll(buildingNames);
            levelBox.getItems().setAll(levelNames);

            buildingBox.setDisable(false);
            levelBox.setDisable(false);

            // Default select first (optional, but helps avoid "plenty")
            if (!buildingBox.getItems().isEmpty()) buildingBox.getSelectionModel().selectFirst();
            if (!levelBox.getItems().isEmpty()) levelBox.getSelectionModel().selectFirst();

            // After defaults, refine (so both match an actual combo)
            refineToNearestValidCombo();

        } finally {
            updatingCombos = false;
        }
    }

    /**
     * Ensures the current (type, building, level) is a valid combo.
     * If current selection is not valid, we adjust level to the first valid level for the selected building (or vice versa).
     */
    private void refineToNearestValidCombo() {
        Integer typeId = typeNameToId.get(projectTypeBox.getValue());
        if (typeId == null) return;

        Integer buildingId = buildingNameToId.get(buildingBox.getValue());
        Integer levelId = levelNameToId.get(levelBox.getValue());
        if (buildingId == null || levelId == null) return;

        boolean ok = validCombos.contains(new Combo(typeId, buildingId, levelId));
        if (ok) return;

        // Try to fix by choosing a valid level for this building
        List<String> validLevels = validCombos.stream()
                .filter(c -> c.typeId == typeId && c.buildingId == buildingId)
                .map(c -> levelIdToName.get(c.levelId))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        if (!validLevels.isEmpty()) {
            levelBox.setValue(validLevels.get(0));
            return;
        }

        // Otherwise fix by choosing a valid building for this level
        List<String> validBuildings = validCombos.stream()
                .filter(c -> c.typeId == typeId && c.levelId == levelId)
                .map(c -> buildingIdToName.get(c.buildingId))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        if (!validBuildings.isEmpty()) {
            buildingBox.setValue(validBuildings.get(0));
        }
    }

    private void filterLevelByBuilding() {
        Integer typeId = typeNameToId.get(projectTypeBox.getValue());
        Integer buildingId = buildingNameToId.get(buildingBox.getValue());
        if (typeId == null || buildingId == null) return;

        updatingCombos = true;
        try {
            String keepLevel = levelBox.getValue();

            List<String> validLevels = validCombos.stream()
                    .filter(c -> c.typeId == typeId && c.buildingId == buildingId)
                    .map(c -> levelIdToName.get(c.levelId))
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();

            levelBox.getItems().setAll(validLevels);

            if (keepLevel != null && validLevels.contains(keepLevel)) levelBox.setValue(keepLevel);
            else if (!validLevels.isEmpty()) levelBox.setValue(validLevels.get(0));

        } finally {
            updatingCombos = false;
        }
    }

    private void filterBuildingByLevel() {
        Integer typeId = typeNameToId.get(projectTypeBox.getValue());
        Integer levelId = levelNameToId.get(levelBox.getValue());
        if (typeId == null || levelId == null) return;

        updatingCombos = true;
        try {
            String keepBuilding = buildingBox.getValue();

            List<String> validBuildings = validCombos.stream()
                    .filter(c -> c.typeId == typeId && c.levelId == levelId)
                    .map(c -> buildingIdToName.get(c.buildingId))
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();

            buildingBox.getItems().setAll(validBuildings);

            if (keepBuilding != null && validBuildings.contains(keepBuilding)) buildingBox.setValue(keepBuilding);
            else if (!validBuildings.isEmpty()) buildingBox.setValue(validBuildings.get(0));

        } finally {
            updatingCombos = false;
        }
    }

    // ===== Duration sync (days only) =====

    private void setupDateDurationSync() {
        if (startDatePicker != null) {
            startDatePicker.valueProperty().addListener((obs, oldV, newV) -> {
                if (syncingDates) return;
                if (endDatePicker != null && endDatePicker.getValue() != null) updateDurationFromDates();
                else if (getDurationDaysOrNull() != null) updateEndFromStartAndDuration();
            });
        }

        if (endDatePicker != null) {
            endDatePicker.valueProperty().addListener((obs, oldV, newV) -> {
                if (syncingDates) return;
                if (startDatePicker != null && startDatePicker.getValue() != null) updateDurationFromDates();
                else if (getDurationDaysOrNull() != null) updateStartFromEndAndDuration();
            });
        }

        if (durationTxt != null) {
            durationTxt.textProperty().addListener((obs, oldV, newV) -> {
                if (syncingDates) return;
                Integer d = getDurationDaysOrNull();
                if (d == null) return;

                LocalDate s = (startDatePicker == null) ? null : startDatePicker.getValue();
                LocalDate e = (endDatePicker == null) ? null : endDatePicker.getValue();

                if (s != null) updateEndFromStartAndDuration();
                else if (e != null) updateStartFromEndAndDuration();
            });
        }
    }

    private Integer getDurationDaysOrNull() {
        if (durationTxt == null) return null;
        String t = durationTxt.getText();
        if (t == null || t.trim().isEmpty()) return null;
        try {
            int d = Integer.parseInt(t.trim());
            return d >= 0 ? d : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void setDurationDays(Integer days) {
        if (durationTxt == null || days == null) return;
        durationTxt.setText(String.valueOf(days));
    }

    private void updateDurationFromDates() {
        if (startDatePicker == null || endDatePicker == null) return;
        LocalDate s = startDatePicker.getValue();
        LocalDate e = endDatePicker.getValue();
        if (s == null || e == null) return;

        syncingDates = true;
        try {
            long diff = ChronoUnit.DAYS.between(s, e);
            if (diff < 0) diff = 0;
            setDurationDays((int) diff);
        } finally {
            syncingDates = false;
        }
    }

    private void updateEndFromStartAndDuration() {
        if (startDatePicker == null || endDatePicker == null) return;
        LocalDate s = startDatePicker.getValue();
        Integer d = getDurationDaysOrNull();
        if (s == null || d == null) return;

        syncingDates = true;
        try {
            endDatePicker.setValue(s.plusDays(d));
        } finally {
            syncingDates = false;
        }
    }

    private void updateStartFromEndAndDuration() {
        if (startDatePicker == null || endDatePicker == null) return;
        LocalDate e = endDatePicker.getValue();
        Integer d = getDurationDaysOrNull();
        if (e == null || d == null) return;

        syncingDates = true;
        try {
            startDatePicker.setValue(e.minusDays(d));
        } finally {
            syncingDates = false;
        }
    }

    // ===== Actions =====

    @FXML
    private void onClose() {
        createProjectDraft.getInstance().clear(); // ✅ add this
        sideBarPaneController p = parent();
        if (p != null) {
            p.closeAddOverlay();
            p.openInnerView("viewProjects.fxml");
        }
    }

    @FXML
    private void onDeny() {
        createProjectDraft.getInstance().clear(); // ✅ keep this
        sideBarPaneController p = parent();
        if (p != null) {
            p.closeAddOverlay();
            p.openInnerView("viewProjects.fxml");
        }
    }


    @FXML
    private void onApprove() {
        try {
            String instanceName = req(textOf(instanceNameTxt), "Instance Name");
            String supervisor = req(valueOf(siteEngineerBox), "Site Engineer");

            // Type must be first
            String typeName = req(valueOf(projectTypeBox), "Project Type");
            Integer typeId = typeNameToId.get(typeName);
            if (typeId == null) throw new IllegalArgumentException("Invalid Project Type.");

            String buildingName = req(valueOf(buildingBox), "Building");
            String levelName = req(valueOf(levelBox), "Finishing Level");

            Integer buildingId = buildingNameToId.get(buildingName);
            Integer levelId = levelNameToId.get(levelName);
            if (buildingId == null || levelId == null) throw new IllegalArgumentException("Invalid Building/Level selection.");

            // Ensure combo exists in cached set
            if (!validCombos.contains(new Combo(typeId, buildingId, levelId))) {
                throw new IllegalArgumentException("Selected Type/Building/Level has no valid projectDetails row.");
            }

            String address = req(textOf(addressTxt), "Address");
            double contractValue = parseDouble(req(textOf(contractValueTxt), "Contract Value"), "Contract Value");

            LocalDate s = (startDatePicker == null) ? null : startDatePicker.getValue();
            LocalDate e = (endDatePicker == null) ? null : endDatePicker.getValue();
            if (s == null) throw new IllegalArgumentException("Planned Start Date is required.");
            if (e == null) throw new IllegalArgumentException("Planned End Date is required.");
            if (e.isBefore(s)) throw new IllegalArgumentException("End date must be after start date.");

            Integer durationDaysInt = getDurationDaysOrNull();
            if (durationDaysInt == null) throw new IllegalArgumentException("Duration (Days) is required.");
            double durationDays = durationDaysInt.doubleValue();

            // Store into draft (your createViewProjectController reads this)
            createProjectDraft d = createProjectDraft.getInstance();
            d.instanceName = instanceName;
            d.supervisorName = supervisor;
            d.projectTypeName = typeName;
            d.buildingName = buildingName;
            d.levelName = levelName;
            d.address = address;
            d.contractValue = contractValue;
            d.startDate = s;
            d.endDate = e;
            d.duration = durationDays;
            d.projectTypeId = typeId;
            d.buildingId = buildingId;
            d.levelId = levelId;
            d.area = parseOptionalDouble(areaTxt, "Area");
            d.units = parseOptionalDouble(unitTxt, "Units");
            d.stories = parseOptionalDouble(storyTxt, "Stories");
            d.height = parseOptionalDouble(heightTxt, "Height");


            sideBarPaneController p = parent();
            if (p != null) {
                p.closeAddOverlay();
                p.openInnerView("createViewProject.fxml");
            }

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private Double parseOptionalDouble(TextField tf, String field) {
        if (tf == null) return null;
        String t = tf.getText();
        if (t == null || t.trim().isEmpty()) return null;

        try {
            return Double.parseDouble(t.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(field + " must be a number.");
        }
    }

    private static String textOf(TextField tf) { return tf == null ? null : tf.getText(); }
    private static String valueOf(ComboBox<String> cb) { return cb == null ? null : cb.getValue(); }

    private String req(String v, String field) {
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        return v.trim();
    }

    private double parseDouble(String v, String field) {
        try {
            return Double.parseDouble(v.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(field + " must be a number.");
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    // ===== AddOverlayForm =====

    @Override
    public boolean hasUnsavedChanges() {
        return (instanceNameTxt != null && !instanceNameTxt.getText().trim().isEmpty())
                || (siteEngineerBox != null && siteEngineerBox.getValue() != null)
                || (projectTypeBox != null && projectTypeBox.getValue() != null)
                || (buildingBox != null && buildingBox.getValue() != null)
                || (levelBox != null && levelBox.getValue() != null)
                || (contractValueTxt != null && !contractValueTxt.getText().trim().isEmpty())
                || (addressTxt != null && !addressTxt.getText().trim().isEmpty())
                || (startDatePicker != null && startDatePicker.getValue() != null)
                || (endDatePicker != null && endDatePicker.getValue() != null)
                || (durationTxt != null && !durationTxt.getText().trim().isEmpty());
    }

    @Override
    public boolean isFormValid() {
        // building/level disabled until type selected; still validate at approve time
        return (instanceNameTxt != null && !instanceNameTxt.getText().trim().isEmpty())
                && (siteEngineerBox != null && siteEngineerBox.getValue() != null)
                && (projectTypeBox != null && projectTypeBox.getValue() != null)
                && (buildingBox != null && buildingBox.getValue() != null)
                && (levelBox != null && levelBox.getValue() != null)
                && (contractValueTxt != null && !contractValueTxt.getText().trim().isEmpty())
                && (addressTxt != null && !addressTxt.getText().trim().isEmpty())
                && (startDatePicker != null && startDatePicker.getValue() != null)
                && (endDatePicker != null && endDatePicker.getValue() != null)
                && (durationTxt != null && !durationTxt.getText().trim().isEmpty());
    }

    @Override
    public String getValidationMessage() {
        return "Please fill all required fields before closing.";
    }
}
