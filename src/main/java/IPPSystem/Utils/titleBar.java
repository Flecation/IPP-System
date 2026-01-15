package IPPSystem.Utils;

import javafx.animation.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class titleBar extends effect{

//    For all pane of the title bar
    private static Parent root;

    public static void setTitleBar(Parent basePane, Button minimizeBtn, Button restoreBtn, Button exitBtn){
        root = basePane;

        FontIcon sizeIcon = new FontIcon("fas-window-restore");
        sizeIcon.setIconSize(12);
        restoreBtn.setGraphic(sizeIcon);
        setToolTip(restoreBtn,"restore");

        FontIcon miniIcon = new FontIcon("fas-window-minimize");
        miniIcon.setIconSize(12);
        minimizeBtn.setGraphic(miniIcon);
        setToolTip(minimizeBtn,"minimize");

        FontIcon exitIcon = new FontIcon("fas-times");
        exitIcon.setIconSize(12);
        exitBtn.setGraphic(exitIcon);
        setToolTip(exitBtn,"exit");


        minimizeBtn.setOnAction(event -> {
            minimizeEffect();
        });

        restoreBtn.setOnAction(event -> {
            restoreEffect();
        });

        exitBtn.setOnAction(event -> {
            exitEffect();
        });

    }

    private static void exitEffect(){

        Region region = new Region();
        region.setPrefSize(root.getBoundsInLocal().getWidth(), root.getBoundsInLocal().getHeight());
        region.setManaged(false);
        region.setMouseTransparent(true);
        region.setOpacity(0);

        if(root instanceof Pane pane) {
            pane.getChildren().add(region);
            region.toFront();
        }

        GaussianBlur blur = new GaussianBlur(1);
        root.setEffect(blur);

        Timeline blurIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(blur.radiusProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(region.opacityProperty(), 0, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(blur.radiusProperty(), 5, Interpolator.EASE_BOTH),
                        new KeyValue(region.opacityProperty(), 0.5, Interpolator.EASE_BOTH)
                )

        );

        blurIn.setOnFinished(event -> {
            System.exit(0);
        });

        blurIn.play();
    }

    private static void minimizeEffect(){
        Stage stage = (Stage) root.getScene().getWindow();
        FadeTransition fade = new FadeTransition(Duration.millis(180), root);
        fade.setFromValue(1);
        fade.setToValue(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(180), root);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(0.85);
        scale.setToY(0.85);

        ParallelTransition animation = new ParallelTransition(fade, scale);

        animation.setOnFinished(e -> {
            stage.setIconified(true);
            root.setOpacity(1);
            root.setScaleX(1);
            root.setScaleY(1);
        });

        animation.play();

    }

    private static void restoreEffect(){
        Stage stage = (Stage) root.getScene().getWindow();
        double minWidth = 600;
        double minHeight = 500;

        Region region = new Region();
        region.setPrefSize(root.getBoundsInLocal().getWidth(), root.getBoundsInLocal().getHeight());
        region.setManaged(false);
        region.setMouseTransparent(true);
        region.setOpacity(0);

        if(root instanceof Pane pane) {
            pane.getChildren().add(region);
            region.toFront();
        }

        GaussianBlur blur = new GaussianBlur(0);
        root.setEffect(blur);

        Timeline blurIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(blur.radiusProperty(), 0, Interpolator.EASE_BOTH),
                        new KeyValue(region.opacityProperty(), 0, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(blur.radiusProperty(), 2, Interpolator.EASE_BOTH),
                        new KeyValue(region.opacityProperty(), 0.25, Interpolator.EASE_BOTH)
                )

        );
        blurIn.setOnFinished(event -> {
            Timeline blurOut = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(blur.radiusProperty(), 2, Interpolator.EASE_BOTH),
                            new KeyValue(region.opacityProperty(), 0.25, Interpolator.EASE_BOTH)
                    ),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(blur.radiusProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(region.opacityProperty(), 0, Interpolator.EASE_BOTH)
                    )
            );
            blurOut.setOnFinished(e -> {
                if(stage.isMaximized()){
                    stage.setWidth(minWidth);
                    stage.setHeight(minHeight);
                    stage.setMaximized(false);
                }else{
                    stage.setMaximized(true);
                }
                root.setEffect(null);
                if (root instanceof Pane p) {
                    p.getChildren().remove(region);
                }
            });
            blurOut.play();
        });
        blurIn.play();

    }
}
