package library.utils;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

/**
 * Utility class for GSON configuration
 * @author Library Team
 * @version 1.0
 */
public class GsonUtils {
    
    /**
     * Create a Gson instance with LocalDateTime support
     * @return configured Gson instance
     */
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * Create a Gson instance for compact JSON (without pretty printing)
     * @return configured Gson instance
     */
    public static Gson createCompactGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}