package IPPSystem.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class CustomTabController {

    @FXML
    private Label titleLabel;

    @FXML
    private Button closeBtn;

    private Tab tab;

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    @FXML
    private void initialize() {
        closeBtn.setOnAction(e -> {
            tab.getTabPane().getTabs().remove(tab);
        });
    }
}
