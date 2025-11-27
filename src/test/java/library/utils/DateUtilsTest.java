package library.utils;



import org.junit.jupiter.api.Test;

import library.models.Loan;

import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DateUtils
 * @author Library Team
 * @version 1.0
 */
@DisplayName("DateUtils Tests")
class DateUtilsTest {
	

    @Test
    @DisplayName("Should convert LocalDateTime to string and back")
    void testDateTimeConversion() {
        // Arrange
        LocalDateTime original = LocalDateTime.of(2023, 12, 25, 10, 30, 45);

        // Act
        String stringValue = DateUtils.toString(original);
        LocalDateTime convertedBack = DateUtils.fromString(stringValue);

        // Assert
        assertNotNull(stringValue);
        assertEquals(original, convertedBack);
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullHandling() {
        assertNull(DateUtils.toString(null));
        assertNull(DateUtils.fromString(null));
    }

    @Test
    @DisplayName("Should get current date time")
    void testNow() {
        // Act
        LocalDateTime now = DateUtils.now();

        // Assert
        assertNotNull(now);
        // Should be close to the actual current time (within a few seconds)
        assertTrue(LocalDateTime.now().minusSeconds(5).isBefore(now));
    }
    
    /**
     * Create a loan with overdue status for testing
     * @param userId user ID
     * @param bookId book ID
     * @param overdueDays how many days overdue
     * @return overdue loan
     */
    public static Loan createOverdueLoan(String userId, String bookId, int overdueDays) {
        Loan loan = new Loan(userId, bookId, 28);
        loan.setDueDateTime(LocalDateTime.now().minusDays(overdueDays));
        return loan;
    }
    
    /**
     * Create a loan with future due date for testing
     * @param userId user ID
     * @param bookId book ID  
     * @param daysUntilDue days until due date
     * @return future due loan
     */
    public static Loan createFutureDueLoan(String userId, String bookId, int daysUntilDue) {
        Loan loan = new Loan(userId, bookId, 28);
        loan.setDueDateTime(LocalDateTime.now().plusDays(daysUntilDue));
        return loan;
    }
}