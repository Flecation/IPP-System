package IPPSystem.Utils;

import IPPSystem.Constants.enumDuration;
import IPPSystem.Models.projects;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;

public class durationHelper {

    public Double durationAssign(Double duration, enumDuration durationStatus){
        if (durationStatus.equals(enumDuration.DAY)){
              return duration/26; // working Days Per month is 26
        }else if(durationStatus.equals(enumDuration.MONTH)){
            return duration;
        }else {
            return duration*12;
        }
    }

    public HashMap<enumDuration,Double> showDuration(Double duration){
        HashMap<enumDuration,Double> result = new HashMap<>();
        Double year = duration/12;
        Double day = duration*26;

        result.put(enumDuration.DAY,day);
        result.put(enumDuration.MONTH,duration);
        result.put(enumDuration.YEAR,year);

        return result;
    }

    public void durationAssignHelper(projects project, ComboBox<String> durationComboBox, TextField durationShowTxt){
        HashMap<enumDuration,Double> getDuration = showDuration(project.getProjectDuration());
        for (enumDuration s : getDuration.keySet()){
            durationComboBox.getItems().add(s.toString());
        }

        durationComboBox.getSelectionModel().selectFirst();
        durationShowTxt.setText(String.valueOf(project.getProjectDuration()));
        durationComboBox.setOnAction(e->{
            // to get duration of Calculate Function
            Double d = 0.0;
            if (durationComboBox.getSelectionModel().getSelectedItem().equals(enumDuration.DAY.toString())){
                d = getDuration.get(enumDuration.DAY);
            }
            if (durationComboBox.getSelectionModel().getSelectedItem().equals(enumDuration.MONTH.toString())){
                d = getDuration.get(enumDuration.MONTH);
            }
            if(durationComboBox.getSelectionModel().getSelectedItem().equals(enumDuration.YEAR.toString())){
                d = getDuration.get(enumDuration.YEAR);
            }

            durationShowTxt.setText(String.valueOf(d));
        });
    }
}
