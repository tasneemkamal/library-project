package library.repositories;

import com.google.gson.Gson;
import library.models.User;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private JsonFileHandler fileHandler;
    private Gson gson;
    private UserRepository repo;

    @BeforeEach
    void setup() {
        fileHandler = mock(JsonFileHandler.class);
        gson = GsonUtils.createGson();

        // Mock: أول تحميل يرجع null ↦ users = {}
        when(fileHandler.readFromFile(anyString())).thenReturn(null);

        repo = new UserRepository("data/users.json", fileHandler, gson);
    }

    // -------------------------------
    // loadUsers() tests
    // -------------------------------

    @Test
    void testLoadUsers_NullJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn(null);

        UserRepository r = new UserRepository("data/users.json", fileHandler, gson);

        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoadUsers_EmptyJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn("  ");

        UserRepository r = new UserRepository("data/users.json", fileHandler, gson);

        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoadUsers_EmptyObject() {
        when(fileHandler.readFromFile(anyString())).thenReturn("{}");

        UserRepository r = new UserRepository("data/users.json", fileHandler, gson);

        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoadUsers_InvalidJson() {
        when(fileHandler.readFromFile(anyString())).thenReturn("{ invalid");

        UserRepository r = new UserRepository("data/users.json", fileHandler, gson);

        assertEquals(0, r.findAll().size());
    }

    @Test
    void testLoadUsers_ValidJson() {
        String json = "{ \"U1\": { \"id\":\"U1\", \"name\":\"A\", \"email\":\"a@test.com\" } }";
        when(fileHandler.readFromFile(anyString())).thenReturn(json);

        UserRepository r = new UserRepository("data/users.json", fileHandler, gson);

        assertEquals(1, r.findAll().size());
        assertEquals("U1", r.findById("U1").getId());
        assertEquals("a@test.com", r.findById("U1").getEmail());
    }

    // -------------------------------
    // save() tests
    // -------------------------------

    @Test
    void testSave_NewUser_GeneratesId() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        User u = new User();
        u.setName("Test");
        u.setEmail("t@test.com");

        assertTrue(repo.save(u));
        assertNotNull(u.getId());
    }

    @Test
    void testSave_UpdateExistingUser() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        User u = new User();
        u.setName("Test");
        repo.save(u);

        LocalDateTime originalCreated = u.getCreatedAtDateTime();

        u.setName("Updated");
        repo.save(u);

        assertEquals("Updated", repo.findById(u.getId()).getName());
        assertEquals(originalCreated, repo.findById(u.getId()).getCreatedAtDateTime());
        assertNotNull(repo.findById(u.getId()).getUpdatedAtDateTime());
    }

    @Test
    void testSave_NullUser() {
        assertFalse(repo.save(null));
    }

    @Test
    void testSave_FailureWrite() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(false);

        User u = new User();
        u.setName("X");

        assertFalse(repo.save(u));
    }

    // -------------------------------
    // findById()
    // -------------------------------

    @Test
    void testFindById_NotFound() {
        assertNull(repo.findById("ABC"));
    }

    @Test
    void testFindById_Found() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        User u = new User();
        u.setName("A");
        repo.save(u);

        assertNotNull(repo.findById(u.getId()));
    }

    // -------------------------------
    // findByEmail()
    // -------------------------------

    @Test
    void testFindByEmail_Found() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        User u1 = new User();
        u1.setEmail("a@test.com");
        repo.save(u1);

        User u2 = repo.findByEmail("a@test.com");
        assertNotNull(u2);
    }

    @Test
    void testFindByEmail_NotFound() {
        assertNull(repo.findByEmail("none@test.com"));
    }

    @Test
    void testFindByEmail_Null() {
        assertNull(repo.findByEmail(null));
    }

    // -------------------------------
    // delete()
    // -------------------------------

    @Test
    void testDelete_Success() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        User u = new User();
        repo.save(u);

        assertTrue(repo.delete(u.getId()));
    }

    @Test
    void testDelete_NotFound() {
        assertFalse(repo.delete("UNKNOWN"));
    }

    @Test
    void testDelete_NullId() {
        assertFalse(repo.delete(null));
    }

    // -------------------------------
    // clearAll()
    // -------------------------------

    @Test
    void testClearAll() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(new User());
        repo.save(new User());

        repo.clearAll();

        assertEquals(0, repo.findAll().size());
    }

    // -------------------------------
    // findAll()
    // -------------------------------

    @Test
    void testFindAll() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        repo.save(new User());
        repo.save(new User());

        assertEquals(2, repo.findAll().size());
    }
}

