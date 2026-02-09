package IPPSystem.Controllers;

import IPPSystem.DAO.userDatabase;
import IPPSystem.Interfaces.AddOverlayForm;
import IPPSystem.Interfaces.ReloadablePage;
import IPPSystem.Interfaces.loadPaneAware;
import IPPSystem.Models.users;
import IPPSystem.Utils.storage;
import IPPSystem.Utils.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;

public class createSupervisorController implements loadPaneAware, AddOverlayForm {

    @FXML private TextArea Address;
    @FXML private DatePicker DOBPicker;
    @FXML private TextField Email;
    @FXML private TextField Password;
    @FXML private TextField userName;
    @FXML private TextField PhoneNO;
    @FXML private ComboBox<String> RoleCombo;
    @FXML private ImageView profileImg;

    private File selectedImageFile;
    private StackPane loadPane;

    @Override
    public void setLoadPane(StackPane loadPane) {
        this.loadPane = loadPane;
    }

    @FXML
    public void initialize() {
        RoleCombo.getItems().setAll("Supervisor");
        RoleCombo.getSelectionModel().selectFirst();
        enableDragDrop();
    }

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

        String hashPassword = utils.hashPassword(Password.getText().trim());
        u.setUserPassword(hashPassword);

        if (selectedImageFile != null) {
            String path = storage.saveProfileImage(selectedImageFile);
            u.setUserPhoto(path);
        }

        boolean success = userDatabase.createSupervisor(u);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supervisor created successfully.");

            sideBarPaneController sb = getSideBar();
            if (sb != null) {
                sb.closeAddOverlay();

                Object inner = sb.getCurrentInnerController();
                if (inner instanceof ReloadablePage rp) rp.onReload();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create supervisor.");
        }
    }

    @FXML
    void ClickCancel(ActionEvent event) {
        sideBarPaneController sb = getSideBar();
        if (sb != null) sb.closeAddOverlay();
    }

    @FXML
    void ClickPhoto(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(profileImg.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            profileImg.setImage(new Image(file.toURI().toString()));
        }
    }

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

    // ===== AddOverlayForm (outside click discard confirm) =====
    @Override
    public boolean hasUnsavedChanges() {
        return !userName.getText().trim().isEmpty()
                || !Email.getText().trim().isEmpty()
                || !PhoneNO.getText().trim().isEmpty()
                || !Password.getText().trim().isEmpty()
                || !Address.getText().trim().isEmpty()
                || DOBPicker.getValue() != null
                || RoleCombo.getValue() != null
                || selectedImageFile != null;
    }

    @Override
    public boolean isFormValid() {
        clearError(userName, Email, PhoneNO, Password, Address, DOBPicker, RoleCombo);

        String name = userName.getText().trim();
        String email = Email.getText().trim();
        String phone = PhoneNO.getText().trim();
        String pwd = Password.getText().trim();
        String address = Address.getText().trim();

        if (name.isEmpty() || !name.matches("[a-zA-Z\\s]+")) { markError(userName); showAlert(Alert.AlertType.WARNING, "Validation", "Name must contain letters only."); return false; }
        if (email.isEmpty() || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { markError(Email); showAlert(Alert.AlertType.WARNING, "Validation", "Invalid email format."); return false; }
        if (phone.isEmpty() || !phone.matches("09\\d{9,10}")) { markError(PhoneNO); showAlert(Alert.AlertType.WARNING, "Validation", "Phone must start with 09 and be 11–12 digits."); return false; }
        if (pwd.isEmpty() || pwd.length() < 6) { markError(Password); showAlert(Alert.AlertType.WARNING, "Validation", "Password must be at least 6 characters."); return false; }
        if (address.isEmpty()) { markError(Address); showAlert(Alert.AlertType.WARNING, "Validation", "Address cannot be empty."); return false; }
        if (DOBPicker.getValue() == null) { markError(DOBPicker); showAlert(Alert.AlertType.WARNING, "Validation", "Date of Birth is required."); return false; }
        if (RoleCombo.getValue() == null) { markError(RoleCombo); showAlert(Alert.AlertType.WARNING, "Validation", "Role must be selected."); return false; }

        return true;
    }

    @Override
    public String getValidationMessage() {
        return "Please fill all fields correctly.";
    }

    private sideBarPaneController getSideBar() {
        if (loadPane == null) return null;
        Object p = loadPane.getProperties().get("SIDEBAR_CONTROLLER");
        return (p instanceof sideBarPaneController sb) ? sb : null;
    }

    private void markError(Control c) {
        c.setStyle("-fx-border-color: #c44536; -fx-border-width: 1.5;");
    }

    private void clearError(Control... controls) {
        for (Control c : controls) c.setStyle("");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
