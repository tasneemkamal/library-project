package library.repositories;

import library.models.CDLoan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CDLoanRepositoryTest {

    private CDLoanRepository repo;

    @BeforeEach
    void setup() {
      
        File file = new File("data/cdloans.json");
        if (file.exists()) {
            file.delete();
        }
        repo = new CDLoanRepository();
    }

    @Test
    void testSaveAndFindById() {
        CDLoan loan = new CDLoan("USER1", "CD1");

        boolean saved = repo.save(loan);

        assertTrue(saved);
        assertNotNull(loan.getId());

        CDLoan found = repo.findById(loan.getId());
        assertNotNull(found);
        assertEquals("USER1", found.getUserId());
        assertEquals("CD1", found.getCdId());
    }

    @Test
    void testFindByUserId() {
        CDLoan loan1 = new CDLoan("U1", "CD1");
        CDLoan loan2 = new CDLoan("U1", "CD2");
        CDLoan loan3 = new CDLoan("U2", "CD3");

        repo.save(loan1);
        repo.save(loan2);
        repo.save(loan3);

        List<CDLoan> list = repo.findByUserId("U1");
        assertEquals(2, list.size());
    }

    @Test
    void testFindByCDId() {
        CDLoan loan1 = new CDLoan("U1", "CDA");
        CDLoan loan2 = new CDLoan("U2", "CDA");

        repo.save(loan1);
        repo.save(loan2);

        List<CDLoan> list = repo.findByCDId("CDA");

        assertEquals(2, list.size());
    }

    @Test
    void testUpdateExistingLoan() {
        CDLoan loan = new CDLoan("U1", "CD1");
        repo.save(loan);

        loan.setReturned(true);
        loan.setReturnDateTime(LocalDateTime.now());

        boolean updated = repo.update(loan);

        assertTrue(updated);

        CDLoan found = repo.findById(loan.getId());
        assertTrue(found.isReturned());
        assertNotNull(found.getReturnDate());
    }

    @Test
    void testUpdateNonExistingLoan() {
        CDLoan loan = new CDLoan("U1", "CD1");
        loan.setId("UNKNOWN");

        boolean updated = repo.update(loan);

        assertFalse(updated);
    }

    @Test
    void testFindAll() {
        repo.save(new CDLoan("A", "X"));
        repo.save(new CDLoan("B", "Y"));
        repo.save(new CDLoan("C", "Z"));

        List<CDLoan> all = repo.findAll();

        assertEquals(3, all.size());
    }

    @Test
    void testFindOverdueCDLoans() {
        CDLoan overdue = new CDLoan("U1", "CD1");
        overdue.setDueDateTime(LocalDateTime.now().minusDays(5)); // متأخر
        repo.save(overdue);

        CDLoan notOverdue = new CDLoan("U2", "CD2");
        repo.save(notOverdue);

        List<CDLoan> list = repo.findOverdueCDLoans();

        assertEquals(1, list.size());
        assertEquals("U1", list.get(0).getUserId());
    }
}
