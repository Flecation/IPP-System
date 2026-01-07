package IPPSystem.Controllers;

import IPPSystem.Models.users;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class navigationPaneController{
    @FXML
    Button exitBtn,restoreBtn,minimizeBtn,pageAddBtn;

    @FXML
    StackPane loadPane;

    @FXML
    HBox tapBar;

    @FXML
    VBox root;

    private users loginUser = session.getInstance().getUser();

    @FXML
    private void initialize(){
        utils.setTitleBar(root,minimizeBtn,restoreBtn,exitBtn);
        utils.setTheme(root);

    }
}
