package IPPSystem.Utils;

import IPPSystem.Constants.enumDuration;
import IPPSystem.Constants.notificationType;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Models.workItems;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

//This is collected place for all utils
public class utils {

    private static Parent currentAlertRoot = null;

    private static durationHelper helper = new durationHelper();

    //setting the tile bar of the exit,mini,restore buttons called from the tileBar class
    public static void setTitleBar(Parent basePane, Button minimizeBtn, Button restoreBtn, Button exitBtn) {
        titleBar.setTitleBar(basePane, minimizeBtn, restoreBtn, exitBtn);
    }

    //For setting the tool tip for any buttons
    public static void setToolTip(Button btn,String suggestion){
        Tooltip tooltip = new Tooltip(suggestion);
//            tooltip.setMaxSize(33,17);
        tooltip.setStyle("-fx-font-size:12px;");
        tooltip.setShowDelay(Duration.millis(300));
        Tooltip.install(btn,tooltip);
    }
    public static void setToolTip(Label lbl,String suggestion){
        Tooltip tooltip = new Tooltip(suggestion);
//            tooltip.setMaxSize(33,17);
        tooltip.setStyle("-fx-font-size:12px;");
        tooltip.setShowDelay(Duration.millis(300));
        Tooltip.install(lbl,tooltip);

    }

    //For setting the password field if in the needed place
    public static void setPasswordField(TextField showPasswordField, PasswordField hidePasswordField, CheckBox showPasswordCheckBox){

        showPasswordField.setVisible(false);
        hidePasswordField.setVisible(true);
        showPasswordCheckBox.setSelected(false);

        showPasswordCheckBox.setOnAction(event -> {
            if(showPasswordCheckBox.isSelected()){
               showPasswordField.setText(hidePasswordField.getText());
               hidePasswordField.setVisible(false);
               showPasswordField.setVisible(true);

            }else{
                hidePasswordField.setText(showPasswordField.getText());
                hidePasswordField.setVisible(true);
                showPasswordField.setVisible(false);
            }
        });


    }

    public static void setFloatTextFieldStyle(Label textLabel , TextField textField){
        new textFieldStyle().floatTextFieldStyle(textLabel,textField);
    }

    public static void setFloatPasswordFieldStyle(Label pwLabel, TextField showPwTxt , PasswordField hidePwTxt){
        new textFieldStyle().floatPasswordStyle(pwLabel,showPwTxt,hidePwTxt);
    }

    public static void setFocusAnimation(Region underline,String from, String to){
        Color From = Color.web(from);
        Color To = Color.web(to);
        focusAnimation.animateUnderline(underline,From,To);
    }

    public static void setAlertBox(Parent root, String title, String message, notificationType type, boolean onlyShow) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setAlertBox(root, title, message, type, onlyShow));
            return;
        }

        try {
            Pane targetPane = findOverlayTargetPane(root);
            if (targetPane == null) {
                return;
            }

            // If an old alert is still attached, remove it so the new one always shows
            if (currentAlertRoot != null && currentAlertRoot.getParent() instanceof Pane oldPane) {
                oldPane.getChildren().remove(currentAlertRoot);
            }
            currentAlertRoot = null;
            targetPane.getChildren().removeIf(node -> "messageBoxRoot".equals(node.getId()));

            FXMLLoader loader = new FXMLLoader(utils.class.getResource("/View/messageBox.fxml"));
            Parent msgRoot = loader.load();
            currentAlertRoot = msgRoot;
            IPPSystem.Controllers.messageBoxController controller = loader.getController();

            // Make it behave like an overlay (not normal VBox layout)
            msgRoot.setId("messageBoxRoot");
            msgRoot.setManaged(true);
            msgRoot.setLayoutX(0);
            msgRoot.setLayoutY(0);
            if (msgRoot instanceof Region region) {
                region.prefWidthProperty().bind(targetPane.widthProperty());
                region.prefHeightProperty().bind(targetPane.heightProperty());
                region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }

            targetPane.getChildren().add(msgRoot);
            msgRoot.toFront();

            Runnable cleanup = () -> currentAlertRoot = null;
            if (onlyShow) {
                controller.toastMessage(msgRoot, title, message, type, cleanup);
            } else {
                controller.confirmMessage(msgRoot, title, message, type, cleanup);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Pane findOverlayTargetPane(Parent node) {
        if (node == null) {
            return null;
        }

        // Prefer the pane passed by the caller (e.g., login.fxml's overlayPane)
        if (node instanceof Pane pane) {
            return pane;
        }

        Node current = node;
        while (current != null) {
            if (current instanceof Pane pane) {
                return pane;
            }
            current = current.getParent();
        }

        if (node.getScene() != null && node.getScene().getRoot() instanceof Pane sceneRootPane) {
            return sceneRootPane;
        }
        return null;
    }

    public static void setTheme(Parent root){
        themeToggle.getInstance().setTheme(root,"/CSS/lightMode.css","/CSS/darkMode.css");
    }

    public static void changeTheme(){
        themeToggle.getInstance().toggleTheme();
    }

    public static String hashPassword(String password){
        return passwordCrafting.hashPassword(password);
    }

    public static boolean checkPassword(String inputPassword, String realPassword){
        return passwordCrafting.checkPassword(inputPassword,realPassword);
    }

    public static void switchNewScene(Button clickButton, String fxmlName){
        switchPage.getInstance(null).switchScene(clickButton,fxmlName);
    }

    public static void openFxml(String fxml, StackPane loadPane){
        switchPage.getInstance(loadPane).openFxml(fxml);
    }

    public static void showProjectCards(ObservableList<projects> projects, VBox containerPane){
        switchPage.getInstance(null).loadProjects(projects, containerPane);
    }

    public static void openWorkItemDetails(workItems items, StackPane a){
        switchPage.getInstance(a).openWorkItemDetails(items);
    }

    public static void viewUserInfo(users user){
        switchPage.getInstance(null).viewUsersInfo(user);
    }

    public static Double durationFormat(Double duration, enumDuration durationStatus){return helper.durationAssign(duration,durationStatus);}

    public static HashMap<enumDuration,Double> getDuration(Double duration){return helper.showDuration(duration);}

    public static String generateProjectId(int projectId){
        String result = "pj-";
        if (projectId >99){
            result += projectId;
        }else if(projectId >9){
            result += "0"+projectId;
        }else {
            result += "00"+projectId;
        }
        return result;
    }

    public static void durationShowHelper(projects project,ComboBox<String> durationBox,TextField durationTxt){
        helper.durationAssignHelper(project,durationBox,durationTxt);
    }

    public static String dateFormat(Date date){return dateFormatter.formatDate(date);}

    public static FontIcon iconSet(FontAwesomeSolid glyph){
        FontIcon icon = new FontIcon(glyph);
        icon.setIconSize(18);
        icon.getStyleClass().add("icon-Style");
        return icon;
    }
}
