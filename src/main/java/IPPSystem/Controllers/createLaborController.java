//package IPPSystem.Controllers;
//
//import IPPSystem.DAO.laborDatabase;
//import IPPSystem.Models.labors;
//import IPPSystem.Utils.session;
//import javafx.collections.FXCollections;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//
//import java.sql.Date;
//
//public class createLaborController {
//
//    @FXML private Button addLaborBtn;
//    @FXML private Button handleCancel;
//
//    @FXML private TextField laborName;
//    @FXML private TextField laborPhone;
//    @FXML private TextField laborNRC;
//    @FXML private TextField experienceYear;
//
//    @FXML private ComboBox<String> skillCombo;
//    @FXML private DatePicker startDatePicker;
//
//    @FXML
//    public void initialize() {
//
//        // Load skills into dropdown
//        skillCombo.setItems(FXCollections.observableArrayList(
//                laborDatabase.getAllSkills()
//        ));
//    }
//
//    @FXML
//    void clickAddLabor(ActionEvent event) {
//
//        if (!isValid()) return;
//
//        int skillId = laborDatabase.getSkillIdByName(skillCombo.getValue());
//
//        labors labor = new labors();
//        labor.setLaborName(laborName.getText());
//        labor.setLaborPhone(laborPhone.getText());
//        labor.setLaborNRC(laborNRC.getText());
//        labor.setSkillId(skillId);
//        labor.setLaborStartDate(Date.valueOf(startDatePicker.getValue()));
//
//        boolean success = laborDatabase.addLabor(labor);
//
//        if (success) {
//            showAlert("Labor added successfully!");
//            closeModal();
//
////            session.getInstance()
////                    .getNavigationController()
////                    .refreshLaborView();
//        } else {
//            showAlert("Failed to add labor.");
//        }
//    }
//
//    @FXML
//    void clickHandleCancel(ActionEvent event) {
//        closeModal();
//    }
//
//    // 🔒 Validation
//    private boolean isValid() {
//
//        if (laborName.getText().isEmpty()
//                || laborPhone.getText().isEmpty()
//                || laborNRC.getText().isEmpty()
//                || experienceYear.getText().isEmpty()
//                || skillCombo.getValue() == null
//                || startDatePicker.getValue() == null) {
//
//            showAlert("Please fill all fields.");
//            return false;
//        }
//
//        // Experience must be number
//        try {
//            Integer.parseInt(experienceYear.getText());
//        } catch (Exception e) {
//            showAlert("Experience must be a number.");
//            return false;
//        }
//
//        return true;
//    }
//
//    private void closeModal() {
//        session.getInstance().getNavigationController().closeModal();
//    }
//
//    private void showAlert(String msg) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setContentText(msg);
//        alert.show();
//    }
//
//    @FXML
//    private void handleCreateEngineer() {
//        session.getInstance()
//                .getNavigationController()
//                .showModal("createLaborModal.fxml");
//    }
//}

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

    // Callback to notify parent controller
    private Runnable onLaborAdded;

    @FXML
    public void initialize() {
        skillCombo.setItems(FXCollections.observableArrayList(
                laborDatabase.getAllSkills()
        ));
    }

    // Set parent callback
    public void setOnLaborAdded(Runnable callback) {
        this.onLaborAdded = callback;
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
            showAlert(Alert.AlertType.INFORMATION, "Success", "Labor created successfully.");
            session.getInstance().getNavigationController().closeModal();
            closeModal();

            if (onLaborAdded != null) {
                onLaborAdded.run();
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Fail", "Failed to create labor.");
        }
    }

    @FXML
    void clickHandleCancel(ActionEvent event) {
        closeModal();
    }

//    // 🔒 Validation
//    private boolean isValid() {
//        String name = laborName.getText().trim();
//        String phone = laborPhone.getText().trim();
//        String nrc = laborNRC.getText().trim();
//        String exp = experienceYear.getText().trim();
//        String skill = skillCombo.getValue();
//
//        if (name.isEmpty() || phone.isEmpty() || nrc.isEmpty() || exp.isEmpty()
//                || skill == null || startDatePicker.getValue() == null) {
//            showAlert("Please fill all fields.");
//            markError(name);
//            return false;
//        }
//
//        if (!name.matches("[a-zA-Z\\s]+")) {
//            showAlert("Name must contain letters only.");
//            return false;
//        }
//
//        if (!phone.matches("09\\d{9,10}")) {
//            showAlert("Phone must start with 09 and be 11 or 12 digits.");
//            return false;
//        }
//
//        try {
//            Integer.parseInt(exp);
//        } catch (Exception e) {
//            showAlert("Experience must be an integer.");
//            return false;
//        }
//
//        if (!nrc.matches("\\d{2}/[A-Z]{3}\\([A-Z]\\)/\\d{6}")) {
//            showAlert("NRC format is invalid. Example: 12/ABC(N)/123456");
//            return false;
//        }
//
//        return true;
//    }


    private boolean isValid() {
        String nameText = laborName.getText().trim();
        String phoneText = laborPhone.getText().trim();
        String nrcText = laborNRC.getText().trim();
        String expText = experienceYear.getText().trim();
        String skill = skillCombo.getValue();

        // Clear previous error highlights
        clearError(laborName, laborPhone, laborNRC, experienceYear, skillCombo, startDatePicker);

        // Check all fields are filled
        if (nameText.isEmpty() || phoneText.isEmpty() || nrcText.isEmpty()
                || expText.isEmpty() || skill == null || startDatePicker.getValue() == null) {
            showAlert("Please fill all fields.");
            if(nameText.isEmpty()) markError(laborName);
            if(phoneText.isEmpty()) markError(laborPhone);
            if(nrcText.isEmpty()) markError(laborNRC);
            if(expText.isEmpty()) markError(experienceYear);
            if(skill == null) markError(skillCombo);
            if(startDatePicker.getValue() == null) markError(startDatePicker);
            return false;
        }

        // Name: only letters + spaces
        if (!nameText.matches("[a-zA-Z\\s]+")) {
            showAlert("Name must contain letters only.");
            markError(laborName);
            return false;
        }

        // Phone: start with 09, 11–12 digits
        if (!phoneText.matches("09\\d{9,10}")) {
            showAlert("Phone must start with 09 and be 11 or 12 digits.");
            markError(laborPhone);
            return false;
        }

        // Experience year: integer
        try {
            Integer.parseInt(expText);
        } catch (Exception e) {
            showAlert("Experience must be an integer.");
            markError(experienceYear);
            return false;
        }

        // NRC format
        if (!nrcText.matches("\\d{2}/[A-Z]{3}\\([A-Z]\\)/\\d{6}")) {
            showAlert("NRC format is invalid. Example: 12/ABC(N)/123456");
            markError(laborNRC);
            return false;
        }

        return true;
    }


    // Add red border to indicate error
    private void markError(Control c) {
        c.setStyle("-fx-border-color: #c44536;-fx-opacity: 0.8; -fx-border-width: 2px; -fx-border-radius: 4px;");
    }

    // Clear all previous error borders
    private void clearError(Control... controls) {
        for (Control c : controls) {
            c.setStyle(""); // resets style
        }
    }


    private void closeModal() {
        session.getInstance().getNavigationController().closeModal();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }




    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}