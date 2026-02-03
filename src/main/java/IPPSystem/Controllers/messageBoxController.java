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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class messageBoxController {

    @FXML
    private StackPane messageBoxRoot;

    @FXML
    VBox confirmMessageBox;

    @FXML
    HBox toastBox;

    @FXML
    Label confirmMessageIcon,confirmMessageTitle,confirmMessageLbl,toastIcon,toastTitle,toastMessage;

    @FXML
    Button confirmMessageCancelBtn,confirmMessageConfirmBtn;

    private Parent parent;
    private Runnable onCleanup;

    public messageBoxController(){}

    public void confirmMessage(String title, String message, notificationType type,
                               Runnable onConfirm, Runnable onCancel) {

        FontIcon icon = switch (type) {
            case WARNING -> new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
            case SUCCESS -> new FontIcon(FontAwesomeSolid.CHECK);
            case WRONG   -> new FontIcon(FontAwesomeSolid.TIMES);
            default      -> new FontIcon(FontAwesomeSolid.LAPTOP);
        };
        icon.setIconSize(20);
        confirmMessageIcon.setGraphic(icon);
        confirmMessageIcon.setText("");

        confirmMessageTitle.setText(title);
        confirmMessageLbl.setText(message);

        // show root and block clicks (modal)
        messageBoxRoot.setVisible(true);
        messageBoxRoot.setManaged(true);
        messageBoxRoot.setMouseTransparent(false);

        toastBox.setVisible(false);
        confirmMessageBox.setVisible(true);

        confirmMessageCancelBtn.setOnAction(e -> {
            hideAll();
            if (onCancel != null) onCancel.run();
        });

        confirmMessageConfirmBtn.setOnAction(e -> {
            hideAll();
            if (onConfirm != null) onConfirm.run();
        });
    }

    public void toastMessage(String title, String message, notificationType type, Runnable onCleanup) {
        this.onCleanup = onCleanup;
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
        toastBox.getStyleClass().add(type.toString() + "-toast");

        toastTitle.setText(title);
        toastMessage.setText(message);

        // show root, but don't block clicks
        messageBoxRoot.setVisible(true);
        messageBoxRoot.setManaged(true);
        messageBoxRoot.setMouseTransparent(true);

        confirmMessageBox.setVisible(false);
        showToastAnimation();
    }

    private void showAlert(Parent box){
        box.applyCss();
        box.layout();

        Parent overlay = box.getParent();
        if (overlay != null) {
            overlay.setVisible(true);
            overlay.setMouseTransparent(false);
            overlay.toFront();
        }

        // Use fixed offset instead of dynamic translate
        double hiddenY = -120.0;

        box.setVisible(true);
        box.setTranslateY(hiddenY);

        TranslateTransition down = new TranslateTransition(Duration.millis(600), box);
        down.setFromY(hiddenY);
        down.setToY(0);

        PauseTransition stay = new PauseTransition(Duration.millis(500)); // give user more time

        TranslateTransition up = new TranslateTransition(Duration.millis(600), box);
        up.setFromY(0);
        up.setToY(hiddenY);

        down.play();
        down.setOnFinished(e -> stay.play());
        stay.setOnFinished(e -> up.play());
        up.setOnFinished(e -> {
            box.setVisible(false);
            closeOverlay(box);
            if (onCleanup != null) onCleanup.run();
        });
    }

    private void closeOverlay(Parent child) {
        Parent overlay = child.getParent();
        if (overlay != null && overlay.getId() != null && overlay.getId().equals("messageBoxRoot")) {
            overlay.setVisible(false);
            overlay.setMouseTransparent(true);
            if (overlay.getParent() instanceof Pane parentPane) {
                parentPane.getChildren().remove(overlay);
            }
        }
    }

    private void showToastAnimation() {
        toastBox.applyCss();
        toastBox.layout();

        toastBox.setVisible(true);
        toastBox.setManaged(true);

        double hiddenY = -120.0;
        toastBox.setTranslateY(hiddenY);

        TranslateTransition down = new TranslateTransition(Duration.millis(400), toastBox);
        down.setFromY(hiddenY);
        down.setToY(0);

        PauseTransition stay = new PauseTransition(Duration.millis(1200));

        TranslateTransition up = new TranslateTransition(Duration.millis(400), toastBox);
        up.setFromY(0);
        up.setToY(hiddenY);

        down.play();
        down.setOnFinished(e -> stay.play());
        stay.setOnFinished(e -> up.play());
        up.setOnFinished(e -> {
            toastBox.setVisible(false);
            toastBox.setManaged(false);
            hideRootIfNothingVisible();
            if (onCleanup != null) onCleanup.run();
        });
    }

    private void hideAll() {
        confirmMessageBox.setVisible(false);
        confirmMessageBox.setManaged(false);

        toastBox.setVisible(false);
        toastBox.setManaged(false);

        messageBoxRoot.setVisible(false);
        messageBoxRoot.setManaged(false);
        messageBoxRoot.setMouseTransparent(true);
    }

    private void hideRootIfNothingVisible() {
        if (!confirmMessageBox.isVisible() && !toastBox.isVisible()) {
            messageBoxRoot.setVisible(false);
            messageBoxRoot.setManaged(false);
            messageBoxRoot.setMouseTransparent(true);
        }
    }

}