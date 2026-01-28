package IPPSystem.Utils;

import IPPSystem.Constants.notificationType;
import IPPSystem.Models.projects;
import IPPSystem.Models.users;
import IPPSystem.Models.workItems;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

//This is collected place for all utils
public class utils {

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

        public static void setAlertBox(Parent root, String title, String message, notificationType type, boolean onlyShow) throws IOException {
            try {
                FXMLLoader loader = new FXMLLoader(utils.class.getResource("/View/messageBox.fxml"));
                Parent msgRoot = loader.load();
                IPPSystem.Controllers.messageBoxController controller = loader.getController();

                if (root instanceof Pane pane) {
                    if (!pane.getChildren().contains(msgRoot)) {
                        pane.getChildren().add(msgRoot);

                    }
                } else if (root.getScene() != null && root.getScene().getRoot() instanceof Pane pane) {
                    if (!pane.getChildren().contains(msgRoot)) {
                        pane.getChildren().add(msgRoot);
                    }
                }
                msgRoot.setTranslateX(300);
                msgRoot.toFront();
                if (onlyShow) {
                    controller.toastMessage(msgRoot, title, message, type);

                } else {
                    controller.confirmMessage(msgRoot, title, message, type);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        public static void showProjectCards(ArrayList<projects> projects, VBox containerPane){
            switchPage.getInstance(null).loadProjects(projects, containerPane);
        }

        public static void viewUserInfo(users user){
            switchPage.getInstance(null).viewUsersInfo(user);
        }

}

