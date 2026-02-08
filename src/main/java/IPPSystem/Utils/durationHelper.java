package IPPSystem.Utils;

import IPPSystem.Constants.enumDuration;
import IPPSystem.Models.projects;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.HashMap;

public class durationHelper {

    private static final double WORKING_DAYS_PER_MONTH = 26;
    private static final double MONTHS_PER_YEAR = 12;

    public Double durationAssign(Double duration, enumDuration durationStatus) {
        if (duration == null) return 0.0;
        switch (durationStatus) {
            case DAY:
                // Round to nearest whole day (10.09 -> 10, 10.6 -> 11)
                return (double) Math.round(duration);
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

        if (duration == null) {
            result.put(enumDuration.DAY, 0.0);
            result.put(enumDuration.MONTH, 0.0);
            result.put(enumDuration.YEAR, 0.0);
            return result;
        }

        double month = Math.ceil(duration / WORKING_DAYS_PER_MONTH);
        double year  = Math.ceil(duration / (WORKING_DAYS_PER_MONTH * MONTHS_PER_YEAR));

        // Display days as rounded whole number
        result.put(enumDuration.DAY, (double) Math.round(duration));
        result.put(enumDuration.MONTH, month);
        result.put(enumDuration.YEAR, year);

        return result;
    }

    public String showMonthDuration(Double duration) {
        return Math.ceil(duration / WORKING_DAYS_PER_MONTH) + " Months";
    }

    public String showDayDuration(Double duration) {
        if (duration == null) return "0 Days";
        return Math.round(duration) + " Days";
    }

    public String showYearDuration(Double duration) {
        return Math.ceil(duration / (WORKING_DAYS_PER_MONTH * MONTHS_PER_YEAR)) + " Years";
    }

    public void durationAssignHelper(Double projectDuration,
                                     ComboBox<enumDuration> durationComboBox,
                                     TextField durationShowTxt) {

        HashMap<enumDuration, Double> getDuration =
                showDuration(projectDuration);

        durationComboBox.getItems().setAll(enumDuration.values());
        durationComboBox.getSelectionModel().select(enumDuration.DAY);

        durationShowTxt.setText(showDayDuration(projectDuration));

        durationComboBox.setOnAction(e -> {
            enumDuration selected =
                    durationComboBox.getSelectionModel().getSelectedItem();

            Double d = getDuration.get(selected);
            long rounded = (d == null) ? 0L : Math.round(d);
            String suffix = selected == null ? "" : (" " + selected.name().toLowerCase() + (rounded == 1 ? "" : "s"));
            // enum names are DAY/MONTH/YEAR; make them human readable
            suffix = suffix.replace(" day", " Days").replace(" month", " Months").replace(" year", " Years");
            durationShowTxt.setText(rounded + suffix);
        });
    }

}
