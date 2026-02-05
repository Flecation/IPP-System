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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.File;
import java.util.function.Consumer;

public class engineerRowController {

    @FXML
    private Label mgSEActive;

    @FXML
    private Button mgSEDeleteBtn;

    @FXML
    private Circle mgSEImg;

    @FXML
    private Label mgSENameTxt;

    @FXML
    private Label mgSEProjectTypeTxt;


    @FXML
    private Button mgSEViewBtn;

    @FXML
    private HBox ASiteEngineerCtn;


    @FXML
    private Label EndDate;

    @FXML
    private Label StartDate;

    private users engineer;



    // notify parent when view is clicked
    private Consumer<users> onView;

    public void setOnView(Consumer<users> onView) {
        this.onView = onView;
    }



    public void setData(users engineer) {
        this.engineer = engineer;

        mgSENameTxt.setText(engineer.getUserName());
        if(database.currentAssignProject((engineer.getUserId())) == null){
            mgSEProjectTypeTxt.setText("-");
        }else{
            mgSEProjectTypeTxt.setText(database.currentAssignProject(engineer.getUserId()));
        }


        String status = engineer.isActive() ? "Active" : "Inactive";
        mgSEActive.setText(status);


        Image img = loadProfileImage(engineer.getUserPhoto());
        mgSEImg.setFill(new ImagePattern(img));

        String start = engineer.getUserStartDate() != null ? engineer.getUserStartDate().toString() : "N/A";
        String end = engineer.getUserEndDate() != null ? engineer.getUserEndDate().toString() : "Present";

        StartDate.setText(start);
        EndDate.setText(end);


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


    private Image loadProfileImage(String path) {

        if (path != null && !path.isBlank()) {
            File f = new File(path);
            if (f.exists()) {
                return new Image(f.toURI().toString());
            }
        }

        return new Image(
                getClass().getResource("/assets/profile/default.png").toExternalForm()
        );
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




