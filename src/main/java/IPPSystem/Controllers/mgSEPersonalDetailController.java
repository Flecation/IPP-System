package IPPSystem.Controllers;

import IPPSystem.DAO.database;
import IPPSystem.Models.users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class mgSEPersonalDetailController {

    @FXML
    private TextArea address;

    @FXML
    private DatePicker dob;

    @FXML
    private TextField email;

    @FXML
    private Button personalBtn;

    @FXML
    private StackPane personalInfoBodyPane;

    @FXML
    private TextField phone;

    @FXML
    private Button projectBtn;

    @FXML
    private TextField projectType;


        public void setEngineer(users engineer) {

            email.setText(engineer.getUserEmail());
            phone.setText(engineer.getUserPhone());
//            address.setText(engineer.getUserAddress());

            projectType.setText(
                    database.currentAssignProject(engineer.getUserId())
            );
        }



    @FXML
    void clickPersonal(ActionEvent event) {

    }

    @FXML
    void clickProjects(ActionEvent event) {

    }

}
