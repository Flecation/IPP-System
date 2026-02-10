package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.DAO.database;
import IPPSystem.Interfaces.AddOverlayForm;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Interfaces.ReloadablePage;
import IPPSystem.Models.labors;
import IPPSystem.Models.skills;
import IPPSystem.Utils.messageBoxService;
import IPPSystem.Utils.utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.sql.Date;

public class createLaborController implements loadPaneAware, AddOverlayForm {

    @FXML private Button addLaborBtn;
    @FXML private Button handleCancel;

    @FXML private TextField laborName;
    @FXML private TextField laborPhone;
    @FXML private TextField experienceYear;

    // NRC fields
    @FXML private TextField nrcPart1; // State/Division (2 digits)
    @FXML private TextField nrcPart2; // Township code (3 letters)
    @FXML private TextField nrcPart3; // NRC type (1 letter)
    @FXML private TextField nrcPart4; // Registration number (6 digits)

    @FXML private ComboBox<skills> skillCombo;
    @FXML private DatePicker startDatePicker;

    private StackPane loadPane;

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }

    @FXML
    public void initialize() {
        // load all skills (id+name)
        skillCombo.setItems(database.getAllSkill());

        // display skillName
        skillCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(skills item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getSkillName());
            }
        });
        skillCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(skills item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getSkillName());
            }
        });

        // Setup NRC field listeners for auto-advance and validation
        setupNRCFields();
    }

    private void setupNRCFields() {
        // Part 1: State/Division (2 digits max)
        nrcPart1.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nrcPart1.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 2) {
                nrcPart1.setText(newValue.substring(0, 2));
            }
            if (newValue.length() == 2) {
                nrcPart2.requestFocus();
            }
        });

        // Part 2: Township code (3 letters max)
        nrcPart2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z]*")) {
                nrcPart2.setText(newValue.replaceAll("[^A-Za-z]", ""));
            }
            String upper = newValue.toUpperCase();
            if (!newValue.equals(upper)) {
                nrcPart2.setText(upper);
            }
            if (newValue.length() > 3) {
                nrcPart2.setText(newValue.substring(0, 3));
            }
            if (newValue.length() == 3) {
                nrcPart3.requestFocus();
            }
        });

        // Part 3: NRC type (1 letter)
        nrcPart3.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z]*")) {
                nrcPart3.setText(newValue.replaceAll("[^A-Za-z]", ""));
            }
            String upper = newValue.toUpperCase();
            if (!newValue.equals(upper)) {
                nrcPart3.setText(upper);
            }
            if (newValue.length() > 1) {
                nrcPart3.setText(newValue.substring(0, 1));
            }
            if (newValue.length() == 1) {
                nrcPart4.requestFocus();
            }
        });

        // Part 4: Registration number (6 digits)
        nrcPart4.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!newValue.matches("\\d*")) {
            nrcPart4.setText(newValue.replaceAll("[^\\d]", ""));
        }
        if (newValue.length() > 6) {
            nrcPart4.setText(newValue.substring(0, 6));
        }
        });
    }

    private String getFullNRC() {
        String part1 = nrcPart1.getText().trim();
        String part2 = nrcPart2.getText().trim();
        String part3 = nrcPart3.getText().trim();
        String part4 = nrcPart4.getText().trim();

        if (part1.isEmpty() || part2.isEmpty() || part3.isEmpty() || part4.isEmpty()) {
            return "";
        }

        return String.format("%s/%s(%s)/%s", part1, part2, part3, part4);
    }

    private boolean validateNRC() {
        String part1 = nrcPart1.getText().trim();
        String part2 = nrcPart2.getText().trim();
        String part3 = nrcPart3.getText().trim();
        String part4 = nrcPart4.getText().trim();

        // Check if all parts are filled
        if (part1.isEmpty() || part2.isEmpty() || part3.isEmpty() || part4.isEmpty()) {
            return false;
        }

        // Validate each part
        if (!part1.matches("\\d{2}")) return false; // Exactly 2 digits
        if (!part2.matches("[A-Z]{3}")) return false; // Exactly 3 uppercase letters
        if (!part3.matches("[A-Z]")) return false; // Exactly 1 uppercase letter
        if (!part4.matches("\\d{6}")) return false; // Exactly 6 digits

        // Validate Myanmar NRC specific rules
        int stateCode = Integer.parseInt(part1);
        if (stateCode < 1 || stateCode > 14) return false; // Myanmar has 14 states/regions

        // Validate NRC type (common types: N=National, C=Citizen, etc.)
        String validTypes = "NCA";
        if (!validTypes.contains(part3)) return false;

        return true;
    }

    @FXML
    void clickHandleCancel(ActionEvent event) {
        sideBarPaneController sb = getSideBar();
        if (sb != null) sb.closeAddOverlay();
    }

    @FXML
    void clickAddLabor(ActionEvent event) {
        if (!isFormValid()) {
            messageBoxService.toast("Invalid", getValidationMessage(), notificationType.WARNING);
            return;
        }

        skills selected = skillCombo.getValue();
        if (selected == null || selected.getSkillId() <= 0) {
            messageBoxService.toast("Invalid", "Please choose a valid skill.", notificationType.WARNING);
            return;
        }

        if (!validateNRC()) {
            messageBoxService.toast("Invalid NRC", "Please enter a valid NRC format.\nFormat: 12/ABC(N)/123456", notificationType.WARNING);
            return;
        }

        labors labor = new labors();
        labor.setSkillId(selected.getSkillId());
        labor.setLaborName(laborName.getText().trim());
        labor.setLaborPhone(laborPhone.getText().trim());
        labor.setLaborNRC(getFullNRC()); // Use the assembled NRC
        labor.setLaborStartDate(Date.valueOf(startDatePicker.getValue()));
        labor.setActive(true);

        boolean ok = database.createLabor(labor);

        if (ok) {
            messageBoxService.toast("Success", "Labor created successfully.", notificationType.SUCCESS);

            sideBarPaneController sb = getSideBar();
            if (sb != null) {
                sb.closeAddOverlay();

                // ✅ reload current inner page (labor view)
                Object inner = sb.getCurrentInnerController();
                if (inner instanceof ReloadablePage rp) rp.onReload();
            }
        } else {
            messageBoxService.toast("Fail", "Failed to create labor.", notificationType.ERROR);
        }
    }

    private sideBarPaneController getSideBar() {
        StackPane lp = (loadPane != null) ? loadPane : utils.findTabLoadPane(handleCancel);
        if (lp == null) return null;
        Object p = lp.getProperties().get("SIDEBAR_CONTROLLER");
        return (p instanceof sideBarPaneController sb) ? sb : null;
    }

    // ===== AddOverlayForm (for outside click confirm discard) =====
    @Override
    public boolean hasUnsavedChanges() {
        return (laborName != null && !laborName.getText().trim().isEmpty())
                || (laborPhone != null && !laborPhone.getText().trim().isEmpty())
                || (experienceYear != null && !experienceYear.getText().trim().isEmpty())
                || (nrcPart1 != null && !nrcPart1.getText().trim().isEmpty())
                || (nrcPart2 != null && !nrcPart2.getText().trim().isEmpty())
                || (nrcPart3 != null && !nrcPart3.getText().trim().isEmpty())
                || (nrcPart4 != null && !nrcPart4.getText().trim().isEmpty())
                || (skillCombo != null && skillCombo.getValue() != null)
                || (startDatePicker != null && startDatePicker.getValue() != null);
    }

    @Override
    public boolean isFormValid() {
        String name = laborName == null ? "" : laborName.getText().trim();
        String phone = laborPhone == null ? "" : laborPhone.getText().trim();
        String exp = experienceYear == null ? "" : experienceYear.getText().trim();
        skills s = skillCombo == null ? null : skillCombo.getValue();

        if (name.isEmpty() || phone.isEmpty() || exp.isEmpty() || s == null || startDatePicker.getValue() == null)
            return false;

        if (!name.matches("[a-zA-Z\\s]+")) return false;
        if (!phone.matches("09\\d{9,10}")) return false;

        try {
            int expYear = Integer.parseInt(exp);
            if (expYear < 0 || expYear > 50) return false; // Reasonable experience range
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public String getValidationMessage() {
        return "Please fill all fields correctly.\n" +
                "- Name: letters only\n" +
                "- Phone: 09 + 9 or 10 digits (e.g., 09123456789)\n" +
                "- Experience: 0-50 years\n" +
                "- NRC: Enter in format 12/ABC(N)/123456\n" +
                "- Skill + Entrance Date required";
    }
}