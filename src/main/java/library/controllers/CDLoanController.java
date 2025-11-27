package library.controllers;



import library.services.CDLoanService;
import library.services.CDFineService;
import library.repositories.UserRepository;
import library.repositories.CDRepository;
import library.models.CDLoan;
import library.models.CDFine;
import library.models.CD;
import java.util.List;

/**
 * Controller for CD loan operations
 * @author Library Team
 * @version 1.0
 */
public class CDLoanController {
    private CDLoanService cdLoanService;
    private CDFineService cdFineService;
    private UserRepository userRepository;
    private CDRepository cdRepository;

    public CDLoanController(CDLoanService cdLoanService, CDFineService cdFineService, 
                          UserRepository userRepository, CDRepository cdRepository) {
        this.cdLoanService = cdLoanService;
        this.cdFineService = cdFineService;
        this.userRepository = userRepository;
        this.cdRepository = cdRepository;
    }

    /**
     * View user's CD loans
     * @param userId user ID
     */
    public void viewUserCDLoans(String userId) {
        List<CDLoan> loans = cdLoanService.getUserCDLoans(userId);
        if (loans.isEmpty()) {
            System.out.println("No CD loans found.");
            return;
        }

        System.out.println("\n=== My CD Loans ===");
        System.out.printf("%-15s %-20s %-15s %-12s %-15s %-10s%n", 
            "Loan ID", "CD Title", "Artist", "Due Date", "Days Left", "Status");
        System.out.println("-------------------------------------------------------------------------------");
        
        for (CDLoan loan : loans) {
            CD cd = cdRepository.findById(loan.getCdId());
            String cdTitle = cd != null ? cd.getTitle() : "Unknown CD";
            String artist = cd != null ? cd.getArtist() : "Unknown";
            
            String status = loan.isReturned() ? "Returned" : 
                           loan.isOverdue() ? "Overdue" : "Active";
            
            long daysLeft = java.time.Duration.between(
                java.time.LocalDateTime.now(), loan.getDueDateTime()).toDays();
            
            System.out.printf("%-15s %-20s %-15s %-12s %-15s %-10s%n",
                loan.getId().substring(0, 8) + "...",
                shortenString(cdTitle, 18),
                shortenString(artist, 13),
                loan.getDueDate().substring(0, 10),
                daysLeft + " days",
                status);
        }
    }

    /**
     * View user's CD fines
     * @param userId user ID
     */
    public void viewUserCDFines(String userId) {
        List<CDFine> fines = cdFineService.getUserCDFines(userId);
        if (fines.isEmpty()) {
            System.out.println("No CD fines found.");
            return;
        }

        System.out.println("\n=== My CD Fines ===");
        System.out.printf("%-15s %-10s %-10s %-10s %-10s%n", 
            "Fine ID", "Amount", "Paid", "Remaining", "Status");
        System.out.println("-------------------------------------------------------");
        
        for (CDFine fine : fines) {
            String status = fine.isPaid() ? "Paid" : "Unpaid";
            System.out.printf("%-15s $%-9.2f $%-9.2f $%-9.2f %-10s%n",
                fine.getId().substring(0, 8) + "...",
                fine.getAmount(),
                fine.getPaidAmount(),
                fine.getRemainingAmount(),
                status);
        }
    }

    /**
     * Borrow a CD
     * @param userId user ID
     * @param cdId CD ID
     */
    public void borrowCD(String userId, String cdId) {
        boolean success = cdLoanService.borrowCD(userId, cdId);
        if (success) {
            System.out.println("CD borrowed successfully for 7 days!");
        } else {
            System.out.println("Failed to borrow CD. Please check if:");
            System.out.println("- The CD is available");
            System.out.println("- You have no unpaid fines");
            System.out.println("- You have no overdue CDs");
        }
    }

    /**
     * Return a CD
     * @param cdLoanId CD loan ID
     */
    public void returnCD(String cdLoanId) {
        boolean success = cdLoanService.returnCD(cdLoanId);
        if (success) {
            System.out.println("CD returned successfully!");
        } else {
            System.out.println("Failed to return CD. Please check the loan ID.");
        }
    }

    /**
     * Pay a CD fine
     * @param cdFineId CD fine ID
     * @param amount payment amount
     */
    public void payCDFine(String cdFineId, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid payment amount!");
            return;
        }

        boolean success = cdFineService.payCDFine(cdFineId, amount);
        if (success) {
            System.out.println("CD fine payment processed successfully!");
        } else {
            System.out.println("Failed to process CD fine payment. Please check the fine ID and amount.");
        }
    }

    /**
     * Shorten string for display
     * @param str string to shorten
     * @param maxLength maximum length
     * @return shortened string
     */
    private String shortenString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str != null ? str : "";
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}