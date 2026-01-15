package IPPSystem.Utils;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class themeToggle {
    private static themeToggle instance;

    private static String lightTheme,darkTheme;
    private static Parent root;

    private static final SimpleBooleanProperty darkModeProperty = new SimpleBooleanProperty(false);

    public static BooleanProperty darkModeProperty(){
        return darkModeProperty;
    }

    public static boolean isDarkMode(){
        return darkModeProperty.get();
    }

    private themeToggle(){}

    public static themeToggle getInstance(){
        if (instance == null) instance = new themeToggle();
        return instance;
    }

    public void setTheme(Parent basePane, String lightCss, String darkCss){
        root = basePane;
        lightTheme = lightCss;
        darkTheme = darkCss;
        applyThemeFirstTIme();
    }

    public static Parent getRoot() {
        return root;
    }

    public static void setRoot(Parent root) {
        themeToggle.root = root;
    }

    public void applyThemeFirstTIme(){
        root.getStylesheets().removeAll();
        if (darkModeProperty.get()){
            root.getStylesheets().add(darkTheme);
        }else{
            root.getStylesheets().add(lightTheme);
        }
    }

    public void toggleTheme(){
        darkModeProperty.set(!darkModeProperty.get());
        applyThemeSmooth();
    }

    private void applyThemeSmooth() {


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
                new KeyFrame(Duration.millis(250),
                        new KeyValue(blur.radiusProperty(), 5, Interpolator.EASE_BOTH),
                        new KeyValue(region.opacityProperty(), 0.25, Interpolator.EASE_BOTH)
                )

        );
        blurIn.setOnFinished(event -> {
            root.getStylesheets().remove("");

            Timeline blurOut = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(blur.radiusProperty(), 5, Interpolator.EASE_BOTH),
                            new KeyValue(region.opacityProperty(), 0.25, Interpolator.EASE_BOTH)
                    ),
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(blur.radiusProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(region.opacityProperty(), 0, Interpolator.EASE_BOTH)
                    )
            );
            blurOut.setOnFinished(e -> {
                if (darkModeProperty.get()){
                    root.getStylesheets().add(darkTheme);
                    root.getStylesheets().remove(lightTheme);
                }else{
                    root.getStylesheets().add(lightTheme);
                    root.getStylesheets().remove(darkTheme);
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
