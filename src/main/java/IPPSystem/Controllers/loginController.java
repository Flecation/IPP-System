package IPPSystem.Controllers;

import IPPSystem.Utils.effect;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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
    ImageView imageView;

    @FXML
    public void initialize(){
//        Font.getFamilies().forEach(System.out::println);

        effect.setPasswordField(showPasswordTxt,hidePasswordTxt,showPasswordCheckBox);
        effect.setTitleBar(root,minimizeBtn,restoreBtn,exitBtn);
        restoreBtn.setDisable(true);
        effect.setFloatTextFieldStyle(userNameLbl,userNameTxt);
        effect.setFloatPasswordFieldStyle(passwordLbl,showPasswordTxt,hidePasswordTxt);

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
            }else{

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
    }
}
