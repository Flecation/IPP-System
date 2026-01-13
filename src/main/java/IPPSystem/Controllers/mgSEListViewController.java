package IPPSystem.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class mgSEListViewController {

    @FXML
    private Button managerSpCreateBtn;

    @FXML
    private ComboBox<?> managerSpProjectTypeCombo;

    @FXML
    private ComboBox<?> managerSpStatusCombo;

    @FXML
    private VBox managerSupervisorListPane;

    @FXML
    private Button paginationBtn1;

    @FXML
    private Button paginationBtn2;

    @FXML
    private Button paginationBtn3;

    @FXML
    private Button paginationNextBtn;

    @FXML
    private Button paginationPrevBtn;

    @FXML
    void clickon1(ActionEvent event) {

    }

    @FXML
    void clickon2(ActionEvent event) {

    }

    @FXML
    void clickon3(ActionEvent event) {

    }

    @FXML
    void clickonnext(ActionEvent event) {

    }

    @FXML
    void clickonprev(ActionEvent event) {

    }

    public void initialize() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/mgSERow.fxml"));
        Parent row = fxmlLoader.load();
        managerSupervisorListPane.getChildren().add(row);
    }

}

