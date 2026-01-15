package IPPSystem.Utils;

import IPPSystem.Main.HelloApplication;
import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class switchPage extends utils {

    // Dashboard page switch animation
    public static void setSwitchPane(
            StackPane basePane,
            Parent fromPane,
            String toPane,
            Button titleUrlButton,
            Button clickedButton
    ) {

        // Loading spinner
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(14, 14);
        titleUrlButton.setGraphic(spinner);
        titleUrlButton.setText("Loading...");

        // Load next pane safely
        Parent nextPane;
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(toPane)
            );
            nextPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            titleUrlButton.setGraphic(null);
            titleUrlButton.setText("Error");
            return;
        }

        // Overlay region
        Region region = new Region();
        region.prefWidthProperty().bind(basePane.widthProperty());
        region.prefHeightProperty().bind(basePane.heightProperty());
        region.setManaged(false);
        region.setMouseTransparent(true);
        region.setOpacity(0);

        // Blur effect
        GaussianBlur blur = new GaussianBlur(0);
        fromPane.setEffect(blur);

        Timeline blurIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(blur.radiusProperty(), 0),
                        new KeyValue(region.opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(blur.radiusProperty(), 5),
                        new KeyValue(region.opacityProperty(), 0.5)
                )
        );

        blurIn.setOnFinished(event -> {

            // Update title button
            titleUrlButton.setGraphic(null);
            setToolTip(titleUrlButton, clickedButton.getText());
            titleUrlButton.setText(clickedButton.getText());

            // Switch panes
            basePane.getChildren().addAll(region, nextPane);
            basePane.getChildren().remove(fromPane);

            Timeline blurOut = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(blur.radiusProperty(), 5),
                            new KeyValue(region.opacityProperty(), 0.5)
                    ),
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(blur.radiusProperty(), 0),
                            new KeyValue(region.opacityProperty(), 0)
                    )
            );

            blurOut.setOnFinished(e -> {
                nextPane.setEffect(null);
                basePane.getChildren().remove(region);
            });

            blurOut.play();
        });

        blurIn.play();
    }

    // Simple FXML opener utility
    public static Parent openFxml(String fxmlFile) {
        try {
            return FXMLLoader.load(
                    HelloApplication.class.getResource(fxmlFile)
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlFile, e);
        }
    }

    //from the login controller to the dashboard with animation
    public static void switchScene(Button button, String fxmlPath) {

        String fxml = "/View/" + fxmlPath;

        String originalText = button.getText();
        Node originalGraphic = button.getGraphic();

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(16, 16);

        button.setText("Loading...");
        button.setGraphic(spinner);
        button.setDisable(true);

        Stage stage = (Stage) button.getScene().getWindow();
        Parent oldRoot = stage.getScene().getRoot();

        GaussianBlur blur = new GaussianBlur(0);
        oldRoot.setEffect(blur);

        // Allow UI to render spinner
        PauseTransition pause = new PauseTransition(Duration.millis(80));
        pause.setOnFinished(p -> {

            Timeline fadeOut = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(oldRoot.opacityProperty(), 1),
                            new KeyValue(blur.radiusProperty(), 0)
                    ),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(oldRoot.opacityProperty(), 0),
                            new KeyValue(blur.radiusProperty(), 6)
                    )
            );

            fadeOut.setOnFinished(event -> {

                Parent newRoot;
                try {
                    newRoot = FXMLLoader.load(
                            HelloApplication.class.getResource(fxml)
                    );
                } catch (IOException e) {
                    button.setText(originalText);
                    button.setGraphic(originalGraphic);
                    button.setDisable(false);
                    oldRoot.setEffect(null);
                    e.printStackTrace();
                    return;
                }

                newRoot.setOpacity(0);
                GaussianBlur newBlur = new GaussianBlur(6);
                newRoot.setEffect(newBlur);

                Scene newScene = new Scene(newRoot);
                newScene.setFill(Color.TRANSPARENT);

                stage.setScene(newScene);
                stage.setMaximized(true);

                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(newRoot.opacityProperty(), 0),
                                new KeyValue(newBlur.radiusProperty(), 6)
                        ),
                        new KeyFrame(Duration.millis(300),
                                new KeyValue(newRoot.opacityProperty(), 1),
                                new KeyValue(newBlur.radiusProperty(), 0)
                        )
                );

                fadeIn.setOnFinished(e -> newRoot.setEffect(null));
                fadeIn.play();
            });

            fadeOut.play();
        });

        pause.play();
    }

}
