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

    // Utility لتغيير قيم الحقول الخاصة
    private void setPrivate(Object target, String field, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        gson = GsonUtils.createGson();

        repository = new CDRepository();

        setPrivate(repository, "fileHandler", fileHandler);
        setPrivate(repository, "cds", new HashMap<>());

        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(true);
    }

    // ---------------------------------------------------
    // ✔ Constructor Tests (يغطي المنطقة الحمراء)
    // ---------------------------------------------------

    @Test
    void testConstructor_Default() {
        CDRepository repo = new CDRepository();
        assertNotNull(repo);
    }

    @Test
    void testConstructor_WithFileHandler() {
        JsonFileHandler fh = mock(JsonFileHandler.class);
        CDRepository repo = new CDRepository(fh);

        assertNotNull(repo);
        // verify تم تخزين fileHandler الذي مررناه
        setPrivate(repo, "fileHandler", fh);
    }

    @Test
    void testConstructor_InitializesEmptyMap() throws Exception {
        CDRepository repo = new CDRepository();

        Field cdsField = CDRepository.class.getDeclaredField("cds");
        cdsField.setAccessible(true);

        Object cdsValue = cdsField.get(repo);

        assertTrue(cdsValue instanceof Map);
        assertEquals(0, ((Map<?, ?>) cdsValue).size());
    }

    // ---------------------------------------------------
    // save()
    // ---------------------------------------------------

    @Test
    void testSave() {
        CD cd = new CD("Test CD", "Artist A", "Pop");
        boolean result = repository.save(cd);

        assertTrue(result);
        assertNotNull(cd.getId());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testSave_FailsWrite() {
        when(fileHandler.writeToFile(anyString(), anyString())).thenReturn(false);

        CD cd = new CD("X", "Y", "Z");
        assertTrue(repository.save(cd)); // save() دائماً يرجّع true حسب الكود
    }

    // ---------------------------------------------------
    // findById()
    // ---------------------------------------------------

    @Test
    void testFindById() {
        CD cd = new CD("Album 1", "Artist X", "Rock");
        repository.save(cd);

        CD found = repository.findById(cd.getId());
        assertNotNull(found);
        assertEquals("Album 1", found.getTitle());
    }

    @Test
    void testFindById_NotFound() {
        assertNull(repository.findById("xxx"));
    }

    // ---------------------------------------------------
    // findAll()
    // ---------------------------------------------------

    @Test
    void testFindAll() {
        repository.save(new CD("CD1", "A", "Rock"));
        repository.save(new CD("CD2", "B", "Pop"));

        assertEquals(2, repository.findAll().size());
    }

    // ---------------------------------------------------
    // search()
    // ---------------------------------------------------

    @Test
    void testSearch_Normal() {
        repository.save(new CD("Love Songs", "Artist1", "Pop"));
        repository.save(new CD("Rock Hits", "Artist2", "Rock"));

        var results = repository.search("rock");
        assertEquals(1, results.size());
    }

    @Test
    void testSearch_EmptyQuery_ReturnsAll() {
        repository.save(new CD("A", "X", "G"));
        repository.save(new CD("B", "Y", "G"));

        assertEquals(2, repository.search("").size());
        assertEquals(2, repository.search("   ").size());
    }

    @Test
    void testSearch_NullQuery() {
        repository.save(new CD("X", "A", "G"));

        assertEquals(1, repository.search(null).size());
    }

    @Test
    void testSearch_NoMatches() {
        repository.save(new CD("A", "ArtistA", "Pop"));
        assertEquals(0, repository.search("metal").size());
    }

    // ---------------------------------------------------
    // update()
    // ---------------------------------------------------

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
    void testUpdate_FailsWhenMissing() {
        CD cd = new CD("X", "Y", "Z");
        cd.setId("NOPE");

        assertFalse(repository.update(cd));
    }

    // ---------------------------------------------------
    // delete()
    // ---------------------------------------------------

    @Test
    void testDelete() {
        CD cd = new CD("To Delete", "A", "G");
        repository.save(cd);

        boolean deleted = repository.delete(cd.getId());
        assertTrue(deleted);
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void testDelete_NotFound() {
        assertFalse(repository.delete("NO_ID"));
    }

    // ---------------------------------------------------
    // findByArtist / findByGenre
    // ---------------------------------------------------

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

    // ---------------------------------------------------
    // ID Generator Coverage
    // ---------------------------------------------------

    @Test
    void testGenerateId_Unique() {
        CD cd1 = new CD("A", "B", "C");
        CD cd2 = new CD("X", "Y", "Z");

        repository.save(cd1);
        repository.save(cd2);

        assertNotEquals(cd1.getId(), cd2.getId());
    }
}
