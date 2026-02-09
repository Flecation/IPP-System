package IPPSystem.Controllers;

import IPPSystem.Constants.notificationType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class confirmBoxController {

    @FXML private StackPane confirmRoot;
    @FXML private VBox confirmMessageBox;
    @FXML private Label confirmMessageIcon, confirmMessageTitle, confirmMessageLbl;
    @FXML private Button confirmMessageCancelBtn, confirmMessageConfirmBtn;

    public void show(String title, String message, notificationType type,
                     Runnable onConfirm, Runnable onCancel) {

        setIcon(confirmMessageIcon, type, 20);

        confirmMessageTitle.setText(title);
        confirmMessageLbl.setText(message);

        confirmRoot.setVisible(true);
        confirmRoot.setManaged(true);

        // center confirm box inside overlay
        centerBox();

        confirmRoot.widthProperty().addListener((o,a,b) -> centerBox());
        confirmRoot.heightProperty().addListener((o,a,b) -> centerBox());

        confirmMessageCancelBtn.setOnAction(e -> {
            hide();
            if (onCancel != null) onCancel.run();
        });

        confirmMessageConfirmBtn.setOnAction(e -> {
            hide();
            if (onConfirm != null) onConfirm.run();
        });
    }

    public void hide() {
        confirmRoot.setVisible(false);
        confirmRoot.setManaged(false);
    }

    public boolean isShowing() {
        return confirmRoot.isVisible();
    }

    private void centerBox() {
        if (confirmRoot == null || confirmMessageBox == null) return;
        confirmMessageBox.applyCss();
        confirmMessageBox.layout();

        double x = (confirmRoot.getWidth() - confirmMessageBox.getWidth()) / 2.0;
        double y = (confirmRoot.getHeight() - confirmMessageBox.getHeight()) / 2.0;
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        confirmMessageBox.setLayoutX(x);
        confirmMessageBox.setLayoutY(y);
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
