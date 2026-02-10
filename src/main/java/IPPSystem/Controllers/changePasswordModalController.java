package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Interfaces.AddOverlayForm;
import IPPSystem.Models.users;
import IPPSystem.Utils.messageBoxService;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Overlay form opened from sideBarPaneController for changing the current logged-in user's password.
 */
public class changePasswordModalController implements AddOverlayForm {

    @FXML private VBox root;
    @FXML private PasswordField currentPwField;
    @FXML private PasswordField newPwField;
    @FXML private PasswordField confirmPwField;
    @FXML private Button cancelBtn;
    @FXML private Button saveBtn;

    private users loginUser;

    @FXML
    private void initialize() {
        loginUser = session.getInstance().getUser();
    }

    @FXML
    private void onCancel(ActionEvent e) {
        closeOverlay(e);
    }

    @FXML
    private void onUpdate(ActionEvent e) {
        if (!isFormValid()) {
            messageBoxService.toast("Invalid", getValidationMessage(), notificationType.WARNING);
            return;
        }

        if (loginUser == null) {
            messageBoxService.toast("Error", "No logged-in user.", notificationType.ERROR);
            return;
        }

        String current = currentPwField.getText();
        String next = newPwField.getText();

        // Verify current password
        if (!utils.checkPassword(current, loginUser.getUserPassword())) {
            messageBoxService.toast("Wrong password", "Current password is incorrect.", notificationType.WARNING);
            return;
        }

        try {
            String hashed = utils.hashPassword(next);
            boolean ok = userDatabase.updatePasswordByEmail(loginUser.getUserEmail(), hashed);

            if (ok) {
                loginUser.setUserPassword(hashed);
                session.getInstance().setUser(loginUser);

                messageBoxService.toast("Updated", "Password changed successfully.", notificationType.SUCCESS);
                clearFields();
                closeOverlay(e);
            } else {
                messageBoxService.toast("Not updated", "Password update failed.", notificationType.ERROR);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            messageBoxService.toast("Error", "Failed to change password.", notificationType.ERROR);
        }
    }

    private void clearFields() {
        if (currentPwField != null) currentPwField.clear();
        if (newPwField != null) newPwField.clear();
        if (confirmPwField != null) confirmPwField.clear();
    }

    /**
     * Close the sidebar overlay (addNewPane) without requiring a direct controller reference.
     * Works per-tab because we resolve nodes from the same Scene.
     */
    private void closeOverlay(ActionEvent e) {
        try {
            Node any = (Node) e.getSource();
            if (any == null || any.getScene() == null) return;

            StackPane addNew = (StackPane) any.getScene().lookup("#addNew");
            StackPane addNewPane = (StackPane) any.getScene().lookup("#addNewPane");
            BorderPane basePane = (BorderPane) any.getScene().lookup("#basePane");

            if (addNew != null) addNew.getChildren().clear();
            if (addNewPane != null) {
                addNewPane.setVisible(false);
                addNewPane.setManaged(false);
            }
            if (basePane != null) {
                basePane.setDisable(false);
                basePane.setOpacity(1.0);
            }
        } catch (Exception ignored) {
        }
    }

    // ===== AddOverlayForm =====

    @Override
    public boolean isFormValid() {
        if (currentPwField == null || newPwField == null || confirmPwField == null) return false;

        String current = currentPwField.getText() == null ? "" : currentPwField.getText().trim();
        String next = newPwField.getText() == null ? "" : newPwField.getText().trim();
        String confirm = confirmPwField.getText() == null ? "" : confirmPwField.getText().trim();

        if (current.isEmpty() || next.isEmpty() || confirm.isEmpty()) return false;
        if (!next.equals(confirm)) return false;
        return next.length() >= 6;
    }

    @Override
    public boolean hasUnsavedChanges() {
        String a = currentPwField != null ? currentPwField.getText() : "";
        String b = newPwField != null ? newPwField.getText() : "";
        String c = confirmPwField != null ? confirmPwField.getText() : "";
        return (a != null && !a.isBlank()) || (b != null && !b.isBlank()) || (c != null && !c.isBlank());
    }

    @Override
    public String getValidationMessage() {
        String current = currentPwField != null && currentPwField.getText() != null ? currentPwField.getText().trim() : "";
        String next = newPwField != null && newPwField.getText() != null ? newPwField.getText().trim() : "";
        String confirm = confirmPwField != null && confirmPwField.getText() != null ? confirmPwField.getText().trim() : "";

        if (current.isEmpty()) return "Please enter your current password.";
        if (next.isEmpty()) return "Please enter a new password.";
        if (confirm.isEmpty()) return "Please confirm the new password.";
        if (!next.equals(confirm)) return "New password and confirmation do not match.";
        if (next.length() < 6) return "New password must be at least 6 characters.";
        return "Please check your inputs.";
    }
}
