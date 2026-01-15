package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class messageBoxController {
    @FXML
    VBox confirmMessageBox;

    @FXML
    HBox toastBox;

    @FXML
    Label confirmMessageIcon,confirmMessageTitle,confirmMessageLbl,toastIcon,toastTitle,toastMessage;

    @FXML
    Button confirmMessageCancelBtn,confirmMessageConfirmBtn;

    private Parent parent;

    public messageBoxController(){}

    public void confirmMessage(Parent root,String title,String message,notificationType type){
        parent = root;

        FontIcon icon = switch (type) {
            case WARNING -> new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
            case SUCCESS -> new FontIcon(FontAwesomeSolid.CHECK);
            case WRONG   -> new FontIcon(FontAwesomeSolid.TIMES);
            default      -> new FontIcon(FontAwesomeSolid.DESKTOP);
        };
        icon.setIconSize(20);
        confirmMessageIcon.setGraphic(icon);
        confirmMessageIcon.setText("");

        confirmMessageTitle.setText(title);
        confirmMessageLbl.setText(message);

        confirmMessageBox.setVisible(true);

        if (confirmMessageCancelBtn != null) {
            confirmMessageCancelBtn.setOnAction(e -> confirmMessageBox.setVisible(false));
        }
        if (confirmMessageConfirmBtn != null) {
            confirmMessageConfirmBtn.setOnAction(e -> confirmMessageBox.setVisible(false));
        }
    }

    public void toastMessage(Parent root, String title, String message, notificationType type) {
        // Create the icon using Ikonli
        parent = root;

        FontIcon icon = switch (type) {
            case WARNING -> new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
            case SUCCESS -> new FontIcon(FontAwesomeSolid.CHECK);
            case WRONG   -> new FontIcon(FontAwesomeSolid.TIMES);
            default      -> new FontIcon(FontAwesomeSolid.DESKTOP);
        };
        icon.setIconSize(18);
        toastIcon.setGraphic(icon);
        toastIcon.setText("");

        toastBox.getStyleClass().removeAll("warning-toast","wrong-toast","success-toast","info-toast");
        toastBox.getStyleClass().add( type.toString()+ "-toast");

        toastTitle.setText(title);
        toastMessage.setText(message);
        showAlert(toastBox);
    }

    private void showAlert(Parent box){
        box.applyCss();
        box.layout();

        // ensure overlay is interactive and on top while showing
        Parent overlay = box.getParent();
        if (overlay != null) {
            overlay.setVisible(true);
            overlay.setMouseTransparent(false);
            overlay.toFront();
        }

        double hiddenY = box.getTranslateY() + 20;

        box.setVisible(true);
        box.setTranslateY(-hiddenY);

        TranslateTransition down = new TranslateTransition(Duration.millis(300), box);
        down.setFromY(-hiddenY);
        down.setToY(0);

        PauseTransition stay = new PauseTransition(Duration.seconds(0.5));

        TranslateTransition up = new TranslateTransition(Duration.millis(300), box);
        up.setFromY(0);
        up.setToY(-hiddenY);

        down.play();
        down.setOnFinished(e -> stay.play());
        stay.setOnFinished(e -> up.play());
        up.setOnFinished(e -> {
            box.setVisible(false);
            closeOverlay(box);
        });
    }

    private void closeOverlay(Parent child) {
        Parent overlay = child.getParent();
        if (overlay != null) {
            overlay.setVisible(false);
            overlay.setMouseTransparent(true);
            if (overlay.getParent() instanceof Pane pane) {
                pane.getChildren().remove(overlay);
            }
        }
    }
}