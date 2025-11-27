package library.services;



import library.models.CDFine;
import library.repositories.CDFineRepository;
import library.repositories.CDLoanRepository;
import java.util.List;

/**
 * Service for handling CD fine operations
 * @author Library Team
 * @version 1.0
 */
public class CDFineService {
    private CDFineRepository cdFineRepository;
    private CDLoanRepository cdLoanRepository;

    public CDFineService(CDFineRepository cdFineRepository, CDLoanRepository cdLoanRepository) {
        this.cdFineRepository = cdFineRepository;
        this.cdLoanRepository = cdLoanRepository;
    }

    /**
     * Create a new CD fine
     * @param userId user ID
     * @param cdLoanId CD loan ID
     * @param amount fine amount
     * @return true if fine created successfully, false otherwise
     */
    public boolean createCDFine(String userId, String cdLoanId, double amount) {
        // Validate input parameters
        if (userId == null || userId.trim().isEmpty() || 
            cdLoanId == null || cdLoanId.trim().isEmpty() || 
            amount <= 0) {
            System.out.println("Invalid CD fine parameters!");
            return false;
        }

        CDFine cdFine = new CDFine(userId, cdLoanId, amount);
        return cdFineRepository.save(cdFine);
    }

    /**
     * Pay CD fine
     * @param cdFineId CD fine ID
     * @param amount payment amount
     * @return true if payment successful, false otherwise
     */
    public boolean payCDFine(String cdFineId, double amount) {
        CDFine cdFine = cdFineRepository.findById(cdFineId);
        if (cdFine == null || cdFine.isPaid()) {
            System.out.println("CD fine not found or already paid!");
            return false;
        }

        if (amount <= 0 || amount > cdFine.getRemainingAmount()) {
            System.out.println("Invalid payment amount!");
            return false;
        }

        boolean paymentSuccess = cdFine.makePayment(amount);
        if (paymentSuccess) {
            boolean updated = cdFineRepository.update(cdFine);
            if (updated) {
                System.out.println("CD fine payment successful! Remaining balance: $" + cdFine.getRemainingAmount());
                if (cdFine.isPaid()) {
                    System.out.println("CD fine fully paid!");
                }
            }
            return updated;
        }

        return false;
    }

    /**
     * Check if user has unpaid CD fines
     * @param userId user ID
     * @return true if user has unpaid CD fines, false otherwise
     */
    public boolean hasUnpaidCDFines(String userId) {
        List<CDFine> userCDFines = cdFineRepository.findByUserId(userId);
        return userCDFines.stream().anyMatch(fine -> !fine.isPaid());
    }

    /**
     * Get user's total unpaid CD fines
     * @param userId user ID
     * @return total unpaid CD fine amount
     */
    public double getTotalUnpaidCDFines(String userId) {
        List<CDFine> userCDFines = cdFineRepository.findByUserId(userId);
        return userCDFines.stream()
                .filter(fine -> !fine.isPaid())
                .mapToDouble(CDFine::getRemainingAmount)
                .sum();
    }

    /**
     * Get user's CD fines
     * @param userId user ID
     * @return list of user's CD fines
     */
    public List<CDFine> getUserCDFines(String userId) {
        return cdFineRepository.findByUserId(userId);
    }

    /**
     * Get all unpaid CD fines
     * @return list of all unpaid CD fines
     */
    public List<CDFine> getAllUnpaidCDFines() {
        return cdFineRepository.findUnpaidCDFines();
    }

    /**
     * Get CD fine by ID
     * @param cdFineId CD fine ID
     * @return CD fine or null if not found
     */
    public CDFine getCDFineById(String cdFineId) {
        return cdFineRepository.findById(cdFineId);
    }
}