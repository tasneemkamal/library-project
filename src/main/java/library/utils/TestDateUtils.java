package library.utils;

import java.time.LocalDateTime;

import library.models.CDLoan;

/**
 * Utility class for date operations in tests
 * @author Library Team  
 * @version 1.0
 */
public class TestDateUtils {
    
    /**
     * Create a CD loan with overdue status for testing
     * @param userId user ID
     * @param cdId CD ID
     * @param overdueDays how many days overdue
     * @return overdue CD loan
     */
    public static CDLoan createOverdueCDLoan(String userId, String cdId, int overdueDays) {
        CDLoan loan = new CDLoan(userId, cdId);
        loan.setDueDateTime(LocalDateTime.now().minusDays(overdueDays));
        return loan;
    }
    
    /**
     * Create a CD loan with future due date for testing
     * @param userId user ID
     * @param cdId CD ID  
     * @param daysUntilDue days until due date
     * @return future due CD loan
     */
    public static CDLoan createFutureDueCDLoan(String userId, String cdId, int daysUntilDue) {
        CDLoan loan = new CDLoan(userId, cdId);
        loan.setDueDateTime(LocalDateTime.now().plusDays(daysUntilDue));
        return loan;
    }
}