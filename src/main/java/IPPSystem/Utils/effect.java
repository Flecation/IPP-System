package IPPSystem.Utils;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;

//This is collected place for all effect
public class effect {

        //setting the tile bar of the exit,mini,restore buttons called from the tileBar class
        public static void setTitleBar(Parent basePane, Button minimizeBtn, Button restoreBtn, Button exitBtn) {
            titleBar.setTitleBar(basePane, minimizeBtn, restoreBtn, exitBtn);
        }

        //For setting the tool tip for any buttons
        public static void setToolTip(Button btn,String suggestion){
            Tooltip tooltip = new Tooltip(suggestion);
            tooltip.setShowDelay(Duration.millis(300));
            Tooltip.install(btn,tooltip);
        }

        //For setting the password field if in the needed place
        public static void setPasswordField(TextField showPasswordField, PasswordField hidePasswordField, CheckBox showPasswordCheckBox){

            showPasswordField.setVisible(false);
            hidePasswordField.setVisible(true);
            showPasswordCheckBox.setSelected(false);

            showPasswordCheckBox.setOnAction(event -> {
                if(showPasswordCheckBox.isSelected()){

                    hidePasswordField.setText(showPasswordField.getText());
                    showPasswordField.setVisible(true);
                    hidePasswordField.setVisible(false);

                }else{

                    showPasswordField.setText(hidePasswordField.getText());
                    hidePasswordField.setVisible(false);
                    showPasswordField.setVisible(true);

                }
            });


        }



}
