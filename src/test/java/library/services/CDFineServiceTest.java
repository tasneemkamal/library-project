package library.services;


import library.models.CDFine;
import library.repositories.CDFineRepository;
import library.repositories.CDLoanRepository;
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
 * Comprehensive unit tests for CDFineService
 * @author Library Team
 * @version 1.0
 */
@DisplayName("CDFineService Tests")
class CDFineServiceTest {
    private CDFineService cdFineService;
    private CDFineRepository cdFineRepository;
    private CDLoanRepository cdLoanRepository;

    @BeforeEach
    void setUp() {
        cdFineRepository = mock(CDFineRepository.class);
        cdLoanRepository = mock(CDLoanRepository.class);
        cdFineService = new CDFineService(cdFineRepository, cdLoanRepository);
    }

    @Nested
    @DisplayName("CD Fine Creation Tests")
    class CDFineCreationTests {
        @Test
        @DisplayName("Should create CD fine successfully with valid data")
        void testCreateCDFineSuccess() {
            // Arrange
            String userId = "user123";
            String cdLoanId = "cdloan456";
            double amount = 60.0;

            when(cdFineRepository.save(any(CDFine.class))).thenReturn(true);

            // Act
            boolean result = cdFineService.createCDFine(userId, cdLoanId, amount);

            // Assert
            assertTrue(result);
            verify(cdFineRepository).save(any(CDFine.class));
        }

        @Test
        @DisplayName("Should fail to create CD fine with zero amount")
        void testCreateCDFineZeroAmount() {
            // Arrange
            String userId = "user123";
            String cdLoanId = "cdloan456";
            double zeroAmount = 0.0;

            // Act
            boolean result = cdFineService.createCDFine(userId, cdLoanId, zeroAmount);

            // Assert
            assertFalse(result);
            verify(cdFineRepository, never()).save(any(CDFine.class));
        }

        @Test
        @DisplayName("Should fail to create CD fine with negative amount")
        void testCreateCDFineNegativeAmount() {
            // Arrange
            String userId = "user123";
            String cdLoanId = "cdloan456";
            double negativeAmount = -10.0;

            // Act
            boolean result = cdFineService.createCDFine(userId, cdLoanId, negativeAmount);

            // Assert
            assertFalse(result);
            verify(cdFineRepository, never()).save(any(CDFine.class));
        }

        @Test
        @DisplayName("Should fail to create CD fine with empty user ID")
        void testCreateCDFineEmptyUserId() {
            // Arrange
            String emptyUserId = "";
            String cdLoanId = "cdloan456";
            double amount = 60.0;

            // Act
            boolean result = cdFineService.createCDFine(emptyUserId, cdLoanId, amount);

            // Assert
            assertFalse(result);
            verify(cdFineRepository, never()).save(any(CDFine.class));
        }
    }

    @Nested
    @DisplayName("CD Fine Payment Tests")
    class CDFinePaymentTests {
        @Test
        @DisplayName("Should process partial payment successfully")
        void testProcessPartialPayment() {
            // Arrange
            String cdFineId = "cdfine123";
            CDFine cdFine = new CDFine("user123", "cdloan456", 100.0);
            cdFine.setId(cdFineId);

            when(cdFineRepository.findById(cdFineId)).thenReturn(cdFine);
            when(cdFineRepository.update(cdFine)).thenReturn(true);

            // Act
            boolean result = cdFineService.payCDFine(cdFineId, 50.0);

            // Assert
            assertTrue(result);
            assertEquals(50.0, cdFine.getPaidAmount());
            assertEquals(50.0, cdFine.getRemainingAmount());
            assertFalse(cdFine.isPaid());
            verify(cdFineRepository).update(cdFine);
        }

        @Test
        @DisplayName("Should fully pay CD fine and mark as paid")
        void testFullPayment() {
            // Arrange
            String cdFineId = "cdfine123";
            CDFine cdFine = new CDFine("user123", "cdloan456", 100.0);
            cdFine.setId(cdFineId);

            when(cdFineRepository.findById(cdFineId)).thenReturn(cdFine);
            when(cdFineRepository.update(cdFine)).thenReturn(true);

            // Act
            boolean result = cdFineService.payCDFine(cdFineId, 100.0);

            // Assert
            assertTrue(result);
            assertEquals(100.0, cdFine.getPaidAmount());
            assertEquals(0.0, cdFine.getRemainingAmount());
            assertTrue(cdFine.isPaid());
            verify(cdFineRepository).update(cdFine);
        }

        @Test
        @DisplayName("Should reject overpayment")
        void testOverpayment() {
            // Arrange
            String cdFineId = "cdfine123";
            CDFine cdFine = new CDFine("user123", "cdloan456", 100.0);
            cdFine.setId(cdFineId);

            when(cdFineRepository.findById(cdFineId)).thenReturn(cdFine);

            // Act
            boolean result = cdFineService.payCDFine(cdFineId, 150.0);

            // Assert
            assertFalse(result);
            verify(cdFineRepository, never()).update(cdFine);
        }

        @Test
        @DisplayName("Should reject payment for already paid CD fine")
        void testPaymentForPaidCDFine() {
            // Arrange
            String cdFineId = "cdfine123";
            CDFine cdFine = new CDFine("user123", "cdloan456", 100.0);
            cdFine.setId(cdFineId);
            cdFine.makePayment(100.0); // Mark as paid

            when(cdFineRepository.findById(cdFineId)).thenReturn(cdFine);

            // Act
            boolean result = cdFineService.payCDFine(cdFineId, 50.0);

            // Assert
            assertFalse(result);
            verify(cdFineRepository, never()).update(cdFine);
        }
    }

    @Nested
    @DisplayName("CD Fine Query Tests")
    class CDFineQueryTests {
        @Test
        @DisplayName("Should check for unpaid CD fines - has unpaid")
        void testHasUnpaidCDFines() {
            // Arrange
            String userId = "user123";
            CDFine unpaidFine = new CDFine(userId, "cdloan456", 60.0);
            List<CDFine> userFines = Arrays.asList(unpaidFine);

            when(cdFineRepository.findByUserId(userId)).thenReturn(userFines);

            // Act
            boolean result = cdFineService.hasUnpaidCDFines(userId);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should check for unpaid CD fines - all paid")
        void testHasNoUnpaidCDFines() {
            // Arrange
            String userId = "user123";
            CDFine paidFine = new CDFine(userId, "cdloan456", 60.0);
            paidFine.makePayment(60.0); // Mark as paid
            List<CDFine> userFines = Arrays.asList(paidFine);

            when(cdFineRepository.findByUserId(userId)).thenReturn(userFines);

            // Act
            boolean result = cdFineService.hasUnpaidCDFines(userId);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should calculate total unpaid CD fines")
        void testGetTotalUnpaidCDFines() {
            // Arrange
            String userId = "user123";
            CDFine fine1 = new CDFine(userId, "cdloan1", 30.0);
            CDFine fine2 = new CDFine(userId, "cdloan2", 70.0);
            List<CDFine> userFines = Arrays.asList(fine1, fine2);

            when(cdFineRepository.findByUserId(userId)).thenReturn(userFines);

            // Act
            double total = cdFineService.getTotalUnpaidCDFines(userId);

            // Assert
            assertEquals(100.0, total);
        }

        @Test
        @DisplayName("Should get user CD fines")
        void testGetUserCDFines() {
            // Arrange
            String userId = "user123";
            CDFine fine1 = new CDFine(userId, "cdloan1", 30.0);
            CDFine fine2 = new CDFine(userId, "cdloan2", 70.0);
            List<CDFine> expectedFines = Arrays.asList(fine1, fine2);

            when(cdFineRepository.findByUserId(userId)).thenReturn(expectedFines);

            // Act
            List<CDFine> result = cdFineService.getUserCDFines(userId);

            // Assert
            assertEquals(2, result.size());
            verify(cdFineRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should return zero for user with no CD fines")
        void testGetTotalUnpaidCDFinesForUserWithNoFines() {
            // Arrange
            String userId = "user_with_no_fines";
            when(cdFineRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // Act
            double total = cdFineService.getTotalUnpaidCDFines(userId);

            // Assert
            assertEquals(0.0, total);
        }
    }
}