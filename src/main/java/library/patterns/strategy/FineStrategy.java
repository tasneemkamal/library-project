package library.patterns.strategy;



/**
 * Strategy interface for calculating fines
 * @author Library Team
 * @version 1.0
 */
public interface FineStrategy {
    /**
     * Calculate fine based on overdue days
     * @param overdueDays number of days overdue
     * @return calculated fine amount
     */
    double calculateFine(int overdueDays);
    
    /**
     * Get the fine rate per day
     * @return daily fine rate
     */
    double getDailyRate();
}