package IPPSystem.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

//        Font Family
        Font.loadFont(
                getClass().getResource("/fonts/Poppins-Regular.ttf").toExternalForm(),12
        );
        Font.loadFont(
                getClass().getResource("/fonts/Poppins-SemiBold.ttf").toExternalForm(),12
        );

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/View/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
//        stage.setTitle("Hello!");
        stage.setScene(scene);



        stage.show();
    }
}
