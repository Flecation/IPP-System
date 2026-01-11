package IPPSystem.Controllers;

import IPPSystem.Utils.TabAware;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class TestXDashboardController {

    @FXML
    private TabPane tabPane;

    private Tab newTabButton;


    @FXML
    public void initialize() throws IOException {
        createPlusTab();
    }

    @FXML
    private void addNewTab(String title, String fxmlPath) {

        // Prevent duplicate
        for (Tab tab : tabPane.getTabs()) {
            if (title.equals(tab.getText())) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        try {
            Tab tab = new Tab();
            tab.setClosable(false);

            // Load content WITH controller access
            FXMLLoader contentLoader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            Parent content = contentLoader.load();

            // TabAware for dynamic title text
            Object controller = contentLoader.getController();
            if (controller instanceof TabAware tabAware) {
                tabAware.setCurrentTab(tab);
            }

            tab.setContent(content);

            // Custom tab header
            FXMLLoader headerLoader = new FXMLLoader(
                    getClass().getResource("/view/custom-tab.fxml")
            );
            Parent header = headerLoader.load();

            // Set Dynamic Tab Title
            CustomTabController c = headerLoader.getController();
            c.setTab(tab);
            c.setTitle(title);
            // store controller name
            tab.getProperties().put("headerController", c);

            tab.setGraphic(header);

            int plusIndex = tabPane.getTabs().indexOf(newTabButton);
            tabPane.getTabs().add(plusIndex, tab);

            tabPane.getSelectionModel().select(tab);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //    New Tab
    private void createPlusTab() {
        newTabButton = new Tab("+ New Tab");
        newTabButton.setClosable(false);
        newTabButton.setContent(new Pane());

        newTabButton.setOnSelectionChanged(e -> {
            if (newTabButton.isSelected()) {
                try {
                    openDashboardTab();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        tabPane.getTabs().add(newTabButton);
    }




    private void openDashboardTab() throws IOException {

        addNewTab("Dashboard", "/view/test-dashboard.fxml");
    }

}
