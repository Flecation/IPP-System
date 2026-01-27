package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Utils.linkButton;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class navigationPaneController{
    @FXML
    Button exitBtn,restoreBtn,minimizeBtn,pageAddBtn;

    @FXML
    StackPane loadPane;

    @FXML
    HBox tapBar;

    @FXML
    VBox root;



    protected static users user = session.getInstance().getUser();

    protected linkButton linkButton = IPPSystem.Utils.linkButton.getInstance();



    @FXML
    private void initialize(){
        utils.setTitleBar(root,minimizeBtn,restoreBtn,exitBtn);
        utils.setTheme(root);
        linkButton.createTab(tapBar,loadPane,"sideBarPane.fxml","Project View");

        FontIcon pageAddIcon  = new FontIcon(FontAwesomeSolid.PLUS);
        pageAddIcon.getStyleClass().add("pageAddIcon");
        pageAddBtn.setGraphic(pageAddIcon);
        pageAddBtn.setOnAction(e->{
            linkButton.createTab(tapBar,loadPane,"sideBarPane.fxml","Project View   ");
        });

//        data.getProjectTypes.putAll(database.getAllProjectTypes());
//        data.getALlProjects.addAll(database.getAllProjects());
//        data.getALlUsers.addAll(database.getAllUsers());
    }

}
