package library.repositories;

import library.models.CD;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CDRepositoryTest {

    private CDRepository repository;
    private Gson gson;

    @Mock
    private JsonFileHandler fileHandler;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        gson = GsonUtils.createGson();

        // Create a real repository instance
        repository = new CDRepository();

        // Inject mock file handler
        Field fhField = CDRepository.class.getDeclaredField("fileHandler");
        fhField.setAccessible(true);
        fhField.set(repository, fileHandler);

        // Override loaded CDs with an empty map
        Field cdsField = CDRepository.class.getDeclaredField("cds");
        cdsField.setAccessible(true);
        cdsField.set(repository, new HashMap<String, CD>());

        // Mock fileHandler behavior
        when(fileHandler.readFromFile(anyString())).thenReturn("{}");
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);
    }

    @Test
    void testSave() {
        CD cd = new CD("Test CD", "Artist A", "Pop");
        boolean result = repository.save(cd);

        assertTrue(result);
        assertNotNull(cd.getId());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testFindById() {
        CD cd = new CD("Album 1", "Artist X", "Rock");
        repository.save(cd);

        CD found = repository.findById(cd.getId());
        assertNotNull(found);
        assertEquals("Album 1", found.getTitle());
    }

    @Test
    void testFindAll() {
        repository.save(new CD("CD1", "A", "Rock"));
        repository.save(new CD("CD2", "B", "Pop"));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void testSearch() {
        repository.save(new CD("Love Songs", "Artist1", "Pop"));
        repository.save(new CD("Rock Hits", "Artist2", "Rock"));

        var results = repository.search("rock");
        assertEquals(1, results.size());
    }

    @Test
    void testUpdate() {
        CD cd = new CD("Old Title", "Artist", "Genre");
        repository.save(cd);

        cd.setTitle("New Title");
        boolean updated = repository.update(cd);

        assertTrue(updated);
        assertEquals("New Title", repository.findById(cd.getId()).getTitle());
    }

    @Test
    void testDelete() {
        CD cd = new CD("To Delete", "A", "G");
        repository.save(cd);

        boolean deleted = repository.delete(cd.getId());
        assertTrue(deleted);
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void testFindByArtist() {
        repository.save(new CD("CD1", "SameArtist", "Rock"));
        repository.save(new CD("CD2", "SameArtist", "Pop"));
        repository.save(new CD("CD3", "OtherArtist", "Rock"));

        var results = repository.findByArtist("SameArtist");
        assertEquals(2, results.size());
    }

    @Test
    void testFindByGenre() {
        repository.save(new CD("CD1", "A", "Rock"));
        repository.save(new CD("CD2", "B", "Rock"));
        repository.save(new CD("CD3", "C", "Pop"));

        var results = repository.findByGenre("Rock");
        assertEquals(2, results.size());
    }
}
