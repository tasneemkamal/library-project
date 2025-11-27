package library.patterns.strategy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Fine Strategy pattern implementations
 * @author Library Team
 * @version 1.0
 */
@DisplayName("Fine Strategy Tests")
class FineStrategyTest {

    @Test
    @DisplayName("Book fine strategy should calculate correct amount")
    void testBookFineStrategy() {
        // Arrange
        FineStrategy strategy = new BookFineStrategy();
        int overdueDays = 5;

        // Act
        double fine = strategy.calculateFine(overdueDays);

        // Assert
        assertEquals(50.0, fine); // 5 days * 10 NIS per day
        assertEquals(10.0, strategy.getDailyRate());
    }

    @Test
    @DisplayName("CD fine strategy should calculate correct amount")
    void testCDFineStrategy() {
        // Arrange
        FineStrategy strategy = new CDFineStrategy();
        int overdueDays = 5;

        // Act
        double fine = strategy.calculateFine(overdueDays);

        // Assert
        assertEquals(100.0, fine); // 5 days * 20 NIS per day
        assertEquals(20.0, strategy.getDailyRate());
    }

    @Test
    @DisplayName("Different strategies should have different rates")
    void testStrategyDifferences() {
        // Arrange
        FineStrategy bookStrategy = new BookFineStrategy();
        FineStrategy cdStrategy = new CDFineStrategy();

        // Assert
        assertNotEquals(bookStrategy.getDailyRate(), cdStrategy.getDailyRate());
        assertTrue(cdStrategy.getDailyRate() > bookStrategy.getDailyRate());
    }

    @Test
    @DisplayName("Zero overdue days should result in zero fine")
    void testZeroOverdueDays() {
        // Arrange
        FineStrategy bookStrategy = new BookFineStrategy();
        FineStrategy cdStrategy = new CDFineStrategy();

        // Act & Assert
        assertEquals(0.0, bookStrategy.calculateFine(0));
        assertEquals(0.0, cdStrategy.calculateFine(0));
    }

   

    @Test
    @DisplayName("Large number of overdue days should calculate correctly")
    void testLargeOverdueDays() {
        // Arrange
        FineStrategy bookStrategy = new BookFineStrategy();
        FineStrategy cdStrategy = new CDFineStrategy();
        int largeOverdueDays = 100;

        // Act & Assert
        assertEquals(1000.0, bookStrategy.calculateFine(largeOverdueDays)); // 100 * 10
        assertEquals(2000.0, cdStrategy.calculateFine(largeOverdueDays)); // 100 * 20
    }
}