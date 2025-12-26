package IPPSystem.Controllers;

import IPPSystem.Utils.effect;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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
    public void initialize(){
        effect.setPasswordField(showPasswordTxt,hidePasswordTxt,showPasswordCheckBox);
        effect.setTitleBar(root,minimizeBtn,restoreBtn,exitBtn);
        restoreBtn.setDisable(true);
        effect.setFloatTextFieldStyle(userNameLbl,userNameTxt);
        effect.setFloatPasswordFieldStyle(passwordLbl,showPasswordTxt,hidePasswordTxt);
    }
}
