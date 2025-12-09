package library.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GsonUtilsTest {

    @Test
    void testCreateGson_NotNull() {
        Gson gson = GsonUtils.createGson();
        assertNotNull(gson, "Gson instance should not be null");
    }

    @Test
    void testCreateGson_HasLocalDateTimeAdapter() {
        Gson gson = GsonUtils.createGson();
        TypeAdapter<LocalDateTime> adapter = gson.getAdapter(LocalDateTime.class);
        assertNotNull(adapter, "LocalDateTime adapter should not be null");
    }

    @Test
    void testCreateCompactGson_NotNull() {
        Gson gson = GsonUtils.createCompactGson();
        assertNotNull(gson, "Compact Gson instance should not be null");
    }

    @Test
    void testCreateCompactGson_HasLocalDateTimeAdapter() {
        Gson gson = GsonUtils.createCompactGson();
        TypeAdapter<LocalDateTime> adapter = gson.getAdapter(LocalDateTime.class);
        assertNotNull(adapter, "LocalDateTime adapter should not be null");
    }

    @Test
    void testPrivateConstructor_NotInstantiable() throws Exception {
        Constructor<GsonUtils> constructor = GsonUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                "Constructor should be private");

        constructor.setAccessible(true);

        Exception exception = assertThrows(Exception.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof UnsupportedOperationException,
                "Constructor should throw UnsupportedOperationException");
    }

    @Test
    void testGsonSerializationDeserialization() {
        Gson gson = GsonUtils.createGson();
        LocalDateTime now = LocalDateTime.now();

        String json = gson.toJson(now);
        assertNotNull(json, "JSON serialization should not return null");

        LocalDateTime parsed = gson.fromJson(json, LocalDateTime.class);
        assertNotNull(parsed, "JSON deserialization should not return null");
    }
}
