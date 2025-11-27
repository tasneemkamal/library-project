package library.controllers;



import library.services.LoanService;
import library.services.FineService;
import library.repositories.UserRepository;
import library.repositories.BookRepository;
import library.models.Loan;
import library.models.Fine;
import java.util.List;

/**
 * Controller for loan operations
 * @author Library Team
 * @version 1.0
 */
public class LoanController {
    private LoanService loanService;
    private FineService fineService;
    private UserRepository userRepository;
    private BookRepository bookRepository;

    public LoanController(LoanService loanService, FineService fineService, 
                         UserRepository userRepository, BookRepository bookRepository) {
        this.loanService = loanService;
        this.fineService = fineService;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * View user's loans
     * @param userId user ID
     */
    public void viewUserLoans(String userId) {
        List<Loan> loans = loanService.getUserLoans(userId);
        if (loans.isEmpty()) {
            System.out.println("No loans found.");
            return;
        }

        System.out.println("\n=== My Loans ===");
        System.out.printf("%-15s %-15s %-15s %-15s %-10s%n", 
            "Loan ID", "Book ID", "Borrow Date", "Due Date", "Status");
        System.out.println("-------------------------------------------------------------------");
        
        for (Loan loan : loans) {
            String status = loan.isReturned() ? "Returned" : 
                           loan.isOverdue() ? "Overdue" : "Active";
            System.out.printf("%-15s %-15s %-15s %-15s %-10s%n",
                loan.getId().substring(0, 8) + "...",
                loan.getBookId().substring(0, 8) + "...",
                loan.getBorrowDate().substring(0, 10),
                loan.getDueDate().substring(0, 10),
                status);
        }
    }

    /**
     * View user's fines
     * @param userId user ID
     */
    public void viewUserFines(String userId) {
        List<Fine> fines = fineService.getUserFines(userId);
        if (fines.isEmpty()) {
            System.out.println("No fines found.");
            return;
        }

        System.out.println("\n=== My Fines ===");
        System.out.printf("%-15s %-10s %-10s %-10s %-10s%n", 
            "Fine ID", "Amount", "Paid", "Remaining", "Status");
        System.out.println("-------------------------------------------------------");
        
        for (Fine fine : fines) {
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
     * Borrow a book
     * @param userId user ID
     * @param bookId book ID
     */
    public void borrowBook(String userId, String bookId) {
        boolean success = loanService.borrowBook(userId, bookId);
        if (success) {
            System.out.println("Book borrowed successfully!");
        } else {
            System.out.println("Failed to borrow book. Please check if:");
            System.out.println("- The book is available");
            System.out.println("- You have no unpaid fines");
            System.out.println("- You have no overdue books");
        }
    }

    /**
     * Return a book
     * @param loanId loan ID
     */
    public void returnBook(String loanId) {
        boolean success = loanService.returnBook(loanId);
        if (success) {
            System.out.println("Book returned successfully!");
        } else {
            System.out.println("Failed to return book. Please check the loan ID.");
        }
    }

    /**
     * Pay a fine
     * @param fineId fine ID
     * @param amount payment amount
     */
    public void payFine(String fineId, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid payment amount!");
            return;
        }

        boolean success = fineService.payFine(fineId, amount);
        if (success) {
            System.out.println("Payment processed successfully!");
        } else {
            System.out.println("Failed to process payment. Please check the fine ID and amount.");
        }
    }
}