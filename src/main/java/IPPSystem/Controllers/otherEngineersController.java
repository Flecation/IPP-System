//package IPPSystem.Controllers;
//
//import IPPSystem.Constants.role;
//import IPPSystem.Models.users;
//import javafx.fxml.FXML;
//import javafx.scene.control.Label;
//import javafx.scene.layout.HBox;
//
//public class otherEngineersController {
//
//    @FXML
//    private Label otherEngineersName;
//
//    @FXML
//    private Label otherEngineersStatus;
//
//    @FXML
//    private HBox otherEngineerPane;
//
//    private  users engineer;
//
//    public void setOtherEngineer(users engineer, boolean isCurrent) {
//        this.engineer = engineer;
//
//        otherEngineersName.setText(engineer.getUserName());
//        String statusText = engineer.isActive() ? "Active" : "Inactive";
//        otherEngineersStatus.setText(statusText);
//
//        // Highlight if this is the current engineer
//        if (isCurrent) {
////            otherEngineersName.setStyle("-fx-font-weight: bold; -fx-text-fill: #2A73FF;");
////            otherEngineersStatus.setStyle("-fx-text-fill: #2A73FF;");
//
//
//            otherEngineerPane.getStyleClass().add("active-engineer");
//        }else {
//            otherEngineerPane.getStyleClass().add("other-engineer");
//        }
//    }
//
//
//}




package IPPSystem.Controllers;

import IPPSystem.Models.users;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class otherEngineersController {

    @FXML
    private Label otherEngineersName;

    @FXML
    private Label otherEngineersStatus;

    @FXML
    private HBox otherEngineerPane;

    private users engineer;
    private mgSEPersonalDetailController parentController; // 👈 reference

    public void setOtherEngineer(users engineer, boolean isCurrent,
                                 mgSEPersonalDetailController parent) {
        this.engineer = engineer;
        this.parentController = parent;

        otherEngineersName.setText(engineer.getUserName());
        otherEngineersStatus.setText(engineer.isActive() ? "Active" : "Inactive");

        if (isCurrent) {
            otherEngineerPane.getStyleClass().add("active-engineer");
        }else {
            otherEngineerPane.getStyleClass().add("other-engineer");
        }

        otherEngineerPane.setOnMouseClicked(e -> {
            parentController.setEngineer(engineer);
        });
    }
}
