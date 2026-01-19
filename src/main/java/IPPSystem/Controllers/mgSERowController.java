package IPPSystem.Controllers;

import IPPSystem.Models.users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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

    private  users engineer;

    public void setData(users engineer){
        this.engineer = engineer;

        mgSENameTxt.setText(engineer.getUserName());
        mgSERoleTxt.setText(engineer.getUserRole());
        mgSEActive.setText(
                engineer.isActive() ? "Active" : "Inactivve"
        );

        mgSEProjectTypeTxt.setText("-");
    }





    @FXML
    void clickMgSEDeleteBtn(ActionEvent event) {

    }

    @FXML
    void clickMgSEViewBtn(ActionEvent event) {

    }

}
