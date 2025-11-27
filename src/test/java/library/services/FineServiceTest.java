package library.services;

import library.models.Fine;
import library.repositories.FineRepository;
import library.repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * Comprehensive unit tests for FineService
 * @author Library Team
 * @version 1.0
 */
@DisplayName("FineService Tests")
class FineServiceTest {
    private FineService fineService;
    private FineRepository fineRepository;
    private LoanRepository loanRepository;

    @BeforeEach
    void setUp() {
        fineRepository = mock(FineRepository.class);
        loanRepository = mock(LoanRepository.class);
        fineService = new FineService(fineRepository, loanRepository);
    }

    @Nested
    @DisplayName("Fine Creation Tests")
    class FineCreationTests {
        @Test
        @DisplayName("Should create fine successfully with valid data")
        void testCreateFineSuccess() {
            // Arrange
            String userId = "user123";
            String loanId = "loan456";
            double amount = 50.0;

            when(fineRepository.save(any(Fine.class))).thenReturn(true);

            // Act
            boolean result = fineService.createFine(userId, loanId, amount);

            // Assert
            assertTrue(result);
            verify(fineRepository).save(any(Fine.class));
        }

        @Test
        @DisplayName("Should fail to create fine with zero amount")
        void testCreateFineZeroAmount() {
            // Arrange
            String userId = "user123";
            String loanId = "loan456";
            double zeroAmount = 0.0;

            // Act
            boolean result = fineService.createFine(userId, loanId, zeroAmount);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).save(any(Fine.class));
        }

        @Test
        @DisplayName("Should fail to create fine with negative amount")
        void testCreateFineNegativeAmount() {
            // Arrange
            String userId = "user123";
            String loanId = "loan456";
            double negativeAmount = -10.0;

            // Act
            boolean result = fineService.createFine(userId, loanId, negativeAmount);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).save(any(Fine.class));
        }

        @Test
        @DisplayName("Should fail to create fine with null user ID")
        void testCreateFineNullUserId() {
            // Arrange
            String nullUserId = null;
            String loanId = "loan456";
            double amount = 50.0;

            // Act
            boolean result = fineService.createFine(nullUserId, loanId, amount);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).save(any(Fine.class));
        }

        @Test
        @DisplayName("Should fail to create fine with empty user ID")
        void testCreateFineEmptyUserId() {
            // Arrange
            String emptyUserId = "";
            String loanId = "loan456";
            double amount = 50.0;

            // Act
            boolean result = fineService.createFine(emptyUserId, loanId, amount);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).save(any(Fine.class));
        }

        @Test
        @DisplayName("Should fail to create fine with null loan ID")
        void testCreateFineNullLoanId() {
            // Arrange
            String userId = "user123";
            String nullLoanId = null;
            double amount = 50.0;

            // Act
            boolean result = fineService.createFine(userId, nullLoanId, amount);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).save(any(Fine.class));
        }
    }

    @Nested
    @DisplayName("Payment Tests")
    class PaymentTests {
        @Test
        @DisplayName("Should process partial payment successfully")
        void testProcessPartialPayment() {
            // Arrange
            String fineId = "fine123";
            Fine fine = new Fine("user123", "loan456", 100.0);
            fine.setId(fineId);

            when(fineRepository.findById(fineId)).thenReturn(fine);
            when(fineRepository.update(fine)).thenReturn(true);

            // Act
            boolean result = fineService.payFine(fineId, 50.0);

            // Assert
            assertTrue(result);
            assertEquals(50.0, fine.getPaidAmount());
            assertEquals(50.0, fine.getRemainingAmount());
            assertFalse(fine.isPaid());
            verify(fineRepository).update(fine);
        }

        @Test
        @DisplayName("Should fully pay fine and mark as paid")
        void testFullPayment() {
            // Arrange
            String fineId = "fine123";
            Fine fine = new Fine("user123", "loan456", 100.0);
            fine.setId(fineId);

            when(fineRepository.findById(fineId)).thenReturn(fine);
            when(fineRepository.update(fine)).thenReturn(true);

            // Act
            boolean result = fineService.payFine(fineId, 100.0);

            // Assert
            assertTrue(result);
            assertEquals(100.0, fine.getPaidAmount());
            assertEquals(0.0, fine.getRemainingAmount());
            assertTrue(fine.isPaid());
            verify(fineRepository).update(fine);
        }

        @Test
        @DisplayName("Should reject overpayment")
        void testOverpayment() {
            // Arrange
            String fineId = "fine123";
            Fine fine = new Fine("user123", "loan456", 100.0);
            fine.setId(fineId);

            when(fineRepository.findById(fineId)).thenReturn(fine);

            // Act
            boolean result = fineService.payFine(fineId, 150.0);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).update(fine);
        }

        @Test
        @DisplayName("Should reject payment for already paid fine")
        void testPaymentForPaidFine() {
            // Arrange
            String fineId = "fine123";
            Fine fine = new Fine("user123", "loan456", 100.0);
            fine.setId(fineId);
            fine.makePayment(100.0); // Mark as paid

            when(fineRepository.findById(fineId)).thenReturn(fine);

            // Act
            boolean result = fineService.payFine(fineId, 50.0);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).update(fine);
        }

        @Test
        @DisplayName("Should reject payment with zero amount")
        void testPaymentWithZeroAmount() {
            // Arrange
            String fineId = "fine123";
            Fine fine = new Fine("user123", "loan456", 100.0);
            fine.setId(fineId);

            when(fineRepository.findById(fineId)).thenReturn(fine);

            // Act
            boolean result = fineService.payFine(fineId, 0.0);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).update(fine);
        }

        @Test
        @DisplayName("Should reject payment with negative amount")
        void testPaymentWithNegativeAmount() {
            // Arrange
            String fineId = "fine123";
            Fine fine = new Fine("user123", "loan456", 100.0);
            fine.setId(fineId);

            when(fineRepository.findById(fineId)).thenReturn(fine);

            // Act
            boolean result = fineService.payFine(fineId, -10.0);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).update(fine);
        }

        @Test
        @DisplayName("Should reject payment for non-existent fine")
        void testPaymentForNonExistentFine() {
            // Arrange
            String nonExistentFineId = "non_existent_fine";
            when(fineRepository.findById(nonExistentFineId)).thenReturn(null);

            // Act
            boolean result = fineService.payFine(nonExistentFineId, 50.0);

            // Assert
            assertFalse(result);
            verify(fineRepository, never()).update(any(Fine.class));
        }
    }

    @Nested
    @DisplayName("Fine Query Tests")
    class FineQueryTests {
        @Test
        @DisplayName("Should check for unpaid fines - has unpaid")
        void testHasUnpaidFines() {
            // Arrange
            String userId = "user123";
            Fine unpaidFine = new Fine(userId, "loan456", 50.0);
            List<Fine> userFines = Arrays.asList(unpaidFine);

            when(fineRepository.findByUserId(userId)).thenReturn(userFines);

            // Act
            boolean result = fineService.hasUnpaidFines(userId);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should check for unpaid fines - all paid")
        void testHasNoUnpaidFines() {
            // Arrange
            String userId = "user123";
            Fine paidFine = new Fine(userId, "loan456", 50.0);
            paidFine.makePayment(50.0); // Mark as paid
            List<Fine> userFines = Arrays.asList(paidFine);

            when(fineRepository.findByUserId(userId)).thenReturn(userFines);

            // Act
            boolean result = fineService.hasUnpaidFines(userId);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should calculate total unpaid fines")
        void testGetTotalUnpaidFines() {
            // Arrange
            String userId = "user123";
            Fine fine1 = new Fine(userId, "loan1", 30.0);
            Fine fine2 = new Fine(userId, "loan2", 70.0);
            List<Fine> userFines = Arrays.asList(fine1, fine2);

            when(fineRepository.findByUserId(userId)).thenReturn(userFines);

            // Act
            double total = fineService.getTotalUnpaidFines(userId);

            // Assert
            assertEquals(100.0, total);
        }

        @Test
        @DisplayName("Should get user fines")
        void testGetUserFines() {
            // Arrange
            String userId = "user123";
            Fine fine1 = new Fine(userId, "loan1", 30.0);
            Fine fine2 = new Fine(userId, "loan2", 70.0);
            List<Fine> expectedFines = Arrays.asList(fine1, fine2);

            when(fineRepository.findByUserId(userId)).thenReturn(expectedFines);

            // Act
            List<Fine> result = fineService.getUserFines(userId);

            // Assert
            assertEquals(2, result.size());
            verify(fineRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should get all unpaid fines")
        void testGetAllUnpaidFines() {
            // Arrange
            Fine unpaidFine1 = new Fine("user1", "loan1", 30.0);
            Fine unpaidFine2 = new Fine("user2", "loan2", 70.0);
            List<Fine> unpaidFines = Arrays.asList(unpaidFine1, unpaidFine2);

            when(fineRepository.findUnpaidFines()).thenReturn(unpaidFines);

            // Act
            List<Fine> result = fineService.getAllUnpaidFines();

            // Assert
            assertEquals(2, result.size());
            verify(fineRepository).findUnpaidFines();
        }

        @Test
        @DisplayName("Should return zero for user with no fines")
        void testGetTotalUnpaidFinesForUserWithNoFines() {
            // Arrange
            String userId = "user_with_no_fines";
            when(fineRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            double total = fineService.getTotalUnpaidFines(userId);

            // Assert
            assertEquals(0.0, total);
        }
    }
}