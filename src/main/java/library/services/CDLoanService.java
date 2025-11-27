package library.services;

import library.models.CDLoan;
import library.models.CD;
import library.models.User;
import library.repositories.CDLoanRepository;
import library.repositories.CDRepository;
import library.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for handling CD loan operations
 * @author Library Team
 * @version 1.0
 */
public class CDLoanService {
    private CDLoanRepository cdLoanRepository;
    private CDRepository cdRepository;
    private UserRepository userRepository;
    private CDFineService cdFineService;

    public CDLoanService(CDLoanRepository cdLoanRepository, CDRepository cdRepository, 
                        UserRepository userRepository, CDFineService cdFineService) {
        this.cdLoanRepository = cdLoanRepository;
        this.cdRepository = cdRepository;
        this.userRepository = userRepository;
        this.cdFineService = cdFineService;
    }

    /**
     * Borrow a CD
     * @param userId user ID
     * @param cdId CD ID
     * @return true if borrow successful, false otherwise
     */
    public boolean borrowCD(String userId, String cdId) {
        // Check if user exists and is active
        User user = userRepository.findById(userId);
        if (user == null || !user.isActive()) {
            System.out.println("User not found or inactive!");
            return false;
        }

        // Check if CD exists and is available
        CD cd = cdRepository.findById(cdId);
        if (cd == null) {
            System.out.println("CD not found!");
            return false;
        }

        if (!cd.isAvailable()) {
            System.out.println("CD is not available for borrowing!");
            return false;
        }

        // Check if user has unpaid CD fines
        if (cdFineService.hasUnpaidCDFines(userId)) {
            System.out.println("User has unpaid CD fines. Cannot borrow CDs!");
            return false;
        }

        // Check if user has overdue CDs
        if (hasOverdueCDs(userId)) {
            System.out.println("User has overdue CDs. Cannot borrow new CDs!");
            return false;
        }

        // Create CD loan (7 days for CDs)
        CDLoan cdLoan = new CDLoan(userId, cdId);
        boolean loanSaved = cdLoanRepository.save(cdLoan);

        if (loanSaved) {
            // Update CD availability
            cd.setAvailable(false);
            cdRepository.update(cd);
            System.out.println("CD borrowed successfully! Due date: " + cdLoan.getDueDate());
            return true;
        }

        return false;
    }

    /**
     * Return a borrowed CD
     * @param cdLoanId CD loan ID
     * @return true if return successful, false otherwise
     */
    public boolean returnCD(String cdLoanId) {
        CDLoan cdLoan = cdLoanRepository.findById(cdLoanId);
        if (cdLoan == null || cdLoan.isReturned()) {
            System.out.println("CD loan not found or already returned!");
            return false;
        }

        CD cd = cdRepository.findById(cdLoan.getCdId());
        if (cd == null) {
            System.out.println("CD not found!");
            return false;
        }

        // Update CD loan
        cdLoan.setReturned(true);
        cdLoan.setReturnDateTime(LocalDateTime.now());

        // Update CD availability
        cd.setAvailable(true);
        cdRepository.update(cd);

        // Calculate and apply fine if overdue (20 NIS per day for CDs)
        if (cdLoan.isOverdue()) {
            double fineAmount = 20.0 * cdLoan.getOverdueDays(); // 20 NIS per day
            cdLoan.setFineAmount(fineAmount);
            
            // Create CD fine record
            cdFineService.createCDFine(cdLoan.getUserId(), cdLoanId, fineAmount);
            System.out.println("CD returned with overdue fine: $" + fineAmount);
        }

        boolean updated = cdLoanRepository.update(cdLoan);
        if (updated) {
            System.out.println("CD returned successfully!");
        }

        return updated;
    }

    /**
     * Check if user has overdue CDs
     * @param userId user ID
     * @return true if user has overdue CDs, false otherwise
     */
    public boolean hasOverdueCDs(String userId) {
        List<CDLoan> userCDLoans = cdLoanRepository.findByUserId(userId);
        return userCDLoans.stream().anyMatch(CDLoan::isOverdue);
    }

    /**
     * Get user's CD loans
     * @param userId user ID
     * @return list of user's CD loans
     */
    public List<CDLoan> getUserCDLoans(String userId) {
        return cdLoanRepository.findByUserId(userId);
    }

    /**
     * Get all overdue CD loans
     * @return list of overdue CD loans
     */
    public List<CDLoan> getOverdueCDLoans() {
        return cdLoanRepository.findOverdueCDLoans();
    }

    /**
     * Get CD loan by ID
     * @param cdLoanId CD loan ID
     * @return CD loan or null if not found
     */
    public CDLoan getCDLoanById(String cdLoanId) {
        return cdLoanRepository.findById(cdLoanId);
    }
}