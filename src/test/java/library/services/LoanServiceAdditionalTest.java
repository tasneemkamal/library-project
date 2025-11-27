package library.services;

import library.models.Book;
import library.models.CDLoan;
import library.models.User;
import library.models.Loan;
import library.repositories.LoanRepository;
import library.repositories.BookRepository;
import library.repositories.UserRepository;
import library.utils.TestDateUtils;
import library.patterns.strategy.FineStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Additional tests for LoanService edge cases
 * @author Library Team
 * @version 1.0
 */
@DisplayName("LoanService Additional Tests")
class LoanServiceAdditionalTest {
    private LoanService loanService;
    private LoanRepository loanRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private FineService fineService;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        bookRepository = mock(BookRepository.class);
        userRepository = mock(UserRepository.class);
        fineService = mock(FineService.class);
        loanService = new LoanService(loanRepository, bookRepository, userRepository, fineService);
    }

    @Test
    @DisplayName("Should get correct loan period for books")
    void testGetLoanPeriodForBooks() {
        assertEquals(28, loanService.getLoanPeriod("BOOK"));
        assertEquals(28, loanService.getLoanPeriod("book"));
        assertEquals(28, loanService.getLoanPeriod("Book"));
        assertEquals(28, loanService.getLoanPeriod("BoOk")); // Mixed case
    }

    @Test
    @DisplayName("Should get correct loan period for CDs")
    void testGetLoanPeriodForCDs() {
        assertEquals(7, loanService.getLoanPeriod("CD"));
        assertEquals(7, loanService.getLoanPeriod("cd"));
        assertEquals(7, loanService.getLoanPeriod("Cd"));
        assertEquals(7, loanService.getLoanPeriod("cD")); // Mixed case
    }

    @Test
    @DisplayName("Should default to book loan period for unknown types")
    void testGetLoanPeriodForUnknownTypes() {
        assertEquals(28, loanService.getLoanPeriod("DVD"));
        assertEquals(28, loanService.getLoanPeriod("MAGAZINE"));
        assertEquals(28, loanService.getLoanPeriod("JOURNAL"));
        assertEquals(28, loanService.getLoanPeriod("UNKNOWN"));
    }

    @Test
    @DisplayName("Should handle null and empty types gracefully")
    void testGetLoanPeriodForNullAndEmpty() {
        assertEquals(28, loanService.getLoanPeriod("")); // Empty string
        assertEquals(28, loanService.getLoanPeriod("   ")); // Whitespace
        assertEquals(28, loanService.getLoanPeriod(null)); // Null
    }

    @Test
    @DisplayName("Should get correct fine strategy for books")
    void testGetFineStrategyForBooks() {
        FineStrategy strategy = loanService.getFineStrategy("BOOK");
        assertNotNull(strategy);
        assertTrue(strategy instanceof library.patterns.strategy.BookFineStrategy);
        assertEquals(10.0, strategy.getDailyRate());
    }

    @Test
    @DisplayName("Should get correct fine strategy for CDs")
    void testGetFineStrategyForCDs() {
        FineStrategy strategy = loanService.getFineStrategy("CD");
        assertNotNull(strategy);
        assertTrue(strategy instanceof library.patterns.strategy.CDFineStrategy);
        assertEquals(20.0, strategy.getDailyRate());
    }

    @Test
    @DisplayName("Should default to book fine strategy for unknown types")
    void testGetFineStrategyForUnknownTypes() {
        FineStrategy strategy = loanService.getFineStrategy("DVD");
        assertNotNull(strategy);
        assertTrue(strategy instanceof library.patterns.strategy.BookFineStrategy);
    }

    @Test
    @DisplayName("Should handle null type for fine strategy")
    void testGetFineStrategyForNullType() {
        FineStrategy strategy = loanService.getFineStrategy(null);
        assertNotNull(strategy);
        assertTrue(strategy instanceof library.patterns.strategy.BookFineStrategy);
    }

    @Test
    @DisplayName("Should create overdue loan for testing")
    void testOverdueLoanCreation() {
        // Create a loan with due date in the past to make it overdue
        Loan overdueLoan = new Loan("user123", "book456", 28);
        
        // Manually set the due date to the past to simulate overdue
        overdueLoan.setDueDateTime(LocalDateTime.now().minusDays(5));
        
        assertTrue(overdueLoan.isOverdue());
        assertTrue(overdueLoan.getOverdueDays() > 0);
    }

    @Test
    @DisplayName("Should not have overdue books for user with no loans")
    void testNoOverdueForUserWithNoLoans() {
        String userId = "user_with_no_loans";
        when(loanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        
        assertFalse(loanService.hasOverdueBooks(userId));
    }

    @Test
    @DisplayName("Should not have overdue books for user with only returned loans")
    void testNoOverdueForUserWithReturnedLoans() {
        String userId = "user123";
        Loan returnedLoan = new Loan(userId, "book456", 28);
        returnedLoan.setReturned(true);
        
        when(loanRepository.findByUserId(userId)).thenReturn(Collections.singletonList(returnedLoan));
        
        assertFalse(loanService.hasOverdueBooks(userId));
    }

    @Test
    @DisplayName("Should detect overdue books correctly")
    void testHasOverdueBooksDetection() {
        String userId = "user123";
        
        // Create an overdue loan
        Loan overdueLoan = new Loan(userId, "book456", 28);
        overdueLoan.setDueDateTime(LocalDateTime.now().minusDays(1)); // Due yesterday
        
        when(loanRepository.findByUserId(userId)).thenReturn(Collections.singletonList(overdueLoan));
        
        assertTrue(loanService.hasOverdueBooks(userId));
    }

    @Test
    @DisplayName("Should not detect non-overdue books as overdue")
    void testNoOverdueForFutureDueBooks() {
        String userId = "user123";
        
        // Create a loan with future due date
        Loan futureLoan = new Loan(userId, "book456", 28);
        futureLoan.setDueDateTime(LocalDateTime.now().plusDays(5)); // Due in 5 days
        
        when(loanRepository.findByUserId(userId)).thenReturn(Collections.singletonList(futureLoan));
        
        assertFalse(loanService.hasOverdueBooks(userId));
    }

    @Test
    @DisplayName("Should handle mixed loans (overdue and not overdue)")
    void testMixedLoansScenario() {
        String userId = "user123";
        
        // Create an overdue loan
        Loan overdueLoan = new Loan(userId, "book1", 28);
        overdueLoan.setDueDateTime(LocalDateTime.now().minusDays(3));
        
        // Create a non-overdue loan
        Loan currentLoan = new Loan(userId, "book2", 28);
        currentLoan.setDueDateTime(LocalDateTime.now().plusDays(5));
        
        when(loanRepository.findByUserId(userId)).thenReturn(java.util.Arrays.asList(overdueLoan, currentLoan));
        
        // Should return true because at least one loan is overdue
        assertTrue(loanService.hasOverdueBooks(userId));
    }
    
   
    @Test
    @DisplayName("Should create overdue CD loan using TestDateUtils")
    void testCreateOverdueCDLoanWithUtils() {
        // Arrange
        String userId = "user123";
        String cdId = "cd456";
        int overdueDays = 3;
        
        CDLoan overdueLoan = TestDateUtils.createOverdueCDLoan(userId, cdId, overdueDays);

        // Act & Assert
        assertTrue(overdueLoan.isOverdue());
        assertTrue(overdueLoan.getOverdueDays() >= overdueDays);
    }

    @Test
    @DisplayName("Should create future due CD loan using TestDateUtils")
    void testCreateFutureDueCDLoanWithUtils() {
        // Arrange
        String userId = "user123";
        String cdId = "cd456";
        int daysUntilDue = 5;
        
        CDLoan futureLoan = TestDateUtils.createFutureDueCDLoan(userId, cdId, daysUntilDue);

        // Act & Assert
        assertFalse(futureLoan.isOverdue());
        assertEquals(0, futureLoan.getOverdueDays());
    }
}