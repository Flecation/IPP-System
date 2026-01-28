package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.DAO.database;
import IPPSystem.DAO.userDatabase;
import IPPSystem.Models.users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class mgSERowController {

    @FXML
    private Label mgSEActive;

    @FXML
    private Button mgSEDeleteBtn;

    @FXML
    private ImageView mgSEImg;

    @FXML
    private Label mgSENameTxt;

    @FXML
    private Label mgSEProjectTypeTxt;

    @FXML
    private Label mgSERoleTxt;

    @FXML
    private Button mgSEViewBtn;

    @FXML
    private HBox ASiteEngineerCtn;

    private  users engineer;



    // notify parent when view is clicked
    private Consumer<users> onView;

    public void setOnView(Consumer<users> onView) {
        this.onView = onView;
    }



    public void setData(users engineer) {
        this.engineer = engineer;

        mgSENameTxt.setText(engineer.getUserName());
        mgSERoleTxt.setText(engineer.getUserRole());
        mgSEProjectTypeTxt.setText(database.currentAssignProject(engineer.getUserId()));


        String status = engineer.isActive() ? "Active" : "Inactive";
        mgSEActive.setText(status);

        ASiteEngineerCtn.getStyleClass().removeAll(
                "managerAccSupervisorCtn",
                "managerAccSupervisorCtn-Inactive"
        );

        if (!engineer.isActive()) {
            ASiteEngineerCtn.getStyleClass()
                    .add("managerAccSupervisorCtn-Inactive");
        } else {
            ASiteEngineerCtn.getStyleClass()
                    .add("managerAccSupervisorCtn");
        }
    }





    // notify to parent
    private Runnable onDelete;

    public void setOnDelete(Runnable onDelete) {
        this.onDelete = onDelete;


    }

    @FXML
    public void clickMgSEDeleteBtn(ActionEvent event) {

        boolean updated = database.deleteUser(engineer.getUserId());


        if (!updated) {
            engineer.setActive(false);
            if (onDelete != null) {
                onDelete.run();
            }
        }

    }


    @FXML
    void clickMgSEViewBtn(ActionEvent event) {
        if (onView != null) {
            onView.accept(engineer);
        }
    }





}




