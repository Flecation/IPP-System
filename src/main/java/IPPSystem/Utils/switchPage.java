package IPPSystem.Utils;

import IPPSystem.Main.HelloApplication;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class switchPage extends effect{

    //For dashBoard Change For Example From the home page to the edit page
    public static void setSwitchPane(Parent basePane,Parent fromPane, String toPane, Button titleUrlButton,Button clickedButton){

        //For inserting the spinner like it is loading
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(14,14);
        titleUrlButton.setGraphic(spinner);
        titleUrlButton.setText("loading...");

        //Setting the toPane
        Parent nextPane;
        try{
            nextPane = FXMLLoader.load(HelloApplication.class.getResource(toPane));
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

        //For inserting the region on the fromPane and will change to the toPane
        Region region = new Region();
        region.setPrefSize(fromPane.getBoundsInLocal().getWidth(), fromPane.getBoundsInLocal().getHeight());
        region.setManaged(false);
        region.setMouseTransparent(true);
        region.setOpacity(0);

        GaussianBlur blur = new GaussianBlur(1);
        fromPane.setEffect(blur);



        Timeline in = new Timeline(
            new KeyFrame(Duration.ZERO,
                    new KeyValue(blur.radiusProperty(),0, Interpolator.EASE_BOTH),
                    new KeyValue(region.opacityProperty(), 0, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.millis(250),
                    new KeyValue(blur.radiusProperty(),5,Interpolator.EASE_BOTH),
                    new KeyValue(region.opacityProperty(),0.5,Interpolator.EASE_BOTH)
            )
            );

        in.setOnFinished(event -> {
            //For re set the name of the title url button
            titleUrlButton.setEffect(null);
            setToolTip(titleUrlButton,clickedButton.getText());
            titleUrlButton.setText(clickedButton.getText());

            //Pane Switch
            StackPane container = (StackPane) basePane;
            container.getChildren().add(region);
            container.getChildren().add(nextPane);
            container.getChildren().remove(fromPane);


            Timeline out = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(blur.radiusProperty(),5,Interpolator.EASE_BOTH),
                        new KeyValue(region.opacityProperty(),0.5,Interpolator.EASE_BOTH)
                    ),
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(blur.radiusProperty(),0,Interpolator.EASE_BOTH),
                            new KeyValue(region.opacityProperty(),0,Interpolator.EASE_BOTH)
                    )
            );

            out.setOnFinished(e -> {
                    fromPane.setEffect(null);
                    container.getChildren().remove(region);
            });

            out.play();

        });

        in.play();

    }
}
