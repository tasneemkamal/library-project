package library.patterns.strategy;



/**
 * Fine calculation strategy for CDs
 * @author Library Team
 * @version 1.0
 */
public class CDFineStrategy implements FineStrategy {
    private static final double DAILY_RATE = 20.0;

    @Override
    public double calculateFine(int overdueDays) {
        return overdueDays * DAILY_RATE;
    }

    @Override
    public double getDailyRate() {
        return DAILY_RATE;
    }
}