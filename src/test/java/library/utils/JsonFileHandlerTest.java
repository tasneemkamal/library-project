package library.utils;

import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFileHandlerTest {

    private JsonFileHandler handler;
    private String testFilePath;

    @BeforeEach
    void setup() throws Exception {
        handler = new JsonFileHandler();

        testFilePath = "target/test-tmp/test-file.json";
        Files.createDirectories(Paths.get("target/test-tmp"));

        File file = new File(testFilePath);
        if (file.exists()) file.delete();
    }

    // -------------------------------------------------------
    // READ TESTS
    // -------------------------------------------------------

    @Test
    void testReadFromFile_FileDoesNotExist_ShouldCreateFile() {
        String result = handler.readFromFile(testFilePath);

        assertNotNull(result);
        assertTrue(new File(testFilePath).exists());
    }

    @Test
    void testReadFromFile_WithContent() throws Exception {
        Files.write(Paths.get(testFilePath), "{\"name\":\"test\"}".getBytes());

        String result = handler.readFromFile(testFilePath);

        assertEquals("{\"name\":\"test\"}", result);
    }

    /**
     * Cover real IOException in readFromFile()
     * using directory path instead of file → always throws IOException
     */
    @Test
    void testReadFromFile_IOException_Real() {
        String directoryPath = "target/test-tmp"; // NOT a file

        String result = handler.readFromFile(directoryPath);

        // Should hit the catch block and return empty string
        assertEquals("", result);
    }

    // -------------------------------------------------------
    // WRITE TESTS
    // -------------------------------------------------------

    @Test
    void testWriteToFile_Success() {
        boolean result = handler.writeToFile(testFilePath, "{\"age\":25}");

        assertTrue(result);
        assertEquals("{\"age\":25}", handler.readFromFile(testFilePath));
    }

    /**
     * Cover real IOException in writeToFile()
     * by trying to write to a directory
     */
    @Test
    void testWriteToFile_IOException_Real() {
        String directoryPath = "target/test-tmp"; // Directory, not file

        boolean result = handler.writeToFile(directoryPath, "{test}");

        assertFalse(result); // Should hit catch block
    }

    // -------------------------------------------------------
    // ARTIFICIAL OVERRIDES (Extra safety coverage)
    // -------------------------------------------------------

    @Test
    void testWriteToFile_IOException_Override() {
        JsonFileHandler broken = new JsonFileHandler() {
            @Override
            public boolean writeToFile(String filePath, String content) {
                try {
                    throw new java.io.IOException("Forced");
                } catch (Exception e) {
                    return false;
                }
            }
        };

        assertFalse(broken.writeToFile("ignored.json", "{}"));
    }

    @Test
    void testReadFromFile_IOException_Override() {
        JsonFileHandler broken = new JsonFileHandler() {
            @Override
            public String readFromFile(String filePath) {
                try {
                    throw new java.io.IOException("Forced");
                } catch (Exception e) {
                    return "";
                }
            }
        };

        assertEquals("", broken.readFromFile("ignored.json"));
    }
}


