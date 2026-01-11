package IPPSystem.Controllers;

import IPPSystem.Main.HelloApplication;
import IPPSystem.Utils.TabAware;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;


public class TestDashboardController implements TabAware {


    @FXML
    private AnchorPane AdminDashboardBasePane;

    @FXML
    private StackPane DashboardStackPane;

    @FXML
    private Button adminBtn;

    @FXML
    private Button durationsBtn;

    @FXML
    private VBox sidebarBox;


    private Tab currentTab;

    @Override
    public void setCurrentTab(Tab tab) {
        this.currentTab = tab;
    }




    @FXML
    void clickAdminBtn(ActionEvent event) throws IOException {
        DashboardStackPane.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("/View/test-admin-dashboard.fxml")
        );
        Parent adminDashboard = loader.load();
        DashboardStackPane.getChildren().add(adminDashboard);

        Object obj = currentTab.getProperties().get("headerController");
        if (obj instanceof CustomTabController header) {
            header.setTitle("Admin");
        }

    }

    @FXML
    void clickDurationsBtn(ActionEvent event) throws IOException {
        DashboardStackPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("/View/test-durations-dashboard.fxml")
        );
        Parent durationDashboard = loader.load();
        DashboardStackPane.getChildren().add(durationDashboard);
        Tab newAdminTab = new Tab("Duration Dashboard");
        newAdminTab.setClosable(true);

        Object obj = currentTab.getProperties().get("headerController");
        if(obj instanceof CustomTabController header){
            header.setTitle("Duration");
        }

    }

}
