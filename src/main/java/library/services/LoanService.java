package library.services;



import library.models.Loan;
import library.models.Book;
import library.models.User;
import library.repositories.LoanRepository;
import library.repositories.BookRepository;
import library.repositories.UserRepository;
import library.patterns.strategy.FineStrategy;
import library.patterns.strategy.BookFineStrategy;
import library.patterns.strategy.CDFineStrategy;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for handling loan operations
 * @author Library Team
 * @version 1.0
 */
public class LoanService {


	  private LoanRepository loanRepository;
	    private BookRepository bookRepository;
	    private UserRepository userRepository;
	    private FineService fineService;

	    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, 
	                      UserRepository userRepository, FineService fineService) {
	        this.loanRepository = loanRepository;
	        this.bookRepository = bookRepository;
	        this.userRepository = userRepository;
	        this.fineService = fineService;
	    }

	    /**
	     * Borrow a book
	     * @param userId user ID
	     * @param bookId book ID
	     * @return true if borrow successful, false otherwise
	     */
	    public boolean borrowBook(String userId, String bookId) {
	        // Check if user exists and is active
	        User user = userRepository.findById(userId);
	        if (user == null || !user.isActive()) {
	            System.out.println("User not found or inactive!");
	            return false;
	        }

	        // Check if book exists and is available
	        Book book = bookRepository.findById(bookId);
	        if (book == null) {
	            System.out.println("Book not found!");
	            return false;
	        }

	        if (!book.isAvailable()) {
	            System.out.println("Book is not available for borrowing!");
	            return false;
	        }

	        // Check if user has unpaid fines
	        if (fineService.hasUnpaidFines(userId)) {
	            System.out.println("User has unpaid fines. Cannot borrow books!");
	            return false;
	        }

	        // Check if user has overdue books
	        if (hasOverdueBooks(userId)) {
	            System.out.println("User has overdue books. Cannot borrow new books!");
	            return false;
	        }

	        // Determine loan period based on book type
	        int loanPeriod = getLoanPeriod(book.getType());

	        // Create loan
	        Loan loan = new Loan(userId, bookId, loanPeriod);
	        boolean loanSaved = loanRepository.save(loan);

	        if (loanSaved) {
	            // Update book availability
	            book.setAvailable(false);
	            bookRepository.update(book);
	            System.out.println("Book borrowed successfully! Due date: " + loan.getDueDate());
	            return true;
	        }

	        return false;
	    }

	    /**
	     * Return a borrowed book
	     * @param loanId loan ID
	     * @return true if return successful, false otherwise
	     */
	    public boolean returnBook(String loanId) {
	        Loan loan = loanRepository.findById(loanId);
	        if (loan == null || loan.isReturned()) {
	            System.out.println("Loan not found or already returned!");
	            return false;
	        }

	        Book book = bookRepository.findById(loan.getBookId());
	        if (book == null) {
	            System.out.println("Book not found!");
	            return false;
	        }

	        // Update loan
	        loan.setReturned(true);
	        loan.setReturnDateTime(LocalDateTime.now());

	        // Update book availability
	        book.setAvailable(true);
	        bookRepository.update(book);

	        // Calculate and apply fine if overdue
	        if (loan.isOverdue()) {
	            FineStrategy fineStrategy = getFineStrategy(book.getType());
	            double fineAmount = fineStrategy.calculateFine(loan.getOverdueDays());
	            loan.setFineAmount(fineAmount);
	            
	            // Create fine record
	            fineService.createFine(loan.getUserId(), loanId, fineAmount);
	            System.out.println("Book returned with overdue fine: $" + fineAmount);
	        }

	        boolean updated = loanRepository.update(loan);
	        if (updated) {
	            System.out.println("Book returned successfully!");
	        }

	        return updated;
	    }

	    /**
	     * Get loan period based on book type - package private for testing
	     * @param bookType type of book
	     * @return loan period in days
	     */
	    public int getLoanPeriod(String bookType) {
	        if (bookType == null) {
	            return 28; // Default to book period
	        }
	        
	        switch (bookType.toUpperCase()) {
	            case "CD":
	                return 7;
	            case "BOOK":
	            default:
	                return 28;
	        }
	    }

	    /**
	     * Get fine strategy based on book type - package private for testing
	     * @param bookType type of book
	     * @return appropriate fine strategy
	     */
	    public FineStrategy getFineStrategy(String bookType) {
	        if (bookType == null) {
	            return new BookFineStrategy(); // Default to book strategy
	        }
	        
	        switch (bookType.toUpperCase()) {
	            case "CD":
	                return new CDFineStrategy();
	            case "BOOK":
	            default:
	                return new BookFineStrategy();
	        }
	    }

	    /**
	     * Check if user has overdue books
	     * @param userId user ID
	     * @return true if user has overdue books, false otherwise
	     */
	    public boolean hasOverdueBooks(String userId) {
	        List<Loan> userLoans = loanRepository.findByUserId(userId);
	        return userLoans.stream().anyMatch(Loan::isOverdue);
	    }

	    /**
	     * Get user's active loans
	     * @param userId user ID
	     * @return list of active loans
	     */
	    public List<Loan> getUserLoans(String userId) {
	        return loanRepository.findByUserId(userId);
	    }

	    /**
	     * Get all overdue loans
	     * @return list of overdue loans
	     */
	    public List<Loan> getOverdueLoans() {
	        return loanRepository.findOverdueLoans();
	    }
	

   
    /**
     * Return a borrowed book
     * @param loanId loan ID
     * @return true if return successful, false otherwise
     */
 
 
        
    }