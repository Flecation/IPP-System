package IPPSystem.Controllers;

import Constants.notificationType;
import IPPSystem.DAO.databaseConnection;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.users;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

    public static session user = session.getInstance();


    @FXML
    public void initialize(){
//        userDatabase.addUser(new users("ant","ant@gmail.com","099666",utils.hashPassword("123"), role.MANAGER.toString(), dateFormatter.DOB("2005-09-27"),dateFormatter.today()));
        try {
            Connection con = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            }else if(password.isEmpty()){
                try {
                    utils.setAlertBox(overlayPane,"Empty Password","Please Enter Your Password",notificationType.WARNING,true);
                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }else{
                users check = userDatabase.loginUser(name,password);
                if (check == null){
                    try {

                        utils.setAlertBox(overlayPane,"Wrong User","Please Check Your Name or Password",notificationType.WRONG,true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                   user.setUser(check);
                    utils.switchNewScene(loginBtn,"navigationPane.fxml");
                }
            }

        });
    }
}
