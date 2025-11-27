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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;

/**
 * Tests specifically for CD overdue scenarios
 * @author Library Team
 * @version 1.0
 */
@DisplayName("CD Overdue Scenarios Tests")
class CDOverdueTest {
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
    @DisplayName("Should create fine for exactly 3 days overdue CD")
    void testExactOverdueDaysFine() {
        // Arrange
        String cdLoanId = "cdloan123";
        String userId = "user123";
        String cdId = "cd456";
        
        CDLoan cdLoan = new CDLoan(userId, cdId);
        cdLoan.setId(cdLoanId);
        // Set to exactly 3 days overdue
        cdLoan.setBorrowDateTime(LocalDateTime.now().minusDays(10)); // Borrowed 10 days ago
        cdLoan.setDueDateTime(LocalDateTime.now().minusDays(3)); // Due 3 days ago
        
        CD cd = new CD("Test CD", "Test Artist", "Test Genre", 10, "Test Pub", 2024);

        when(cdLoanRepository.findById(cdLoanId)).thenReturn(cdLoan);
        when(cdRepository.findById(cdId)).thenReturn(cd);
        when(cdLoanRepository.update(cdLoan)).thenReturn(true);
        when(cdRepository.update(cd)).thenReturn(true);
        when(cdFineService.createCDFine(userId, cdLoanId, 60.0)).thenReturn(true); // 3 days * 20 NIS

        // Act
        boolean result = cdLoanService.returnCD(cdLoanId);

        // Assert
        assertTrue(result);
        assertTrue(cdLoan.isOverdue());
        assertEquals(3, cdLoan.getOverdueDays());
        verify(cdFineService).createCDFine(userId, cdLoanId, 60.0);
    }

    @Test
    @DisplayName("Should create fine for 1 day overdue CD")
    void testOneDayOverdueFine() {
        // Arrange
        String cdLoanId = "cdloan123";
        String userId = "user123";
        String cdId = "cd456";
        
        CDLoan cdLoan = new CDLoan(userId, cdId);
        cdLoan.setId(cdLoanId);
        // Set to exactly 1 day overdue
        cdLoan.setDueDateTime(LocalDateTime.now().minusDays(1));
        
        CD cd = new CD("Test CD", "Test Artist", "Test Genre", 10, "Test Pub", 2024);

        when(cdLoanRepository.findById(cdLoanId)).thenReturn(cdLoan);
        when(cdRepository.findById(cdId)).thenReturn(cd);
        when(cdLoanRepository.update(cdLoan)).thenReturn(true);
        when(cdRepository.update(cd)).thenReturn(true);
        when(cdFineService.createCDFine(userId, cdLoanId, 20.0)).thenReturn(true); // 1 day * 20 NIS

        // Act
        boolean result = cdLoanService.returnCD(cdLoanId);

        // Assert
        assertTrue(result);
        assertTrue(cdLoan.isOverdue());
        assertEquals(1, cdLoan.getOverdueDays());
        verify(cdFineService).createCDFine(userId, cdLoanId, 20.0);
    }

    @Test
    @DisplayName("Should not create fine for CD returned on due date")
    void testNoFineForOnTimeReturn() {
        // Arrange
        String cdLoanId = "cdloan123";
        String userId = "user123";
        String cdId = "cd456";
        
        CDLoan cdLoan = new CDLoan(userId, cdId);
        cdLoan.setId(cdLoanId);
        // Set due date to today (not overdue)
        cdLoan.setDueDateTime(LocalDateTime.now());
        
        CD cd = new CD("Test CD", "Test Artist", "Test Genre", 10, "Test Pub", 2024);

        when(cdLoanRepository.findById(cdLoanId)).thenReturn(cdLoan);
        when(cdRepository.findById(cdId)).thenReturn(cd);
        when(cdLoanRepository.update(cdLoan)).thenReturn(true);
        when(cdRepository.update(cd)).thenReturn(true);

        // Act
        boolean result = cdLoanService.returnCD(cdLoanId);

        // Assert
        assertTrue(result);
        assertFalse(cdLoan.isOverdue());
        assertEquals(0, cdLoan.getOverdueDays());
        verify(cdFineService, never()).createCDFine(anyString(), anyString(), anyDouble());
    }

    @Test
    @DisplayName("Should verify CD loan is actually overdue")
    void testCDLoanOverdueVerification() {
        // Create a CD loan that should be overdue
        CDLoan cdLoan = new CDLoan("user123", "cd456");
        cdLoan.setDueDateTime(LocalDateTime.now().minusDays(5)); // 5 days ago
        
        // Verify it's actually overdue
        assertTrue(cdLoan.isOverdue(), "CD loan should be overdue when due date is in past");
        assertTrue(cdLoan.getOverdueDays() >= 5, "CD loan should have at least 5 overdue days");
        
        // Test with future due date
        CDLoan futureLoan = new CDLoan("user123", "cd456");
        futureLoan.setDueDateTime(LocalDateTime.now().plusDays(3)); // 3 days in future
        
        assertFalse(futureLoan.isOverdue(), "CD loan should not be overdue when due date is in future");
        assertEquals(0, futureLoan.getOverdueDays(), "CD loan should have 0 overdue days when due date is in future");
    }
}