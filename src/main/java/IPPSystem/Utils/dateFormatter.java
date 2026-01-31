package IPPSystem.Utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
 import java.text.SimpleDateFormat;

public class dateFormatter {
    // === Common formatters ===
    public static final DateTimeFormatter UI_DATE =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static final DateTimeFormatter UI_DATE_TIME =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


    // === Convenience methods ===
    public static String formatForUI(LocalDate date) {
        return date != null ? date.format(UI_DATE) : "";
    }

    public static String formatForUI(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(UI_DATE_TIME) : "";
    }

    public static String formatDate(java.util.Date date){
        if (date == null) return "-";
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }


    public static Date DOB(String DOB){
        return Date.valueOf(DOB);
    }

    public static Date today() {
        return Date.valueOf(LocalDate.now());
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDate getLocalDate(Date date){
        if (date == null) return null ;
        return date.toLocalDate();
    }
}
