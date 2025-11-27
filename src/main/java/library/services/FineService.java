package library.services;

import library.models.Fine;
import library.repositories.FineRepository;
import library.repositories.LoanRepository;
import java.util.List;

/**
 * Service for handling fine operations
 * @author Library Team
 * @version 1.0
 */
public class FineService {
    private FineRepository fineRepository;
    private LoanRepository loanRepository;

    public FineService(FineRepository fineRepository, LoanRepository loanRepository) {
        this.fineRepository = fineRepository;
        this.loanRepository = loanRepository;
    }

    /**
     * Create a new fine
     * @param userId user ID
     * @param loanId loan ID
     * @param amount fine amount
     * @return true if fine created successfully, false otherwise
     */
    public boolean createFine(String userId, String loanId, double amount) {
        // Validate input parameters
        if (userId == null || userId.trim().isEmpty() || 
            loanId == null || loanId.trim().isEmpty() || 
            amount <= 0) {
            System.out.println("Invalid fine parameters!");
            return false;
        }

        Fine fine = new Fine(userId, loanId, amount);
        return fineRepository.save(fine);
    }

    /**
     * Pay fine
     * @param fineId fine ID
     * @param amount payment amount
     * @return true if payment successful, false otherwise
     */
    public boolean payFine(String fineId, double amount) {
        Fine fine = fineRepository.findById(fineId);
        if (fine == null || fine.isPaid()) {
            System.out.println("Fine not found or already paid!");
            return false;
        }

        if (amount <= 0 || amount > fine.getRemainingAmount()) {
            System.out.println("Invalid payment amount!");
            return false;
        }

        boolean paymentSuccess = fine.makePayment(amount);
        if (paymentSuccess) {
            boolean updated = fineRepository.update(fine);
            if (updated) {
                System.out.println("Payment successful! Remaining balance: $" + fine.getRemainingAmount());
                if (fine.isPaid()) {
                    System.out.println("Fine fully paid!");
                }
            }
            return updated;
        }

        return false;
    }

    /**
     * Check if user has unpaid fines
     * @param userId user ID
     * @return true if user has unpaid fines, false otherwise
     */
    public boolean hasUnpaidFines(String userId) {
        List<Fine> userFines = fineRepository.findByUserId(userId);
        return userFines.stream().anyMatch(fine -> !fine.isPaid());
    }

    /**
     * Get user's total unpaid fines
     * @param userId user ID
     * @return total unpaid fine amount
     */
    public double getTotalUnpaidFines(String userId) {
        List<Fine> userFines = fineRepository.findByUserId(userId);
        return userFines.stream()
                .filter(fine -> !fine.isPaid())
                .mapToDouble(Fine::getRemainingAmount)
                .sum();
    }

    /**
     * Get user's fines
     * @param userId user ID
     * @return list of user's fines
     */
    public List<Fine> getUserFines(String userId) {
        return fineRepository.findByUserId(userId);
    }

    /**
     * Get all unpaid fines
     * @return list of all unpaid fines
     */
    public List<Fine> getAllUnpaidFines() {
        return fineRepository.findUnpaidFines();
    }
}