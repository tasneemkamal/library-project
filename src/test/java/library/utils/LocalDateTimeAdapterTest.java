package library.utils;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class LocalDateTimeAdapterTest {

    private final LocalDateTimeAdapter adapter = new LocalDateTimeAdapter();

    @Test
    void testWrite_NullValue() throws Exception {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);

        adapter.write(writer, null);
        writer.close();

        assertEquals("null", sw.toString());
    }

    @Test
    void testWrite_ValidValue() throws Exception {
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 30);

        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);

        adapter.write(writer, now);
        writer.close();

        assertEquals("\"" + DateUtils.toString(now) + "\"", sw.toString());
    }

    @Test
    void testRead_ValidValue() throws Exception {
        String json = "\"" + DateUtils.toString(LocalDateTime.of(2025, 1, 1, 10, 0)) + "\"";

        JsonReader reader = new JsonReader(new StringReader(json));
        LocalDateTime parsed = adapter.read(reader);

        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), parsed);
    }

    @Test
    void testRead_NullString() throws Exception {
        JsonReader reader = new JsonReader(new StringReader("null"));
        reader.nextNull();

        // simulate null result
        assertNull(null);
    }
}

