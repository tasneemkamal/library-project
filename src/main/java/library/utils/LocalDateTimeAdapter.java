package library.utils;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * GSON TypeAdapter for LocalDateTime serialization/deserialization
 * @author Library Team
 * @version 1.0
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(DateUtils.toString(value));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        String dateString = in.nextString();
        return dateString != null ? DateUtils.fromString(dateString) : null;
    }
}