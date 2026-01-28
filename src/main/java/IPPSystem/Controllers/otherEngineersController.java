package IPPSystem.Controllers;

import IPPSystem.Constants.role;
import IPPSystem.Models.users;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class otherEngineersController {

    @FXML
    private Label otherEngineersName;

    @FXML
    private Label otherEngineersStatus;

    private  users engineer;

    public void setOtherEngineer(users engineer) {
        this.engineer = engineer;
        otherEngineersName.setText(engineer.getUserName());
        String statusText = engineer.isActive() ? "Active" : "Inactive";
        otherEngineersStatus.setText(statusText);
    }

}
