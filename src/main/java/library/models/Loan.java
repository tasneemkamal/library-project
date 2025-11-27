package library.models;

import library.utils.DateUtils;
import java.time.LocalDateTime;

/**
 * Loan model representing a book borrowing transaction
 * @author Library Team
 * @version 1.0
 */
public class Loan {
    private String id;
    private String userId;
    private String bookId;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private boolean isReturned;
    private double fineAmount;

    public Loan() {}

    public Loan(String userId, String bookId, int loanPeriodDays) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = DateUtils.toString(LocalDateTime.now());
        this.dueDate = DateUtils.toString(LocalDateTime.now().plusDays(loanPeriodDays));
        this.isReturned = false;
        this.fineAmount = 0.0;
    }

    // Getters and Setters for string dates
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    
    public LocalDateTime getBorrowDateTime() {
        return DateUtils.fromString(borrowDate);
    }
    
    public void setBorrowDateTime(LocalDateTime borrowDate) {
        this.borrowDate = DateUtils.toString(borrowDate);
    }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getDueDateTime() {
        return DateUtils.fromString(dueDate);
    }
    
    public void setDueDateTime(LocalDateTime dueDate) {
        this.dueDate = DateUtils.toString(dueDate);
    }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    
    public LocalDateTime getReturnDateTime() {
        return DateUtils.fromString(returnDate);
    }
    
    public void setReturnDateTime(LocalDateTime returnDate) {
        this.returnDate = DateUtils.toString(returnDate);
    }

    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    /**
     * Check if loan is overdue
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue() {
        if (isReturned) {
            return false; // Returned books cannot be overdue
        }
        
        LocalDateTime dueDateTime = getDueDateTime();
        if (dueDateTime == null) {
            return false; // No due date set
        }
        
        return LocalDateTime.now().isAfter(dueDateTime);
    }

    /**
     * Calculate overdue days
     * @return number of overdue days
     */
    public int getOverdueDays() {
        if (!isOverdue()) return 0;
        
        LocalDateTime dueDateTime = getDueDateTime();
        if (dueDateTime == null) return 0;
        
        return (int) java.time.Duration.between(dueDateTime, LocalDateTime.now()).toDays();
    }
   

   
    }
