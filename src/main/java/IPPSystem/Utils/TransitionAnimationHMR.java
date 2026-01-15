package IPPSystem.Utils;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class TransitionAnimationHMR {
    public static void addHoverFade(Node node, double from, double to, int millis){
        FadeTransition fadeInt = new FadeTransition(Duration.millis(millis));
        fadeInt.setFromValue(from);
        fadeInt.setToValue(to);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(millis));
        fadeOut.setFromValue(to);
        fadeOut.setToValue(from);

        node.setOnMouseEntered(e-> fadeInt.playFromStart());
        node.setOnMouseExited(e->fadeOut.playFromStart());
    }
}
