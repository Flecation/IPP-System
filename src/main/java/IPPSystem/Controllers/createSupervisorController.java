//////package IPPSystem.Controllers;
//////import IPPSystem.Utils.session;
//////import javafx.event.ActionEvent;
//////import javafx.fxml.FXML;
//////import javafx.scene.control.Button;
//////import javafx.scene.control.ComboBox;
//////import javafx.scene.control.TextArea;
//////import javafx.scene.control.TextField;
//////import javafx.scene.image.ImageView;
//////
//////public class createSupervisorController {
//////
//////    @FXML
//////    private TextArea Address;
//////
//////    @FXML
//////    private Button Cancelbtn;
//////
//////    @FXML
//////    private Button Createbtn;
//////
//////    @FXML
//////    private TextField Email;
//////
//////    @FXML
//////    private TextField PhoneNO;
//////
//////    @FXML
//////    private ComboBox<?> ProjectTypeCombo;
//////
//////    @FXML
//////    private ComboBox<?> RoleCombo;
//////
//////    @FXML
//////    private Button UploadPhotobtn;
//////
//////    @FXML
//////    private ImageView profileImg;
//////
//////    @FXML
//////    private TextField userName;
//////
//////    @FXML
//////    void ClickCancel(ActionEvent event) {
//////
//////    }
//////
//////    @FXML
//////    void ClickCreate(ActionEvent event) {
//////
//////    }
//////
//////    @FXML
//////    void ClickPhoto(ActionEvent event) {
//////
//////    }
//////
//////    @FXML
//////    private void handleCancel() {
//////        session.getInstance().getNavigationController().closeModal();
//////    }
//////
//////    @FXML
//////    private void handleCreateEngineer() {
//////        session.getInstance()
//////                .getNavigationController()
//////                .showModal("createSupervisorModal.fxml");
//////    }
//////
//////}
/////
/////
/////
/////
/////
//
//package IPPSystem.Controllers;
//
//import IPPSystem.DAO.projectDatabase;
//import IPPSystem.DAO.userDatabase;
//import IPPSystem.Models.projects;
//import IPPSystem.Models.users;
//import IPPSystem.Utils.passwordCrafting;
//import IPPSystem.Utils.session;
//import IPPSystem.Utils.storage;
//import IPPSystem.Utils.utils;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//        import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.Dragboard;
//import javafx.scene.input.TransferMode;
//import javafx.stage.FileChooser;
//
//import java.io.File;
//import java.time.LocalDate;
//
//public class createSupervisorController {
//
//    @FXML private TextArea Address;
//    @FXML
//    private DatePicker DOBPicker;
//    @FXML private TextField Email;
//
//    @FXML
//    private TextField Password;
//
//    @FXML
//    private TextField userName;
//
//    @FXML private TextField PhoneNO;
//    @FXML private ComboBox<String> RoleCombo;
//    @FXML private ImageView profileImg;
//
//    private String imagePath; // store photo path
//
//
//    private File selectedImageFile;
//    private String savedImagePath;
//
//    @FXML
//    public void initialize() {
//        RoleCombo.getItems().add("Supervisor");
//        RoleCombo.getSelectionModel().selectFirst();
//
//        enableDragDrop();
//    }
//
//
//    @FXML
//    void ClickCreate(ActionEvent event) {
//
//        if (!isFormValid()) {
//            alert("Please fill all required fields");
//            return;
//        }
//
//        users u = new users();
//        u.setUserName(userName.getText());
//        u.setUserEmail(Email.getText());
//        u.setUserPhone(PhoneNO.getText());
//        u.setUserDOB(java.sql.Date.valueOf(DOBPicker.getValue()));
//        u.setUserRole("supervisor");
//        u.setUserAddress(Address.getText());
//
//
//        passwordCrafting PasswordCrafting;
//        String hashPassword = utils.hashPassword(Password.getText());
//        u.setUserPassword(hashPassword);
//
//
//        if (selectedImageFile != null) {
//            String path = storage.saveProfileImage(selectedImageFile);
//            u.setUserPhoto(path);
//        }
//
//        boolean success = userDatabase.createSupervisor(u);
//
//        if (success) {
//            alert("Supervisor created successfully");
//            session.getInstance().getNavigationController().closeModal();
//        } else {
//            alert("Failed to create supervisor");
//        }
//    }
//
//
//    /* ---------------- IMAGE BUTTON ---------------- */
//
//    @FXML
//    void ClickPhoto(ActionEvent event) {
//
//        FileChooser chooser = new FileChooser();
//        chooser.getExtensionFilters().add(
//                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
//        );
//
//        File file = chooser.showOpenDialog(profileImg.getScene().getWindow());
//
//        if (file != null) {
//            selectedImageFile = file;
//            profileImg.setImage(new Image(file.toURI().toString()));
//        }
//    }
//
//    /* ---------------- DRAG & DROP ---------------- */
//
//    private void enableDragDrop() {
//
//        profileImg.setOnDragOver(e -> {
//            if (e.getDragboard().hasFiles()) {
//                e.acceptTransferModes(TransferMode.COPY);
//            }
//            e.consume();
//        });
//
//        profileImg.setOnDragDropped(e -> {
//            Dragboard db = e.getDragboard();
//            if (db.hasFiles()) {
//                selectedImageFile = db.getFiles().get(0);
//                profileImg.setImage(new Image(selectedImageFile.toURI().toString()));
//                e.setDropCompleted(true);
//            }
//            e.consume();
//        });
//    }
//
//    /* ---------------- CANCEL ---------------- */
//
//    @FXML
//    void ClickCancel(ActionEvent event) {
//        session.getInstance().getNavigationController().closeModal();
//    }
//
//    /* ---------------- ALERT ---------------- */
//
//    private void alert(String msg) {
//        Alert a = new Alert(Alert.AlertType.INFORMATION);
//        a.setContentText(msg);
//        a.show();
//    }
//
//
//    private void markError(Control c) {
//        c.setStyle("-fx-border-color: red; -fx-border-width: 1.5;");
//    }
//
//    private void clearError(Control c) {
//        c.setStyle("");
//    }
//
//
//    private boolean isFormValid() {
//
//        boolean valid = true;
//
//        // reset previous error styles
//        clearError(userName);
//        clearError(Email);
//        clearError(PhoneNO);
//        clearError(DOBPicker);
//        clearError(RoleCombo);
//
//        clearError(Password);
//
//        if (userName.getText().isBlank()) {
//            markError(userName);
//            valid = false;
//        }
//
//
//        if (Email.getText().isBlank()) {
//            markError(Email);
//            valid = false;
//        }
//
//        if (PhoneNO.getText().isBlank()) {
//            markError(PhoneNO);
//            valid = false;
//        }
//
//        if(Password.getText().isBlank()){
//            markError(Password);
//            valid = false;
//        }
//        if (Address.getText().isBlank()) {
//            markError(Address);
//            valid = false;
//        }
//
//        if (DOBPicker.getValue() == null) {
//            markError(DOBPicker);
//            valid = false;
//        }
//
//        if (RoleCombo.getValue() == null) {
//            markError(RoleCombo);
//            valid = false;
//        }
//
//
//        return valid;
//    }
//
//
//
//    @FXML
//    private void handleCancel() {
//        session.getInstance().getNavigationController().closeModal();
//    }
//
//    @FXML
//    private void handleCreateEngineer() {
//        session.getInstance()
//                .getNavigationController()
//                .showModal("createSupervisorModal.fxml");
//    }
//
//
//
//
//}



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
