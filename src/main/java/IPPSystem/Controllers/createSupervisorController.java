package IPPSystem.Controllers;

import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.users;
import IPPSystem.Utils.passwordCrafting;
import IPPSystem.Utils.session;
import IPPSystem.Utils.storage;
import IPPSystem.Utils.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;

public class createSupervisorController {

    @FXML private TextArea Address;
    @FXML private DatePicker DOBPicker;
    @FXML private TextField Email;
    @FXML private TextField Password;
    @FXML private TextField userName;
    @FXML private TextField PhoneNO;
    @FXML private ComboBox<String> RoleCombo;
    @FXML private ImageView profileImg;

    private File selectedImageFile;

    @FXML
    public void initialize() {
        RoleCombo.getItems().add("Supervisor");
        RoleCombo.getSelectionModel().selectFirst();
        enableDragDrop();
    }

    /* ---------------- CREATE BUTTON ---------------- */
    @FXML
    void ClickCreate(ActionEvent event) {

        if (!isFormValid()) return;

        users u = new users();
        u.setUserName(userName.getText().trim());
        u.setUserEmail(Email.getText().trim());
        u.setUserPhone(PhoneNO.getText().trim());
        u.setUserDOB(java.sql.Date.valueOf(DOBPicker.getValue()));
        u.setUserRole(RoleCombo.getValue().toLowerCase());
        u.setUserAddress(Address.getText().trim());

        // hash password
        String hashPassword = utils.hashPassword(Password.getText().trim());
        u.setUserPassword(hashPassword);

        // save profile image
        if (selectedImageFile != null) {
            String path = storage.saveProfileImage(selectedImageFile);
            u.setUserPhoto(path);
        }

        boolean success = userDatabase.createSupervisor(u);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supervisor created successfully.");
//            session.getInstance().getNavigationController().closeModal();
            closeModal();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create supervisor.");
        }
    }


    private void closeModal() {
        session.getInstance().getNavigationController().closeModal();
    }
    /* ---------------- CANCEL BUTTON ---------------- */
    @FXML
    void ClickCancel(ActionEvent event) {
        session.getInstance().getNavigationController().closeModal();
    }

    /* ---------------- IMAGE BUTTON ---------------- */
    @FXML
    void ClickPhoto(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(profileImg.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            profileImg.setImage(new Image(file.toURI().toString()));
        }
    }

    /* ---------------- DRAG & DROP ---------------- */
    private void enableDragDrop() {
        profileImg.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) e.acceptTransferModes(TransferMode.COPY);
            e.consume();
        });

        profileImg.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasFiles()) {
                selectedImageFile = db.getFiles().get(0);
                profileImg.setImage(new Image(selectedImageFile.toURI().toString()));
                e.setDropCompleted(true);
            }
            e.consume();
        });
    }

    /* ---------------- VALIDATION ---------------- */
    private boolean isFormValid() {
        String name = userName.getText().trim();
        String email = Email.getText().trim();
        String phone = PhoneNO.getText().trim();
        String pwd = Password.getText().trim();
        String address = Address.getText().trim();

        // reset styles
        clearError(userName, Email, PhoneNO, Password, Address, DOBPicker, RoleCombo);

        // Name: letters + spaces
        if (name.isEmpty() || !name.matches("[a-zA-Z\\s]+")) {
            markError(userName);
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name must contain letters only.");
            return false;
        }

        // Email: simple email regex
        if (email.isEmpty() || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            markError(Email);
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid email format.");
            return false;
        }

        // Phone: starts with 09, 11–12 digits
        if (phone.isEmpty() || !phone.matches("09\\d{9,10}")) {
            markError(PhoneNO);
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Phone must start with 09 and be 11–12 digits.");
            return false;
        }

        // Password: min 6 chars
        if (pwd.isEmpty() || pwd.length() < 6) {
            markError(Password);
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters.");
            return false;
        }

        // Address
        if (address.isEmpty()) {
            markError(Address);
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Address cannot be empty.");
            return false;
        }

        // DOB
        if (DOBPicker.getValue() == null) {
            markError(DOBPicker);
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Date of Birth is required.");
            return false;
        }

        // Role
        if (RoleCombo.getValue() == null) {
            markError(RoleCombo);
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Role must be selected.");
            return false;
        }

        return true;
    }

    /* ---------------- STYLE HELPERS ---------------- */
    private void markError(Control c) {
        c.setStyle("-fx-border-color: #c44536; -fx-border-width: 1.5;");
    }

    private void clearError(Control... controls) {
        for (Control c : controls) {
            c.setStyle("");
        }
    }

    /* ---------------- UI ALERT ---------------- */
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
