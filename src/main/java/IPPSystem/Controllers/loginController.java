package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.DAO.databaseConnection;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.users;
import IPPSystem.Utils.dateFormatter;
import IPPSystem.Utils.passwordCrafting;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class  loginController {

    @FXML
    Button exitBtn,minimizeBtn,restoreBtn,loginBtn,forgetBtn;

    @FXML
    TextField userEmailTxt,showPasswordTxt;

    @FXML
    PasswordField hidePasswordTxt;

    @FXML
    CheckBox showPasswordCheckBox;

    @FXML
    Label userEmailLbl,passwordLbl;

    @FXML
    VBox root;

    @FXML
    StackPane overlayPane;

    @FXML
    ImageView imageView;

    @FXML Label emailWrongLbl,pwWrongLbl;

    @FXML private VBox loginBox, otpBox, forgetPwBox,changeNewPwBox;
    @FXML private TextField forgetPwEmailTxt;
    @FXML private Label forgetPwEmailLbl, backToLogin, otpEmail, otpEmailName;
    @FXML private Button sentResetCodeBtn, verifyOtpBtn;
    @FXML private TextField otp1, otp2, otp3, otp4, otp5, otp6;
    @FXML private Label openEmailAction,showChangePwLbl;
    @FXML private BorderPane openwithEmail;
    @FXML private HBox backToLoginRow;
    @FXML private Button changePwBtn;
    @FXML private TextField showChangePwTxt;
    @FXML private PasswordField hideChangePwTxt;
    @FXML private CheckBox showChangePwChoiceBox;

    public static session user = session.getInstance();

    private static int count = 5;

    private String resetEmail = null;


    @FXML
    public void initialize(){
     // userDatabase.addUser(new users("manager","manager@gmail.com","099666",utils.hashPassword("123"), role.MANAGER.toString(), dateFormatter.DOB("2005-09-27"),dateFormatter.today(),""));
  //      userDatabase.addUser(new users("supervisor","supervisor@gmail.com","092666",utils.hashPassword("123"), role.SUPERVISOR.toString(), dateFormatter.DOB("2005-09-27"),dateFormatter.today(),""));
//        userDatabase.addUser(new users("Kyaw Kyaw","ant@gmail.com","099666",utils.hashPassword("123"), role.SUPERVISOR.toString(), dateFormatter.DOB("2005-09-27"),dateFormatter.today(),""));
//        userDatabase.addUser(new users("Mg Mg","ant@gmail.com","099666",utils.hashPassword("123"), role.SUPERVISOR.toString(), dateFormatter.DOB("2005-09-27"),dateFormatter.today(),""));
//        userDatabase.addUser(new users("Zaw Zaw","ant@gmail.com","099666",utils.hashPassword("123"), role.SUPERVISOR.toString(), dateFormatter.DOB("2005-09-27"),dateFormatter.today(),""));
//        userDatabase.addUser(new users("Hla Hla","ant@gmail.com","099666",utils.hashPassword("123"), role.SUPERVISOR.toString(), dateFormatter.DOB("2005-09-27"),dateFormatter.today(),""));
//        userDatabase.addUser(new users("Mya Mya","ant@gmail.com","099666",utils.hashPassword("123"), role.SUPERVISOR.toString(), dateFormatter.DOB("2005-09-27"),dateFormatter.today(),""));
        loginBox.setVisible(true);
        otpBox.setVisible(false);
        forgetPwBox.setVisible(false);
        changeNewPwBox.setVisible(false);
        openwithEmail.setVisible(false);
        try {
            databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        emailWrongLbl.setVisible(false);
        pwWrongLbl.setVisible(false);
        utils.setTheme(root);
//        utils.changeTheme(root);
        utils.setPasswordField(showPasswordTxt,hidePasswordTxt,showPasswordCheckBox);
        utils.setPasswordField(showChangePwTxt,hideChangePwTxt,showChangePwChoiceBox);
        utils.setTitleBar(root,minimizeBtn,restoreBtn,exitBtn);
        restoreBtn.setDisable(true);
        utils.setFloatTextFieldStyle(userEmailLbl,userEmailTxt);
        utils.setFloatPasswordFieldStyle(passwordLbl,showPasswordTxt,hidePasswordTxt);
        utils.setFloatTextFieldStyle(forgetPwEmailLbl,forgetPwEmailTxt);
        utils.setFloatPasswordFieldStyle(showChangePwLbl,showChangePwTxt,hideChangePwTxt);

        userEmailTxt.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                if(showPasswordTxt.isVisible()){
                    showPasswordTxt.requestFocus();
                }else {
                    hidePasswordTxt.requestFocus();
                }
            }
            if (keyEvent.getCode() == KeyCode.DOWN){
                if(showPasswordTxt.isVisible()){
                    showPasswordTxt.requestFocus();
                }else {
                    hidePasswordTxt.requestFocus();
                }
            }
        });

        showPasswordTxt.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                loginBtn.fire();
            }
            if (keyEvent.getCode() == KeyCode.UP){
                userEmailTxt.requestFocus();
            }
        });

        hidePasswordTxt.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                loginBtn.fire();
            }
            if (keyEvent.getCode() == KeyCode.UP){
                userEmailTxt.requestFocus();
            }
        });

        loginBtn.setOnAction(event -> {
            pwWrongLbl.setVisible(false);
            emailWrongLbl.setVisible(false);
            if (count <= 0) {
                openwithEmail.toFront();
                showNode(openwithEmail);
                utils.setAlertBox(overlayPane, "Too Many Attempts", "Open via email to reset your password.", notificationType.WARNING, true);
                return;
            }

            String email = userEmailTxt.getText().trim();
            String password = hidePasswordTxt.isVisible() ? hidePasswordTxt.getText() : showPasswordTxt.getText();

            if (email.isEmpty() || !email.contains("@")) {
                emailWrongLbl.setVisible(true);
                emailWrongLbl.setText("Enter a valid email address.");
                userEmailTxt.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                pwWrongLbl.setVisible(true);
                pwWrongLbl.setText("Enter your password.");
                if (showPasswordTxt.isVisible()) showPasswordTxt.requestFocus();
                else hidePasswordTxt.requestFocus();
                return;
            }

            users user = database.loginUser(email);
            if (user == null) {
                count = Math.max(0, count - 1);
                if (count == 0) {
                    openwithEmail.toFront();
                    showNode(openwithEmail);
                    utils.setAlertBox(overlayPane, "Too Many Attempts", "Open via email to reset your password.", notificationType.WARNING, true);
                    return;
                }
                emailWrongLbl.setVisible(true);
                emailWrongLbl.setText("Email not found. (" + count + " attempts left)");
                userEmailTxt.requestFocus();
                return;
            }

            if (!utils.checkPassword(password, user.getUserPassword())) {
                count = Math.max(0, count - 1);
                if (count == 0) {
                    openwithEmail.toFront();
                    showNode(openwithEmail);
                    utils.setAlertBox(overlayPane, "Too Many Attempts", "Open via email to reset your password.", notificationType.WARNING, true);
                    return;
                }
                pwWrongLbl.setVisible(true);
                pwWrongLbl.setText("Incorrect password. (" + count + " attempts left)");
                if (showPasswordTxt.isVisible()) showPasswordTxt.requestFocus();
                else hidePasswordTxt.requestFocus();
                return;
            }

            // Success
            this.user.setUser(user);
            utils.switchNewScene(loginBtn, "navigationPane.fxml");
        });
        forgetBtn.setOnAction(e -> {
            switchPane(loginBox, forgetPwBox);
            backToLoginRow.setVisible(true);
            backToLoginRow.setManaged(true);
            clearForgotErrors();
        });
        // Back to Login from Forgot Password
        backToLogin.setOnMouseClicked(e -> {
            switchPane(forgetPwBox, loginBox);
            clearForgotErrors();
        });
        // Send Reset Code
        sentResetCodeBtn.setOnAction(e -> onSendResetCode());
        // Verify OTP (if you use OTP)
        verifyOtpBtn.setOnAction(e -> onVerifyOtp());
        // Optional: open email client when "Open Via Email Code" is clicked
        openEmailAction.setOnMouseClicked(e -> {
            hideNode(openwithEmail, null);
            switchPane(loginBox, forgetPwBox);
            backToLoginRow.setVisible(false);
            backToLoginRow.setManaged(false);
            forgetPwEmailTxt.requestFocus();
        });

        configureOtpInputs();
        verifyOtpBtn.setDisable(true);

        changePwBtn.setOnAction(e -> onChangePassword());
        // Make loginBox visible initially (if you ever hide it)
        loginBox.setVisible(true);
    }

    private void clearForgotErrors() {
        // If you add error labels for forgot password, clear them here
    }
    private void onSendResetCode() {
        String email = forgetPwEmailTxt.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            utils.setAlertBox(overlayPane, "Invalid Email", "Please enter a valid email address.", notificationType.ERROR, true);
            return;
        }
        // Check if user exists
        users user = database.loginUser(email);
        if (user == null) {
            utils.setAlertBox(overlayPane, "User Not Found", "No account with that email exists.", notificationType.ERROR, true);
            return;
        }

        resetEmail = email;

        // Simulate sending code (replace with real email service if you have one)
        utils.setAlertBox(overlayPane, "Reset Code Sent", "A reset code has been sent to " + email, notificationType.SUCCESS, true);
        // Show OTP box
        switchPane(forgetPwBox, otpBox);
        otpEmailName.setText(email);
        // Optional: generate a demo OTP and show it (for testing only)
        String demoOtp = "123456";
        // In real app, you'd email this OTP; here we just log it
        System.out.println("DEMO OTP for " + email + ": " + demoOtp);

        clearOtpInputs();
        otp1.requestFocus();
    }

    private void onVerifyOtp() {
        String entered = otp1.getText() + otp2.getText() + otp3.getText() + otp4.getText() + otp5.getText() + otp6.getText();
        if (entered.length() != 6) {
            utils.setAlertBox(overlayPane, "Invalid OTP", "Please enter all 6 digits.", notificationType.ERROR, true);
            return;
        }
        // For demo, accept 123456; replace with real verification
        if ("123456".equals(entered)) {
            utils.setAlertBox(overlayPane, "OTP Verified", "You can now reset your password.", notificationType.SUCCESS, true);
            switchPane(otpBox, changeNewPwBox);
            if (showChangePwTxt.isVisible()) {
                showChangePwTxt.requestFocus();
            } else {
                hideChangePwTxt.requestFocus();
            }
        } else {
            utils.setAlertBox(overlayPane, "Wrong OTP", "The code you entered is incorrect.", notificationType.ERROR, true);
        }
    }

    private void onChangePassword() {
        if (resetEmail == null || resetEmail.isBlank()) {
            utils.setAlertBox(overlayPane, "Missing Email", "Please restart the reset password flow.", notificationType.ERROR, true);
            return;
        }

        String newPw = hideChangePwTxt.isVisible() ? hideChangePwTxt.getText() : showChangePwTxt.getText();
        if (newPw == null || newPw.trim().isEmpty()) {
            utils.setAlertBox(overlayPane, "Invalid Password", "Please enter a new password.", notificationType.ERROR, true);
            return;
        }

        String hashed = utils.hashPassword(newPw.trim());
        boolean ok = userDatabase.updatePasswordByEmail(resetEmail, hashed);
        if (!ok) {
            utils.setAlertBox(overlayPane, "Update Failed", "Unable to update password. Please try again.", notificationType.ERROR, true);
            return;
        }

        utils.setAlertBox(overlayPane, "Password Updated", "You can now log in with your new password.", notificationType.SUCCESS, true);

        // Reset state and go back to login
        resetEmail = null;
        count = 5;
        clearOtpInputs();
        forgetPwEmailTxt.clear();
        showChangePwTxt.clear();
        hideChangePwTxt.clear();

        hideNode(openwithEmail, null);
        hideNode(otpBox, null);
        hideNode(forgetPwBox, null);
        hideNode(changeNewPwBox, null);
        showNode(loginBox);

        userEmailTxt.requestFocus();
    }

    private void configureOtpInputs() {
        configureOtpField(otp1, otp2, null);
        configureOtpField(otp2, otp3, otp1);
        configureOtpField(otp3, otp4, otp2);
        configureOtpField(otp4, otp5, otp3);
        configureOtpField(otp5, otp6, otp4);
        configureOtpField(otp6, null, otp5);
    }

    private void configureOtpField(TextField field, TextField next, TextField prev) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            // keep only digits
            String digitsOnly = newVal.replaceAll("\\D", "");
            if (!digitsOnly.equals(newVal)) {
                field.setText(digitsOnly);
                return;
            }

            // keep only 1 digit
            if (digitsOnly.length() > 1) {
                field.setText(digitsOnly.substring(0, 1));
                return;
            }

            if (digitsOnly.length() == 1 && next != null) {
                next.requestFocus();
                next.selectAll();
            }

            verifyOtpBtn.setDisable(!isOtpComplete());
        });

        field.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.BACK_SPACE) {
                if (field.getText() == null || field.getText().isEmpty()) {
                    if (prev != null) {
                        prev.requestFocus();
                        prev.selectAll();
                    }
                }
                verifyOtpBtn.setDisable(!isOtpComplete());
            }
        });
    }

    private boolean isOtpComplete() {
        return otp1.getText().length() == 1
                && otp2.getText().length() == 1
                && otp3.getText().length() == 1
                && otp4.getText().length() == 1
                && otp5.getText().length() == 1
                && otp6.getText().length() == 1;
    }

    private void clearOtpInputs() {
        otp1.clear();
        otp2.clear();
        otp3.clear();
        otp4.clear();
        otp5.clear();
        otp6.clear();
        verifyOtpBtn.setDisable(true);
    }

    private void switchPane(Node from, Node to) {
        if (from != null) {
            hideNode(from, null);
        }
        if (to != null) {
            showNode(to);
        }
    }

    private void showNode(Node node) {
        if (node == null) {
            return;
        }
        node.setVisible(true);
        node.setOpacity(0);
        node.setTranslateY(10);
        node.setScaleX(0.98);
        node.setScaleY(0.98);

        FadeTransition fade = new FadeTransition(Duration.millis(220), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slide = new TranslateTransition(Duration.millis(220), node);
        slide.setFromY(10);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scale = new ScaleTransition(Duration.millis(220), node);
        scale.setFromX(0.98);
        scale.setFromY(0.98);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(fade, slide, scale).play();
    }

    private void hideNode(Node node, Runnable after) {
        if (node == null || !node.isVisible()) {
            if (after != null) {
                after.run();
            }
            return;
        }

        FadeTransition fade = new FadeTransition(Duration.millis(180), node);
        fade.setFromValue(node.getOpacity());
        fade.setToValue(0);
        fade.setInterpolator(Interpolator.EASE_IN);

        TranslateTransition slide = new TranslateTransition(Duration.millis(180), node);
        slide.setFromY(node.getTranslateY());
        slide.setToY(-6);
        slide.setInterpolator(Interpolator.EASE_IN);

        ParallelTransition pt = new ParallelTransition(fade, slide);
        pt.setOnFinished(e -> {
            node.setVisible(false);
            node.setOpacity(1);
            node.setTranslateY(0);
            if (after != null) {
                after.run();
            }
        });
        pt.play();
    }
}
