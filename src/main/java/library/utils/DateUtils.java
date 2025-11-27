package library.utils;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 * @author Library Team
 * @version 1.0
 */
public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Convert LocalDateTime to string
     * @param dateTime the LocalDateTime to convert
     * @return string representation
     */
    public static String toString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }

    /**
     * Convert string to LocalDateTime
     * @param dateTimeString the string to convert
     * @return LocalDateTime object
     */
    public static LocalDateTime fromString(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, FORMATTER) : null;
    }

    /**
     * Get current date and time
     * @return current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}