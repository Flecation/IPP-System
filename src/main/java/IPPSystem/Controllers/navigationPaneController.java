package IPPSystem.Controllers;

import IPPSystem.Models.users;
import IPPSystem.Utils.linkButton;
import IPPSystem.Utils.messageBoxService;
import IPPSystem.Utils.session;
import IPPSystem.Utils.utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
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

    @FXML
    private StackPane rootStack;

    @FXML
    private AnchorPane modalLayer;

    @FXML
    private StackPane modelBox;

    // ===== Alert includes (from navigationPane.fxml) =====
    @FXML private Parent toastInclude;
    @FXML private toastBoxController toastIncludeController;
    @FXML private Parent confirmInclude;
    @FXML private confirmBoxController confirmIncludeController;



    protected static users user = session.getInstance().getUser();

    protected linkButton linkButton = IPPSystem.Utils.linkButton.getInstance();



    @FXML
    private void initialize(){
        utils.setTitleBar(root,minimizeBtn,restoreBtn,exitBtn);
        utils.setTheme(root);

        // Bind included toast/confirm so any controller can call messageBoxService.toast/confirm()
        messageBoxService.bindIncluded(toastInclude, toastIncludeController, confirmInclude, confirmIncludeController);


        linkButton.bindHost(tapBar, loadPane);
        linkButton.createTab(tapBar,loadPane,"sideBarPane.fxml","Project View");

        FontIcon pageAddIcon  = new FontIcon(FontAwesomeSolid.PLUS);
        pageAddIcon.getStyleClass().add("pageAddIcon");
        pageAddBtn.setGraphic(pageAddIcon);
        pageAddBtn.setOnAction(e->{
            linkButton.createTab(tapBar,loadPane,"sideBarPane.fxml","Project View   ");
        });

        session.getInstance().setNavigationController(this);

        modalLayer.setOnMouseClicked(e -> {
            if (e.getTarget() == modalLayer) {
                modalLayer.setVisible(false);
            }
        });

//        data.getProjectTypes.putAll(database.getAllProjectTypes());
//        data.getALlProjects.addAll(database.getAllProjects());
//        data.getALlUsers.addAll(database.getAllUsers());
    }

    public void showModal(String fxmlPath) {
        showModalWithController(fxmlPath); // keep compatibility
    }

    public <T> T showModalWithController(String fxmlPath) {
        try {
            String resourcePath = fxmlPath.startsWith("/") ? fxmlPath : "/View/" + fxmlPath;
            java.net.URL location = getClass().getResource(resourcePath);

            if (location == null) {
                System.err.println("!!! FXML NOT FOUND at: " + resourcePath);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(location);
            Parent modalUI = loader.load();

            modelBox.getChildren().setAll(modalUI);

            AnchorPane.setTopAnchor(modelBox, 30.0);
            AnchorPane.setBottomAnchor(modelBox, 30.0);
            AnchorPane.setLeftAnchor(modelBox, 0.0);
            AnchorPane.setRightAnchor(modelBox, 0.0);

            modalLayer.setVisible(true);
            modalLayer.toFront();

            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeModal() {
        modalLayer.setVisible(false);
    }



}