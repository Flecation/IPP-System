package IPPSystem.Utils;

import IPPSystem.Constants.enumDuration;
import IPPSystem.Models.projects;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;

public class durationHelper {

    private static final double WORKING_DAYS_PER_MONTH = 26;
    private static final double MONTHS_PER_YEAR = 12;

    public Double durationAssign(Double duration, enumDuration durationStatus) {
        switch (durationStatus) {
            case DAY:
                return Math.ceil(duration);
            case MONTH:
                return Math.ceil(duration * WORKING_DAYS_PER_MONTH);
            case YEAR:
                return Math.ceil(duration * WORKING_DAYS_PER_MONTH * MONTHS_PER_YEAR);
            default:
                return 0.0;
        }
    }

    public HashMap<enumDuration, Double> showDuration(Double duration) {
        HashMap<enumDuration, Double> result = new HashMap<>();

        double month = Math.ceil(duration / WORKING_DAYS_PER_MONTH);
        double year  = Math.ceil(duration / (WORKING_DAYS_PER_MONTH * MONTHS_PER_YEAR));

        result.put(enumDuration.DAY, Math.ceil(duration));
        result.put(enumDuration.MONTH, month);
        result.put(enumDuration.YEAR, year);

        return result;
    }

    public String showMonthDuration(Double duration) {
        return Math.ceil(duration / WORKING_DAYS_PER_MONTH) + " Months";
    }

    public String showDayDuration(Double duration) {
        return Math.ceil(duration) + " Days";
    }

    public String showYearDuration(Double duration) {
        return Math.ceil(duration / (WORKING_DAYS_PER_MONTH * MONTHS_PER_YEAR)) + " Years";
    }

    public void durationAssignHelper(projects project,
                                     ComboBox<enumDuration> durationComboBox,
                                     TextField durationShowTxt) {

        HashMap<enumDuration, Double> getDuration =
                showDuration(project.getProjectDuration());

        durationComboBox.getItems().setAll(enumDuration.values());
        durationComboBox.getSelectionModel().select(enumDuration.DAY);

        durationShowTxt.setText(showDayDuration(project.getProjectDuration()));

        durationComboBox.setOnAction(e -> {
            enumDuration selected =
                    durationComboBox.getSelectionModel().getSelectedItem();

            Double d = getDuration.get(selected);
            durationShowTxt.setText(String.valueOf(d));
        });
    }

}
