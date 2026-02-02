package IPPSystem.Utils;

import IPPSystem.Constants.notificationType;
import IPPSystem.Controllers.messageBoxController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.net.URL;

public final class messageBoxService {

    private static messageBoxController controller;
    private static Parent view;
    private static boolean initialized = false;

    private messageBoxService() {}

    public static void init(Pane hostPane) {
//        if (initialized) return;

        try {
            URL fxml = messageBoxService.class.getResource("/View/messageBox.fxml");
            if (fxml == null) {
                throw new IllegalStateException(
                        "Cannot find /View/messageBox.fxml on classpath. " +
                                "Make sure it's inside src/main/resources/View/"
                );
            }

            FXMLLoader loader = new FXMLLoader(fxml);
            view = loader.load();
            controller = loader.getController();
            hostPane.getChildren().add(view);
            initialized = true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load messageBox.fxml", e);
        }
    }

    public static void toast(String title, String message, notificationType type) {
        if (!initialized) return;
        controller.toastMessage(title, message, type,null);
    }


public static void confirm(String title, String message, notificationType type,
                               Runnable onConfirm, Runnable onCancel) {
        if (!initialized) return;
        controller.confirmMessage(title, message, type, onConfirm, onCancel);
    }
}
