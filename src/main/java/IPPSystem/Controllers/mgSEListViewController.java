package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.users;
import IPPSystem.Utils.PaginationHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class mgSEListViewController {

    @FXML
    private Button managerSpCreateBtn;

    @FXML
    private ComboBox<?> managerSpProjectTypeCombo;

    @FXML
    private ComboBox<String> managerSpStatusCombo;

    @FXML
    private VBox managerSupervisorListPane;

    @FXML
    private HBox paginationBox;

    @FXML
    private Button paginationNextBtn;

    @FXML
    private Button paginationPrevBtn;

    public List<users> allEngineers = new ArrayList<>();

    private PaginationHelper<users> pagination;

    @FXML
    public void initialize() {

        allEngineers =
                userDatabase.getUserByRole(role.SUPERVISOR.toString());

        pagination = new PaginationHelper<>(5);

        pagination.setOnPageChanged(this::renderPage);

        pagination.setData(allEngineers);

        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);
    }


    private void renderPage(List<users> pageData) {

        managerSupervisorListPane.getChildren().clear();

        for (users engineer : pageData) {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/View/mgSERow.fxml"));

                Parent row = loader.load();
                mgSERowController controller = loader.getController();

                controller.setData(engineer);
                controller.setOnDelete(this::refreshUI);

                managerSupervisorListPane.getChildren().add(row);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        pagination.buildButtons(paginationBox);
    }


    private void refreshUI() {

        allEngineers.sort(
                (a, b) -> Boolean.compare(b.isActive(), a.isActive())
        );

        pagination.setData(allEngineers);
        pagination.goToPage(1);
        pagination.buildButtons(paginationBox);
    }



}

