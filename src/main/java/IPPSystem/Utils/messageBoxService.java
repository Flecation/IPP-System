package IPPSystem.Utils;

import IPPSystem.Constants.notificationType;
import IPPSystem.Controllers.confirmBoxController;
import IPPSystem.Controllers.toastBoxController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;


public final class messageBoxService {

    private static Parent toastNode;
    private static toastBoxController toastCtrl;

    private static Parent confirmNode;
    private static confirmBoxController confirmCtrl;

    private static boolean loaded = false;
    private static Pane currentHost;

    private messageBoxService() {}


    /**
     * Use this when toast/confirm are already included in a parent FXML (fx:include).
     * No loading, no adding nodes again — just bind controllers so other pages can call messageBoxService.toast/confirm().
     */
    public static void bindIncluded(Parent toastNodeParam,
                                    toastBoxController toastCtrlParam,
                                    Parent confirmNodeParam,
                                    confirmBoxController confirmCtrlParam) {
        toastNode = toastNodeParam;
        toastCtrl = toastCtrlParam;
        confirmNode = confirmNodeParam;
        confirmCtrl = confirmCtrlParam;
        loaded = (toastNode != null && toastCtrl != null && confirmNode != null && confirmCtrl != null);

        // default interaction behavior
        if (toastNode != null) toastNode.setMouseTransparent(true);
        if (confirmNode != null) confirmNode.setMouseTransparent(false);

        // bring to front if possible
        if (toastNode != null) toastNode.toFront();
        if (confirmNode != null) confirmNode.toFront();
    }

    public static void init(Pane hostPane) {
        try {
            if (!loaded) {
                // toast root is HBox
                FXMLLoader toastLoader = new FXMLLoader(messageBoxService.class.getResource("/View/toastBox.fxml"));
                toastNode = toastLoader.load();
                toastCtrl = toastLoader.getController();

                // confirm root is AnchorPane overlay
                FXMLLoader confirmLoader = new FXMLLoader(messageBoxService.class.getResource("/View/confirmBox.fxml"));
                confirmNode = confirmLoader.load();
                confirmCtrl = confirmLoader.getController();

                loaded = true;
            }

            // move nodes if host changed (login -> navigation)
            if (currentHost != null && currentHost != hostPane) {
                currentHost.getChildren().removeAll(toastNode, confirmNode);
            }
            currentHost = hostPane;

            if (!hostPane.getChildren().contains(toastNode)) hostPane.getChildren().add(toastNode);
            if (!hostPane.getChildren().contains(confirmNode)) hostPane.getChildren().add(confirmNode);

            // always on top
            toastNode.toFront();
            confirmNode.toFront();

            // toast position TOP-LEFT (no wrapper)
            // toast position TOP-LEFT
            if (hostPane instanceof StackPane) {
                StackPane.setAlignment(toastNode, Pos.TOP_LEFT);
                StackPane.setMargin(toastNode, new Insets(16, 0, 0, 16));
            } else {
                toastNode.setLayoutX(16);
                toastNode.setLayoutY(16);
            }



            // confirm overlay fill whole host
            confirmNode.layoutBoundsProperty().addListener((o,a,b) -> confirmNode.toFront());
            confirmNode.resize(hostPane.getWidth(), hostPane.getHeight());
            hostPane.widthProperty().addListener((o,a,b) -> confirmNode.resize(b.doubleValue(), hostPane.getHeight()));
            hostPane.heightProperty().addListener((o,a,b) -> confirmNode.resize(hostPane.getWidth(), b.doubleValue()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to init messageBoxService", e);
        }
    }

    public static void toast(String title, String message, notificationType type) {
        if (!loaded) return;
        toastNode.toFront();
        toastCtrl.show(title, message, type, null);
    }

    public static void confirm(String title, String message, notificationType type,
                               Runnable onConfirm, Runnable onCancel) {
        if (!loaded) return;
        confirmNode.toFront();
        confirmCtrl.show(title, message, type, onConfirm, onCancel);
    }
}
