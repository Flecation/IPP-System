package IPPSystem.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;


public class ManagerDashboard {
    @FXML
    private StackPane chartPane;

    @FXML
    private Circle outerCircle, innerCircle;

    @FXML
    private Label percentLabel;

    @FXML
    public void initialize() {

        // Outer circle
        outerCircle.radiusProperty().bind(
                chartPane.widthProperty().divide(2.8)
        );

        // Inner circle (hole)
        innerCircle.radiusProperty().bind(
                chartPane.widthProperty().divide(3.6)
        );
        percentLabel.styleProperty().set("-fx-font-size: 18px;");
    }



}
