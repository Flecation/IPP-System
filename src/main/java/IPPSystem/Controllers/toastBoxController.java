package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class toastBoxController {

    @FXML private HBox toastBox;
    @FXML private Label toastIcon, toastTitle, toastMessage;

    private Runnable onCleanup;

    public void show(String title, String message, notificationType type, Runnable onCleanup) {
        this.onCleanup = onCleanup;

        setIcon(toastIcon, type, 18);

        toastBox.getStyleClass().removeAll(
                "warning-toast","wrong-toast","success-toast","info-toast","error-toast"
        );
        toastBox.getStyleClass().add(type.toString() + "-toast");

        toastTitle.setText(title);
        toastMessage.setText(message);

        toastBox.setVisible(true);
        toastBox.setManaged(true);

        playAnim();
    }

    public void hide() {
        toastBox.setVisible(false);
        toastBox.setManaged(false);
    }

    public boolean isShowing() {
        return toastBox.isVisible();
    }

    private void playAnim() {
        toastBox.applyCss();
        toastBox.layout();

        double hiddenY = -120.0;
        toastBox.setTranslateY(hiddenY);

        TranslateTransition down = new TranslateTransition(Duration.millis(350), toastBox);
        down.setFromY(hiddenY);
        down.setToY(0);

        PauseTransition stay = new PauseTransition(Duration.millis(1200));

        TranslateTransition up = new TranslateTransition(Duration.millis(350), toastBox);
        up.setFromY(0);
        up.setToY(hiddenY);

        down.play();
        down.setOnFinished(e -> stay.play());
        stay.setOnFinished(e -> up.play());
        up.setOnFinished(e -> {
            hide();
            if (onCleanup != null) onCleanup.run();
        });
    }

    private static void setIcon(Label label, notificationType type, int size) {
        FontAwesomeSolid fa = switch (type) {
            case WARNING -> FontAwesomeSolid.EXCLAMATION_TRIANGLE;
            case SUCCESS -> FontAwesomeSolid.CHECK;
            case WRONG, ERROR -> FontAwesomeSolid.TIMES;
            case INFO -> FontAwesomeSolid.INFO_CIRCLE;
        };
        FontIcon icon = new FontIcon(fa);
        icon.setIconSize(size);
        label.setGraphic(icon);
        label.setText("");
    }
}
