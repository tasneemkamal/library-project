package library.services;

import library.models.CD;
import library.models.User;
import library.models.CDLoan;
import library.repositories.CDLoanRepository;
import library.repositories.CDRepository;
import library.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Additional tests for CDLoanService edge cases
 * @author Library Team
 * @version 1.0
 */
@DisplayName("CDLoanService Additional Tests")
class CDLoanServiceAdditionalTest {
    private CDLoanService cdLoanService;
    private CDLoanRepository cdLoanRepository;
    private CDRepository cdRepository;
    private UserRepository userRepository;
    private CDFineService cdFineService;

    @BeforeEach
    void setUp() {
        cdLoanRepository = mock(CDLoanRepository.class);
        cdRepository = mock(CDRepository.class);
        userRepository = mock(UserRepository.class);
        cdFineService = mock(CDFineService.class);
        cdLoanService = new CDLoanService(cdLoanRepository, cdRepository, userRepository, cdFineService);
    }

    @Test
    @DisplayName("Should get CD loan by ID successfully")
    void testGetCDLoanByIdSuccess() {
        // Arrange
        String cdLoanId = "cdloan123";
        CDLoan expectedLoan = new CDLoan("user123", "cd456");
        expectedLoan.setId(cdLoanId);

        when(cdLoanRepository.findById(cdLoanId)).thenReturn(expectedLoan);

        // Act
        CDLoan result = cdLoanService.getCDLoanById(cdLoanId);

        // Assert
        assertNotNull(result);
        assertEquals(cdLoanId, result.getId());
        verify(cdLoanRepository).findById(cdLoanId);
    }

    @Test
    @DisplayName("Should return null for non-existent CD loan ID")
    void testGetCDLoanByIdNonExistent() {
        // Arrange
        String nonExistentId = "non_existent";
        when(cdLoanRepository.findById(nonExistentId)).thenReturn(null);

        // Act
        CDLoan result = cdLoanService.getCDLoanById(nonExistentId);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should calculate correct overdue days for CD")
    void testCDOverdueDaysCalculation() {
        // Arrange
        CDLoan cdLoan = new CDLoan("user123", "cd456");
        // Set due date to 3 days ago to make it overdue
        cdLoan.setDueDateTime(LocalDateTime.now().minusDays(3));

        // Act & Assert
        assertTrue(cdLoan.isOverdue(), "CD loan should be overdue");
        assertTrue(cdLoan.getOverdueDays() >= 3, "Overdue days should be at least 3");
    }

    @Test
    @DisplayName("Should not have overdue days for non-overdue CD")
    void testCDNoOverdueDays() {
        // Arrange
        CDLoan cdLoan = new CDLoan("user123", "cd456");
        // Set due date to 5 days in the future
        cdLoan.setDueDateTime(LocalDateTime.now().plusDays(5));

        // Act & Assert
        assertFalse(cdLoan.isOverdue(), "CD loan should not be overdue");
        assertEquals(0, cdLoan.getOverdueDays(), "Overdue days should be 0");
    }

    @Test
    @DisplayName("Should handle empty CD loans list")
    void testEmptyCDLoans() {
        // Arrange
        String userId = "user_with_no_loans";
        when(cdLoanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        boolean hasOverdue = cdLoanService.hasOverdueCDs(userId);

        // Assert
        assertFalse(hasOverdue);
    }

    @Test
    @DisplayName("Should detect overdue CDs correctly")
    void testHasOverdueCDs() {
        // Arrange
        String userId = "user123";
        CDLoan overdueLoan = new CDLoan(userId, "cd456");
        overdueLoan.setDueDateTime(LocalDateTime.now().minusDays(1)); // 1 day overdue

        when(cdLoanRepository.findByUserId(userId)).thenReturn(Collections.singletonList(overdueLoan));

        // Act
        boolean result = cdLoanService.hasOverdueCDs(userId);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should not detect non-overdue CDs as overdue")
    void testNoOverdueCDs() {
        // Arrange
        String userId = "user123";
        CDLoan currentLoan = new CDLoan(userId, "cd456");
        currentLoan.setDueDateTime(LocalDateTime.now().plusDays(3)); // Due in 3 days

        when(cdLoanRepository.findByUserId(userId)).thenReturn(Collections.singletonList(currentLoan));

        // Act
        boolean result = cdLoanService.hasOverdueCDs(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle returned CD loans correctly")
    void testReturnedCDLoans() {
        // Arrange
        String userId = "user123";
        CDLoan returnedLoan = new CDLoan(userId, "cd456");
        returnedLoan.setReturned(true);
        returnedLoan.setDueDateTime(LocalDateTime.now().minusDays(5)); // Would be overdue if not returned

        when(cdLoanRepository.findByUserId(userId)).thenReturn(Collections.singletonList(returnedLoan));

        // Act
        boolean result = cdLoanService.hasOverdueCDs(userId);

        // Assert
        assertFalse(result, "Returned CD loans should not be considered overdue");
    }

    @Test
    @DisplayName("Should calculate CD loan period correctly")
    void testCDLoanPeriod() {
        // Arrange
        CDLoan cdLoan = new CDLoan("user123", "cd456");

        // Act
        LocalDateTime borrowDate = cdLoan.getBorrowDateTime();
        LocalDateTime dueDate = cdLoan.getDueDateTime();

        // Assert
        assertNotNull(borrowDate);
        assertNotNull(dueDate);
        
        long daysBetween = java.time.Duration.between(borrowDate, dueDate).toDays();
        assertEquals(7, daysBetween, "CD loan period should be 7 days");
    }
}