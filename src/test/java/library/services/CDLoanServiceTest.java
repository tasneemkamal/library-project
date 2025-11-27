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
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * Comprehensive unit tests for CDLoanService
 * @author Library Team
 * @version 1.0
 */
@DisplayName("CDLoanService Tests")
class CDLoanServiceTest {
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

    @Nested
    @DisplayName("Borrow CD Tests")
    class BorrowCDTests {
        @Test
        @DisplayName("Should borrow CD successfully with valid data")
        void testBorrowCDSuccess() {
            // Arrange
            String userId = "user123";
            String cdId = "cd456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            user.setActive(true);
            CD cd = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            cd.setAvailable(true);

            when(userRepository.findById(userId)).thenReturn(user);
            when(cdRepository.findById(cdId)).thenReturn(cd);
            when(cdFineService.hasUnpaidCDFines(userId)).thenReturn(false);
            when(cdLoanRepository.save(any(CDLoan.class))).thenReturn(true);
            when(cdRepository.update(cd)).thenReturn(true);
            when(cdLoanRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            boolean result = cdLoanService.borrowCD(userId, cdId);

            // Assert
            assertTrue(result);
            verify(cdLoanRepository).save(any(CDLoan.class));
            verify(cdRepository).update(cd);
        }

        @Test
        @DisplayName("Should fail to borrow unavailable CD")
        void testBorrowUnavailableCD() {
            // Arrange
            String userId = "user123";
            String cdId = "cd456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            CD cd = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            cd.setAvailable(false);

            when(userRepository.findById(userId)).thenReturn(user);
            when(cdRepository.findById(cdId)).thenReturn(cd);

            // Act
            boolean result = cdLoanService.borrowCD(userId, cdId);

            // Assert
            assertFalse(result);
            verify(cdLoanRepository, never()).save(any(CDLoan.class));
        }

        @Test
        @DisplayName("Should fail to borrow with unpaid CD fines")
        void testBorrowWithUnpaidCDFines() {
            // Arrange
            String userId = "user123";
            String cdId = "cd456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            CD cd = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            cd.setAvailable(true);

            when(userRepository.findById(userId)).thenReturn(user);
            when(cdRepository.findById(cdId)).thenReturn(cd);
            when(cdFineService.hasUnpaidCDFines(userId)).thenReturn(true);

            // Act
            boolean result = cdLoanService.borrowCD(userId, cdId);

            // Assert
            assertFalse(result);
            verify(cdLoanRepository, never()).save(any(CDLoan.class));
        }

        @Test
        @DisplayName("Should fail to borrow with overdue CDs")
        void testBorrowWithOverdueCDs() {
            // Arrange
            String userId = "user123";
            String cdId = "cd456";
            
            User user = new User("John Doe", "john@test.com", "hash", "USER");
            CD cd = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            cd.setAvailable(true);
            
            // Create an overdue CD loan
            CDLoan overdueCDLoan = new CDLoan(userId, "oldCD");
            overdueCDLoan.setDueDateTime(LocalDateTime.now().minusDays(1)); // Overdue

            when(userRepository.findById(userId)).thenReturn(user);
            when(cdRepository.findById(cdId)).thenReturn(cd);
            when(cdFineService.hasUnpaidCDFines(userId)).thenReturn(false);
            when(cdLoanRepository.findByUserId(userId)).thenReturn(Arrays.asList(overdueCDLoan));

            // Act
            boolean result = cdLoanService.borrowCD(userId, cdId);

            // Assert
            assertFalse(result);
            verify(cdLoanRepository, never()).save(any(CDLoan.class));
        }
    }

    @Nested
    @DisplayName("Return CD Tests")
    class ReturnCDTests {
        @Test
        @DisplayName("Should return CD successfully")
        void testReturnCDSuccess() {
            // Arrange
            String cdLoanId = "cdloan123";
            String userId = "user123";
            String cdId = "cd456";
            
            CDLoan cdLoan = new CDLoan(userId, cdId);
            cdLoan.setId(cdLoanId);
            CD cd = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);

            when(cdLoanRepository.findById(cdLoanId)).thenReturn(cdLoan);
            when(cdRepository.findById(cdId)).thenReturn(cd);
            when(cdLoanRepository.update(cdLoan)).thenReturn(true);
            when(cdRepository.update(cd)).thenReturn(true);

            // Act
            boolean result = cdLoanService.returnCD(cdLoanId);

            // Assert
            assertTrue(result);
            assertTrue(cdLoan.isReturned());
            assertTrue(cd.isAvailable());
            verify(cdFineService, never()).createCDFine(anyString(), anyString(), anyDouble());
        }

        @Test
        @DisplayName("Should apply fine for overdue CD")
        void testReturnOverdueCDWithFine() {
            // Arrange
            String cdLoanId = "cdloan123";
            String userId = "user123";
            String cdId = "cd456";
            
            // Create a CD loan that is definitely overdue
            CDLoan cdLoan = new CDLoan(userId, cdId);
            cdLoan.setId(cdLoanId);
            // Set borrow date to 10 days ago and due date to 8 days ago to ensure it's overdue
            cdLoan.setBorrowDateTime(LocalDateTime.now().minusDays(10));
            cdLoan.setDueDateTime(LocalDateTime.now().minusDays(8)); // 8 days overdue
            
            CD cd = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);

            when(cdLoanRepository.findById(cdLoanId)).thenReturn(cdLoan);
            when(cdRepository.findById(cdId)).thenReturn(cd);
            when(cdLoanRepository.update(cdLoan)).thenReturn(true);
            when(cdRepository.update(cd)).thenReturn(true);
            when(cdFineService.createCDFine(userId, cdLoanId, 160.0)).thenReturn(true); // 8 days * 20 NIS

            // Act
            boolean result = cdLoanService.returnCD(cdLoanId);

            // Assert
            assertTrue(result);
            assertTrue(cdLoan.isOverdue(), "CD loan should be overdue");
            assertTrue(cdLoan.getOverdueDays() > 0, "CD loan should have overdue days");
            verify(cdFineService).createCDFine(userId, cdLoanId, 160.0);
        }

        @Test
        @DisplayName("Should fail to return non-existent CD loan")
        void testReturnNonExistentCDLoan() {
            // Arrange
            String nonExistentLoanId = "non_existent";
            when(cdLoanRepository.findById(nonExistentLoanId)).thenReturn(null);

            // Act
            boolean result = cdLoanService.returnCD(nonExistentLoanId);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should fail to return already returned CD")
        void testReturnAlreadyReturnedCD() {
            // Arrange
            String cdLoanId = "cdloan123";
            CDLoan cdLoan = new CDLoan("user123", "cd456");
            cdLoan.setId(cdLoanId);
            cdLoan.setReturned(true);

            when(cdLoanRepository.findById(cdLoanId)).thenReturn(cdLoan);

            // Act
            boolean result = cdLoanService.returnCD(cdLoanId);

            // Assert
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("CD Loan Query Tests")
    class CDLoanQueryTests {
        @Test
        @DisplayName("Should get user CD loans")
        void testGetUserCDLoans() {
            // Arrange
            String userId = "user123";
            CDLoan cdLoan1 = new CDLoan(userId, "cd1");
            CDLoan cdLoan2 = new CDLoan(userId, "cd2");
            List<CDLoan> expectedLoans = Arrays.asList(cdLoan1, cdLoan2);

            when(cdLoanRepository.findByUserId(userId)).thenReturn(expectedLoans);

            // Act
            List<CDLoan> result = cdLoanService.getUserCDLoans(userId);

            // Assert
            assertEquals(2, result.size());
            verify(cdLoanRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should get overdue CD loans")
        void testGetOverdueCDLoans() {
            // Arrange
            CDLoan overdueLoan1 = new CDLoan("user1", "cd1");
            overdueLoan1.setDueDateTime(LocalDateTime.now().minusDays(1));
            CDLoan overdueLoan2 = new CDLoan("user2", "cd2");
            overdueLoan2.setDueDateTime(LocalDateTime.now().minusDays(2));
            List<CDLoan> overdueLoans = Arrays.asList(overdueLoan1, overdueLoan2);

            when(cdLoanRepository.findOverdueCDLoans()).thenReturn(overdueLoans);

            // Act
            List<CDLoan> result = cdLoanService.getOverdueCDLoans();

            // Assert
            assertEquals(2, result.size());
            verify(cdLoanRepository).findOverdueCDLoans();
        }

        @Test
        @DisplayName("Should check for overdue CDs - has overdue")
        void testHasOverdueCDs() {
            // Arrange
            String userId = "user123";
            CDLoan overdueLoan = new CDLoan(userId, "cd1");
            overdueLoan.setDueDateTime(LocalDateTime.now().minusDays(1)); // Overdue
            
            when(cdLoanRepository.findByUserId(userId)).thenReturn(Arrays.asList(overdueLoan));

            // Act
            boolean result = cdLoanService.hasOverdueCDs(userId);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should check for overdue CDs - no overdue")
        void testHasNoOverdueCDs() {
            // Arrange
            String userId = "user123";
            CDLoan currentLoan = new CDLoan(userId, "cd1");
            currentLoan.setDueDateTime(LocalDateTime.now().plusDays(5)); // Not overdue
            
            when(cdLoanRepository.findByUserId(userId)).thenReturn(Arrays.asList(currentLoan));

            // Act
            boolean result = cdLoanService.hasOverdueCDs(userId);

            // Assert
            assertFalse(result);
        }
    }
}