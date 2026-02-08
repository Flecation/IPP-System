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
    @FXML private TextField laborNRC;
    @FXML private TextField experienceYear;

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

        labors labor = new labors();
        labor.setSkillId(selected.getSkillId());
        labor.setLaborName(laborName.getText().trim());
        labor.setLaborPhone(laborPhone.getText().trim());
        labor.setLaborNRC(laborNRC.getText().trim());
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
                || (laborNRC != null && !laborNRC.getText().trim().isEmpty())
                || (experienceYear != null && !experienceYear.getText().trim().isEmpty())
                || (skillCombo != null && skillCombo.getValue() != null)
                || (startDatePicker != null && startDatePicker.getValue() != null);
    }

    @Override
    public boolean isFormValid() {
        String name = laborName == null ? "" : laborName.getText().trim();
        String phone = laborPhone == null ? "" : laborPhone.getText().trim();
        String nrc = laborNRC == null ? "" : laborNRC.getText().trim();
        String exp = experienceYear == null ? "" : experienceYear.getText().trim();
        skills s = skillCombo == null ? null : skillCombo.getValue();

        if (name.isEmpty() || phone.isEmpty() || nrc.isEmpty() || exp.isEmpty() || s == null || startDatePicker.getValue() == null)
            return false;

        if (!name.matches("[a-zA-Z\\s]+")) return false;
        if (!phone.matches("09\\d{9,10}")) return false;

        try { Integer.parseInt(exp); } catch (Exception e) { return false; }

        if (!nrc.matches("\\d{2}/[A-Z]{3}\\([A-Z]\\)/\\d{6}")) return false;

        return true;
    }

    @Override
    public String getValidationMessage() {
        return "Please fill all fields correctly.\n" +
                "- Name: letters only\n" +
                "- Phone: 09 + 11/12 digits\n" +
                "- NRC: 12/ABC(N)/123456\n" +
                "- Skill + Entrance Date required";
    }
}
