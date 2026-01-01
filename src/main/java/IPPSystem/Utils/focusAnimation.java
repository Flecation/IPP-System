package IPPSystem.Utils;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class focusAnimation {

    public static void animateUnderline(Region underline, Color from, Color to) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(underline.backgroundProperty(),
                                new Background(new BackgroundFill(
                                        from, CornerRadii.EMPTY, Insets.EMPTY)))
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(underline.backgroundProperty(),
                                new Background(new BackgroundFill(
                                        to, CornerRadii.EMPTY, Insets.EMPTY)))
                )
        );

        timeline.play();
    }



}
