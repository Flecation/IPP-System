package IPPSystem.Controllers;

import IPPSystem.DAO.laborDatabase;
import IPPSystem.Models.labors;
import IPPSystem.Utils.session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;

public class createLaborController {

    @FXML private Button addLaborBtn;
    @FXML private Button handleCancel;

    @FXML private TextField laborName;
    @FXML private TextField laborPhone;
    @FXML private TextField laborNRC;
    @FXML private TextField experienceYear;

    @FXML private ComboBox<String> skillCombo;
    @FXML private DatePicker startDatePicker;

    @FXML
    public void initialize() {

        // Load skills into dropdown
        skillCombo.setItems(FXCollections.observableArrayList(
                laborDatabase.getAllSkills()
        ));
    }

    @FXML
    void clickAddLabor(ActionEvent event) {

        if (!isValid()) return;

        int skillId = laborDatabase.getSkillIdByName(skillCombo.getValue());

        labors labor = new labors();
        labor.setLaborName(laborName.getText());
        labor.setLaborPhone(laborPhone.getText());
        labor.setLaborNRC(laborNRC.getText());
        labor.setSkillId(skillId);
        labor.setLaborStartDate(Date.valueOf(startDatePicker.getValue()));

        boolean success = laborDatabase.addLabor(labor);

        if (success) {
            showAlert("Labor added successfully!");
            closeModal();

//            session.getInstance()
//                    .getNavigationController()
//                    .refreshLaborView();
        } else {
            showAlert("Failed to add labor.");
        }
    }

    @FXML
    void clickHandleCancel(ActionEvent event) {
        closeModal();
    }

    // 🔒 Validation
    private boolean isValid() {

        if (laborName.getText().isEmpty()
                || laborPhone.getText().isEmpty()
                || laborNRC.getText().isEmpty()
                || experienceYear.getText().isEmpty()
                || skillCombo.getValue() == null
                || startDatePicker.getValue() == null) {

            showAlert("Please fill all fields.");
            return false;
        }

        // Experience must be number
        try {
            Integer.parseInt(experienceYear.getText());
        } catch (Exception e) {
            showAlert("Experience must be a number.");
            return false;
        }

        return true;
    }

    private void closeModal() {
        session.getInstance().getNavigationController().closeModal();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    @FXML
    private void handleCreateEngineer() {
        session.getInstance()
                .getNavigationController()
                .showModal("createLaborModal.fxml");
    }
}
