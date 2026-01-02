package IPPSystem.Controllers;

import Constants.notificationType;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class loginController {

    @FXML
    Button exitBtn,minimizeBtn,restoreBtn,loginBtn,forgetBtn;

    @FXML
    TextField userNameTxt,showPasswordTxt;

    @FXML
    PasswordField hidePasswordTxt;

    @FXML
    CheckBox showPasswordCheckBox;

    @FXML
    Label userNameLbl,passwordLbl;

    @FXML
    VBox root;

    @FXML
    StackPane overlayPane;

    @FXML
    ImageView imageView;

    @FXML
    public void initialize(){
        utils.setTheme(root);
        utils.setPasswordField(showPasswordTxt,hidePasswordTxt,showPasswordCheckBox);
        utils.setTitleBar(root,minimizeBtn,restoreBtn,exitBtn);
        restoreBtn.setDisable(true);
        utils.setFloatTextFieldStyle(userNameLbl,userNameTxt);
        utils.setFloatPasswordFieldStyle(passwordLbl,showPasswordTxt,hidePasswordTxt);

        userNameTxt.setOnKeyPressed(keyEvent -> {
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
                userNameTxt.requestFocus();
            }
        });

        hidePasswordTxt.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                loginBtn.fire();
            }
            if (keyEvent.getCode() == KeyCode.UP){
                userNameTxt.requestFocus();
            }
        });

        loginBtn.setOnAction(event -> {
            String name = userNameTxt.getText();
            String password = hidePasswordTxt.isVisible() ? hidePasswordTxt.getText() : showPasswordTxt.getText();
            if(name.isEmpty()){
                try {
                    utils.setAlertBox(overlayPane,"Empty UserName","Please Enter Your Name", notificationType.WARNING,true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
