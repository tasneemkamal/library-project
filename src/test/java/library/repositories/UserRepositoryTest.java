package library.repositories;

import library.models.User;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class UserRepositoryTest {

    private JsonFileHandler fileHandler;
    private Gson gson;

    @BeforeEach
    void setup() {
        fileHandler = mock(JsonFileHandler.class);
        gson = GsonUtils.createGson();
    }

    @Test
    void testLoadUsers_ValidJson() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"Test\", \"email\":\"t@t.com\" } }";

        when(fileHandler.readFromFile("test_users.json")).thenReturn(json);

        UserRepository repo = new UserRepository("test_users.json", fileHandler, gson);

        assertEquals(1, repo.findAll().size());
        assertNotNull(repo.findById("U1"));
    }

    @Test
    void testLoadUsers_EmptyJson() {
        when(fileHandler.readFromFile("test.json")).thenReturn("");

        UserRepository repo = new UserRepository("test.json", fileHandler, gson);

        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testLoadUsers_InvalidJson() {
        when(fileHandler.readFromFile("bad.json")).thenReturn("{ invalid json ");

        UserRepository repo = new UserRepository("bad.json", fileHandler, gson);

        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testSaveUser_Success() {
        when(fileHandler.readFromFile(anyString())).thenReturn("");
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        UserRepository repo = new UserRepository("save_test.json", fileHandler, gson);

        User user = new User();
        user.setName("Kamal");
        user.setEmail("k@k.com");

        assertTrue(repo.save(user));
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void testSaveUser_Failure() {
        when(fileHandler.readFromFile(anyString())).thenReturn("");
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(false);

        UserRepository repo = new UserRepository("fail.json", fileHandler, gson);

        User user = new User();
        user.setName("X");
        user.setEmail("x@x.com");

        assertFalse(repo.save(user));
    }

    @Test
    void testFindByEmail_Found() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"Test\", \"email\":\"abc@xyz.com\" } }";

        when(fileHandler.readFromFile("test.json")).thenReturn(json);

        UserRepository repo = new UserRepository("test.json", fileHandler, gson);

        User u = repo.findByEmail("abc@xyz.com");

        assertNotNull(u);
        assertEquals("U1", u.getId());
    }

    @Test
    void testFindByEmail_NotFound() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"Test\", \"email\":\"abc@xyz.com\" } }";

        when(fileHandler.readFromFile("test.json")).thenReturn(json);

        UserRepository repo = new UserRepository("test.json", fileHandler, gson);

        assertNull(repo.findByEmail("notfound@x.com"));
    }

    @Test
    void testDeleteUser_Success() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"Test\", \"email\":\"abc@xyz.com\" } }";

        when(fileHandler.readFromFile("test.json")).thenReturn(json);
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        UserRepository repo = new UserRepository("test.json", fileHandler, gson);

        assertTrue(repo.delete("U1"));
        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testDeleteUser_NotExisting() {
        when(fileHandler.readFromFile("del.json")).thenReturn("{ }");

        UserRepository repo = new UserRepository("del.json", fileHandler, gson);

        assertFalse(repo.delete("XXXX"));
    }

    @Test
    void testClearAll() {
        String json = "{ \"A1\": { \"id\":\"A1\", \"name\":\"N\", \"email\":\"e@e.com\" } }";

        when(fileHandler.readFromFile("clr.json")).thenReturn(json);
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        UserRepository repo = new UserRepository("clr.json", fileHandler, gson);
        assertEquals(1, repo.findAll().size());

        repo.clearAll();

        assertEquals(0, repo.findAll().size());
    }
}
