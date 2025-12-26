package IPPSystem.Utils;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class textFieldStyle {

        private static Label textLabel,passwordLabel;

        private static TextField textField, showPw;
        private static PasswordField hidePw;

        public static void floatTextFieldStyle(Label txtLabel, TextField txtField){
            textLabel = txtLabel;
            textField = txtField;

            textField.setOnMouseEntered(e->{floatUp("text");});
            textField.setOnMouseExited(e->{floatDown("text");});

            textLabel.setOnMouseEntered(e -> {floatUp("text");});
            textLabel.setOnMouseExited(e->{floatDown("text");});

            textField.focusedProperty().addListener((obs,oldV,newV)->{
                if (newV){
                    floatUp("text");
                }else{
                    floatDown("text");
                }
            });

            textField.textProperty().addListener((obs,oldText,newText)->{
                if(newText.isEmpty()){
                    floatUp("text");
                }
            });
        }


        public static void floatPasswordStyle(Label pwLabel, TextField showPasswordField, PasswordField hidePasswordField){
                passwordLabel = pwLabel;
                showPw = showPasswordField;
                hidePw = hidePasswordField;

                showPw.setOnMouseEntered(e->{floatUp("pw");});
                hidePw.setOnMouseEntered(e->{floatUp("pw");});
                passwordLabel.setOnMouseEntered(e->{floatUp("pw");});

                showPw.setOnMouseExited(e->{floatDown("pw");});
                hidePw.setOnMouseExited(e->{floatDown("pw");});
                passwordLabel.setOnMouseExited(e->{floatDown("pw");});

                showPw.focusedProperty().addListener((obs,oldV,newV)->{
                    if (newV){
                        floatUp("pw");
                    }else{
                        floatDown("pw");
                    }
                });

                showPw.textProperty().addListener((obs,oldText,newText)->{
                    if(newText.isEmpty()){
                        floatUp("text");
                    }
                });

                hidePw.focusedProperty().addListener((obs,oldV,newV)->{
                    if (newV){
                        floatUp("pw");
                    }else{
                        floatDown("pw");
                    }
                });

                hidePw.textProperty().addListener((obs,oldText,newText)->{
                    if(newText.isEmpty()){
                        floatUp("text");
                    }
                });

        }

        private static void floatUp(String choice){
            Label floatLbl ;
            if(choice.equals("text")){
                floatLbl = textLabel;
            }else{
                floatLbl = passwordLabel;
            }

            TranslateTransition moveUp = new TranslateTransition(Duration.millis(200), floatLbl);
            moveUp.setToY(-28);
            moveUp.setToX(-10);
            moveUp.setInterpolator(Interpolator.EASE_BOTH);

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), floatLbl);
            scale.setInterpolator(Interpolator.EASE_BOTH);
            scale.setToX(0.85);
            scale.setToY(0.85);

            ParallelTransition transition = new ParallelTransition(moveUp, scale);
            transition.play();
        }

        private static void floatDown(String choice){
            Label floatLbl;
            if(choice.equals("text")) {
                if (!textField.getText().isEmpty()) return;
                if(textField.isFocused()) return;
                floatLbl = textLabel;
            }else{
                if(!showPw.getText().isEmpty()) return;
                if (showPw.isFocused()) return;
                if (!hidePw.getText().isEmpty()) return;
                if (hidePw.isFocused()) return;
                floatLbl = passwordLabel;
            }

            TranslateTransition moveDown = new TranslateTransition(Duration.millis(200), floatLbl);
            moveDown.setToY(0);
            moveDown.setToX(0);


            ScaleTransition scale = new ScaleTransition(Duration.millis(200), floatLbl);
            scale.setToX(1);
            scale.setToY(1);

            ParallelTransition transition = new ParallelTransition(moveDown, scale);
            transition.play();
        }


}
