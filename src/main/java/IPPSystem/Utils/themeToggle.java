package IPPSystem.Utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;

public class themeToggle {
    private static themeToggle instance;

    private static String lightTheme,darkTheme;
    private static Parent root;

    private final SimpleBooleanProperty darkModeProperty = new SimpleBooleanProperty(false);

    public BooleanProperty darkModeProperty(){
        return darkModeProperty;
    }

    public boolean isDarkMode(){
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
        toggleTheme();
    }

    public static Parent getRoot() {
        return root;
    }

    public static void setRoot(Parent root) {
        themeToggle.root = root;

    }

    public void toggleTheme(){
        darkModeProperty.set(!darkModeProperty.get());
        if (isDarkMode()){
            root.getStylesheets().remove(lightTheme);
            root.getStylesheets().add(darkTheme);
        }else {
            root.getStylesheets().remove(darkTheme);
            root.getStylesheets().add(lightTheme);
        }
    }


}
