package library.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

/**
 * Utility class for creating configured Gson instances.
 * Provides support for LocalDateTime through a custom adapter.
 */
public final class GsonUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private GsonUtils() {
        throw new UnsupportedOperationException("Utility class - cannot instantiate");
    }

    /**
     * Creates a Gson instance with pretty printing enabled.
     * Includes LocalDateTime adapter.
     *
     * @return configured Gson instance
     */
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Creates a compact Gson instance (no pretty printing).
     * Includes LocalDateTime adapter.
     *
     * @return configured Gson instance
     */
    public static Gson createCompactGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
