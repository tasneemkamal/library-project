package library.repositories;

import com.google.gson.Gson;
import library.models.User;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;
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

        when(fileHandler.readFromFile("test.json")).thenReturn(json);

        UserRepository repo = new UserRepository("test.json", fileHandler, gson);

        assertEquals(1, repo.findAll().size());
        assertNotNull(repo.findById("U1"));
    }

    @Test
    void testLoadUsers_EmptyJson() {
        when(fileHandler.readFromFile("empty.json")).thenReturn("");

        UserRepository repo = new UserRepository("empty.json", fileHandler, gson);

        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testLoadUsers_InvalidJson() {
        when(fileHandler.readFromFile("invalid.json")).thenReturn("{ invalid ");

        UserRepository repo = new UserRepository("invalid.json", fileHandler, gson);

        assertEquals(0, repo.findAll().size());
    }

    

    @Test
    void testSave_NewUser_Success() {
        when(fileHandler.readFromFile("save.json")).thenReturn("");
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        UserRepository repo = new UserRepository("save.json", fileHandler, gson);

        User u = new User();
        u.setName("Kamal");
        u.setEmail("k@k.com");

        assertTrue(repo.save(u));
        assertNotNull(u.getId());
        assertTrue(u.getId().startsWith("USER_"));

        assertEquals(1, repo.findAll().size());
    }

    @Test
    void testSave_ExistingUser_Update() {
        when(fileHandler.readFromFile("update.json")).thenReturn("{}");
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        UserRepository repo = new UserRepository("update.json", fileHandler, gson);

        User u = new User();
        u.setId("U100");
        u.setName("Old");
        u.setEmail("old@test.com");

        repo.save(u);

        u.setName("Updated");
        assertTrue(repo.save(u));

        assertEquals("Updated", repo.findById("U100").getName());
    }

    @Test
    void testSave_Failure() {
        when(fileHandler.readFromFile("bad.json")).thenReturn("");
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(false);

        UserRepository repo = new UserRepository("bad.json", fileHandler, gson);

        User u = new User();
        u.setName("X");
        u.setEmail("x@x.com");

        assertFalse(repo.save(u));
    }

    

    @Test
    void testGenerateId() {
        when(fileHandler.readFromFile(anyString())).thenReturn("");

        UserRepository repo = new UserRepository("id.json", fileHandler, gson);

        User user = new User();
        user.setName("A");
        user.setEmail("a@a.com");

        repo.save(user);

        assertNotNull(user.getId());
        assertTrue(user.getId().startsWith("USER_"));
    }

    

    @Test
    void testFindById_Found() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"A\", \"email\":\"a@a.com\" } }";

        when(fileHandler.readFromFile("f.json")).thenReturn(json);

        UserRepository repo = new UserRepository("f.json", fileHandler, gson);

        assertNotNull(repo.findById("U1"));
    }

    @Test
    void testFindById_NotFound() {
        when(fileHandler.readFromFile("none.json")).thenReturn("{}");

        UserRepository repo = new UserRepository("none.json", fileHandler, gson);

        assertNull(repo.findById("X"));
    }

    @Test
    void testFindByEmail_Found() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"A\", \"email\":\"abc@xyz.com\" } }";

        when(fileHandler.readFromFile("email.json")).thenReturn(json);

        UserRepository repo = new UserRepository("email.json", fileHandler, gson);

        assertNotNull(repo.findByEmail("abc@xyz.com"));
    }

    @Test
    void testFindByEmail_NotFound() {
        when(fileHandler.readFromFile("email2.json")).thenReturn("{}");

        UserRepository repo = new UserRepository("email2.json", fileHandler, gson);

        assertNull(repo.findByEmail("none@x.com"));
    }

    

    @Test
    void testDelete_Success() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"T\", \"email\":\"e@e.com\" } }";

        when(fileHandler.readFromFile("del.json")).thenReturn(json);
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        UserRepository repo = new UserRepository("del.json", fileHandler, gson);

        assertTrue(repo.delete("U1"));
        assertEquals(0, repo.findAll().size());
    }

    @Test
    void testDelete_Fail() {
        when(fileHandler.readFromFile("del2.json")).thenReturn("{}");

        UserRepository repo = new UserRepository("del2.json", fileHandler, gson);

        assertFalse(repo.delete("UNKNOWN"));
    }

    

    @Test
    void testClearAll() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"T\", \"email\":\"t@t.com\" } }";

        when(fileHandler.readFromFile("clr.json")).thenReturn(json);
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        UserRepository repo = new UserRepository("clr.json", fileHandler, gson);

        assertEquals(1, repo.findAll().size());

        repo.clearAll();

        assertEquals(0, repo.findAll().size());
    }
}

