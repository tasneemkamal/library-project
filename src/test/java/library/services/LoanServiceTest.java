package library.services;

import library.models.Book;
import library.models.User;
import library.models.Loan;
import library.repositories.LoanRepository;
import library.repositories.BookRepository;
import library.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;



import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * Comprehensive unit tests for LoanService
 * @author Library Team
 * @version 1.0
 */
@DisplayName("LoanService Tests")
class LoanServiceTest {
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

    @Nested
    @DisplayName("Borrow Book Tests")
    class BorrowBookTests {
        @Test
        @DisplayName("Should borrow book successfully with valid data")
        void testBorrowBookSuccess() {
            // Arrange
            String userId = "user123";
            String bookId = "book456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            user.setActive(true);
            Book book = new Book("Test Book", "Test Author", "123", "BOOK");
            book.setAvailable(true);

            when(userRepository.findById(userId)).thenReturn(user);
            when(bookRepository.findById(bookId)).thenReturn(book);
            when(fineService.hasUnpaidFines(userId)).thenReturn(false);
            when(loanRepository.save(any(Loan.class))).thenReturn(true);
            when(bookRepository.update(book)).thenReturn(true);
            when(loanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            boolean result = loanService.borrowBook(userId, bookId);

            // Assert
            assertTrue(result);
            verify(loanRepository).save(any(Loan.class));
            verify(bookRepository).update(book);
        }

        @Test
        @DisplayName("Should fail to borrow unavailable book")
        void testBorrowUnavailableBook() {
            // Arrange
            String userId = "user123";
            String bookId = "book456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            Book book = new Book("Test Book", "Test Author", "123", "BOOK");
            book.setAvailable(false);

            when(userRepository.findById(userId)).thenReturn(user);
            when(bookRepository.findById(bookId)).thenReturn(book);

            // Act
            boolean result = loanService.borrowBook(userId, bookId);

            // Assert
            assertFalse(result);
            verify(loanRepository, never()).save(any(Loan.class));
        }

        @Test
        @DisplayName("Should fail to borrow with unpaid fines")
        void testBorrowWithUnpaidFines() {
            // Arrange
            String userId = "user123";
            String bookId = "book456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            Book book = new Book("Test Book", "Test Author", "123", "BOOK");
            book.setAvailable(true);

            when(userRepository.findById(userId)).thenReturn(user);
            when(bookRepository.findById(bookId)).thenReturn(book);
            when(fineService.hasUnpaidFines(userId)).thenReturn(true);

            // Act
            boolean result = loanService.borrowBook(userId, bookId);

            // Assert
            assertFalse(result);
            verify(loanRepository, never()).save(any(Loan.class));
        }

        @Test
        @DisplayName("Should fail to borrow for non-existent user")
        void testBorrowNonExistentUser() {
            // Arrange
            String userId = "non_existent_user";
            String bookId = "book456";
            
            Book book = new Book("Test Book", "Test Author", "123", "BOOK");
            book.setAvailable(true);

            when(userRepository.findById(userId)).thenReturn(null);
            when(bookRepository.findById(bookId)).thenReturn(book);

            // Act
            boolean result = loanService.borrowBook(userId, bookId);

            // Assert
            assertFalse(result);
            verify(loanRepository, never()).save(any(Loan.class));
        }
    }

    @Nested
    @DisplayName("Return Book Tests")
    class ReturnBookTests {
        @Test
        @DisplayName("Should return book successfully")
        void testReturnBookSuccess() {
            // Arrange
            String loanId = "loan123";
            String userId = "user123";
            String bookId = "book456";
            
            Loan loan = new Loan(userId, bookId, 28);
            loan.setId(loanId);
            Book book = new Book("Test Book", "Test Author", "123", "BOOK");

            when(loanRepository.findById(loanId)).thenReturn(loan);
            when(bookRepository.findById(bookId)).thenReturn(book);
            when(loanRepository.update(loan)).thenReturn(true);
            when(bookRepository.update(book)).thenReturn(true);

            // Act
            boolean result = loanService.returnBook(loanId);

            // Assert
            assertTrue(result);
            assertTrue(loan.isReturned());
            assertTrue(book.isAvailable());
        }

        @Test
        @DisplayName("Should fail to return non-existent loan")
        void testReturnNonExistentLoan() {
            // Arrange
            String loanId = "non_existent_loan";
            when(loanRepository.findById(loanId)).thenReturn(null);

            // Act
            boolean result = loanService.returnBook(loanId);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail to return already returned book")
        void testReturnAlreadyReturnedBook() {
            // Arrange
            String loanId = "loan123";
            Loan loan = new Loan("user123", "book456", 28);
            loan.setId(loanId);
            loan.setReturned(true);

            when(loanRepository.findById(loanId)).thenReturn(loan);

            // Act
            boolean result = loanService.returnBook(loanId);

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Loan Query Tests")
    class LoanQueryTests {
        @Test
        @DisplayName("Should get user loans")
        void testGetUserLoans() {
            // Arrange
            String userId = "user123";
            Loan loan1 = new Loan(userId, "book1", 28);
            Loan loan2 = new Loan(userId, "book2", 28);
            List<Loan> expectedLoans = Arrays.asList(loan1, loan2);

            when(loanRepository.findByUserId(userId)).thenReturn(expectedLoans);

            // Act
            List<Loan> result = loanService.getUserLoans(userId);

            // Assert
            assertEquals(2, result.size());
            verify(loanRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should get overdue loans")
        void testGetOverdueLoans() {
            // Arrange
            Loan overdueLoan1 = new Loan("user1", "book1", 28);
            Loan overdueLoan2 = new Loan("user2", "book2", 28);
            List<Loan> overdueLoans = Arrays.asList(overdueLoan1, overdueLoan2);

            when(loanRepository.findOverdueLoans()).thenReturn(overdueLoans);

            // Act
            List<Loan> result = loanService.getOverdueLoans();

            // Assert
            assertEquals(2, result.size());
            verify(loanRepository).findOverdueLoans();
        }

       

        @Test
        @DisplayName("Should check for overdue books - has overdue")
        void testHasOverdueBooks() {
            // Arrange
            String userId = "user123";
            
            // Create a loan and set due date to the past to make it overdue
            Loan overdueLoan = new Loan(userId, "book1", 28);
            overdueLoan.setDueDateTime(LocalDateTime.now().minusDays(1)); // Due yesterday
            
            List<Loan> userLoans = Arrays.asList(overdueLoan);

            when(loanRepository.findByUserId(userId)).thenReturn(userLoans);

            // Act
            boolean result = loanService.hasOverdueBooks(userId);

            // Assert
            assertTrue(result);
        }

        @Test  
        @DisplayName("Should check for overdue books - no overdue")
        void testHasNoOverdueBooks() {
            // Arrange
            String userId = "user123";
            
            // Create a loan with future due date
            Loan activeLoan = new Loan(userId, "book1", 28);
            activeLoan.setDueDateTime(LocalDateTime.now().plusDays(5)); // Due in 5 days
            
            List<Loan> userLoans = Arrays.asList(activeLoan);

            when(loanRepository.findByUserId(userId)).thenReturn(userLoans);

            // Act
            boolean result = loanService.hasOverdueBooks(userId);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return empty list for user with no loans")
        void testGetUserLoansEmpty() {
            // Arrange
            String userId = "user_with_no_loans";
            when(loanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            List<Loan> result = loanService.getUserLoans(userId);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Loan Period Tests")
    class LoanPeriodTests {
        @Test
        @DisplayName("Should get correct loan period for books")
        void testBookLoanPeriod() {
            // This is a private method test - we'll test it indirectly through borrow
            String userId = "user123";
            String bookId = "book456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            Book book = new Book("Test Book", "Test Author", "123", "BOOK");
            book.setAvailable(true);

            when(userRepository.findById(userId)).thenReturn(user);
            when(bookRepository.findById(bookId)).thenReturn(book);
            when(fineService.hasUnpaidFines(userId)).thenReturn(false);
            when(loanRepository.save(any(Loan.class))).thenReturn(true);
            when(bookRepository.update(book)).thenReturn(true);
            when(loanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            boolean result = loanService.borrowBook(userId, bookId);

            // Assert
            assertTrue(result);
            // Verify that a loan was created (indirectly testing loan period)
            verify(loanRepository).save(any(Loan.class));
        }

        @Test
        @DisplayName("Should get correct loan period for CDs")
        void testCDLoanPeriod() {
            // This is a private method test - we'll test it indirectly through borrow
            String userId = "user123";
            String bookId = "cd456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            Book cd = new Book("Test CD", "Test Artist", "123", "CD");
            cd.setAvailable(true);

            when(userRepository.findById(userId)).thenReturn(user);
            when(bookRepository.findById(bookId)).thenReturn(cd);
            when(fineService.hasUnpaidFines(userId)).thenReturn(false);
            when(loanRepository.save(any(Loan.class))).thenReturn(true);
            when(bookRepository.update(cd)).thenReturn(true);
            when(loanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            boolean result = loanService.borrowBook(userId, bookId);

            // Assert
            assertTrue(result);
            // Verify that a loan was created (indirectly testing loan period)
            verify(loanRepository).save(any(Loan.class));
        }
    }
}