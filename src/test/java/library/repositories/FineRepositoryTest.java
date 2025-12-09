package library.repositories;

import library.models.Fine;
import library.utils.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FineRepositoryTest {

    private FineRepository fineRepository;
    private JsonFileHandler fileHandlerMock;

    @BeforeEach
    void setup() {
        fileHandlerMock = mock(JsonFileHandler.class);

        
        when(fileHandlerMock.readFromFile(anyString())).thenReturn("");

        fineRepository = new FineRepository();

        
        var fileHandlerField = assertDoesNotThrow(() ->
                FineRepository.class.getDeclaredField("fileHandler"));
        fileHandlerField.setAccessible(true);
        assertDoesNotThrow(() -> fileHandlerField.set(fineRepository, fileHandlerMock));

      
        when(fileHandlerMock.writeToFile(anyString(), anyString())).thenReturn(true);
    }

    @Test
    void testSaveNewFine() {
        Fine fine = new Fine("user1", "loan1", 50.0);

        boolean result = fineRepository.save(fine);

        assertTrue(result);
        assertNotNull(fine.getId());
        assertEquals("user1", fine.getUserId());
    }

    @Test
    void testFindById() {
        Fine fine = new Fine("user1", "loan1", 30.0);
        fineRepository.save(fine);

        Fine found = fineRepository.findById(fine.getId());
        assertNotNull(found);
        assertEquals("loan1", found.getLoanId());
    }

    @Test
    void testFindByUserId() {
        Fine f1 = new Fine("userX", "l1", 10);
        Fine f2 = new Fine("userX", "l2", 20);
        Fine f3 = new Fine("other", "l3", 5);

        fineRepository.save(f1);
        fineRepository.save(f2);
        fineRepository.save(f3);

        List<Fine> list = fineRepository.findByUserId("userX");

        assertEquals(2, list.size());
    }

    @Test
    void testFindUnpaidFines() {
        Fine unpaid = new Fine("u1", "l1", 15);
        Fine paid = new Fine("u2", "l2", 15);
        paid.setPaid(true);

        fineRepository.save(unpaid);
        fineRepository.save(paid);

        List<Fine> list = fineRepository.findUnpaidFines();

        assertEquals(1, list.size());
        assertFalse(list.get(0).isPaid());
    }

    @Test
    void testUpdateFine() {
        Fine fine = new Fine("user", "loan", 25);
        fineRepository.save(fine);

        fine.setPaidAmount(10);

        boolean result = fineRepository.update(fine);

        assertTrue(result);
        Fine updated = fineRepository.findById(fine.getId());
        assertEquals(10, updated.getPaidAmount());
    }

    @Test
    void testUpdateNonExistingFine() {
        Fine fine = new Fine("x", "y", 10);
        fine.setId("DOES_NOT_EXIST");

        boolean result = fineRepository.update(fine);

        assertFalse(result);
    }

    @Test
    void testFindAll() {
        fineRepository.save(new Fine("u1", "a", 10));
        fineRepository.save(new Fine("u2", "b", 20));

        List<Fine> list = fineRepository.findAll();

        assertEquals(2, list.size());
    }
}
