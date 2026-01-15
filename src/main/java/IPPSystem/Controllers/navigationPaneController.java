package IPPSystem.Controllers;

import IPPSystem.Models.users;
import IPPSystem.Utils.linkButton;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class navigationPaneController{
    @FXML
    Button exitBtn,restoreBtn,minimizeBtn,pageAddBtn;

    @FXML
    StackPane loadPane;

    @FXML
    HBox tapBar;

    @FXML
    VBox root;

    protected static users loginUser = session.getInstance().getUser();

    private final linkButton linkButton = new linkButton();
    @FXML
    private void initialize(){
        utils.setTitleBar(root,minimizeBtn,restoreBtn,exitBtn);
        utils.setTheme(root);
        linkButton.createTab(tapBar,loadPane,"sideBarPane.fxml","Dashboard");
        FontIcon pageAddIcon  = new FontIcon(FontAwesomeSolid.PLUS);
        pageAddIcon.getStyleClass().add("pageAddIcon");
        pageAddBtn.setGraphic(pageAddIcon);
        pageAddBtn.setOnAction(e->linkButton.createTab(tapBar,loadPane,"sideBarPane.fxml","Dashboard"));
    }
}
