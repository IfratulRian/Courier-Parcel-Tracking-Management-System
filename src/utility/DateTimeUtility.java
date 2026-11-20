package utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtility {
    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter STORAGE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private DateTimeUtility() {
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }

    public static LocalDateTime parse(String text) {
        return LocalDateTime.parse(text, STORAGE_FORMAT);
    }
}
