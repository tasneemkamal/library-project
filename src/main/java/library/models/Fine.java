package library.models;


import library.utils.DateUtils;
import java.time.LocalDateTime;

/**
 * Fine model representing a financial penalty
 * @author Library Team
 * @version 1.0
 */
public class Fine {
    private String id;
    private String userId;
    private String loanId;
    private double amount;
    private double paidAmount;
    private String issuedDate;
    private String paidDate;
    private boolean isPaid;

    public Fine() {}

    public Fine(String userId, String loanId, double amount) {
        this.userId = userId;
        this.loanId = loanId;
        this.amount = amount;
        this.paidAmount = 0.0;
        this.issuedDate = DateUtils.toString(LocalDateTime.now());
        this.isPaid = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }
    
    public LocalDateTime getIssuedDateTime() {
        return DateUtils.fromString(issuedDate);
    }

    public String getPaidDate() { return paidDate; }
    public void setPaidDate(String paidDate) { this.paidDate = paidDate; }
    
    public LocalDateTime getPaidDateTime() {
        return DateUtils.fromString(paidDate);
    }
    
    public void setPaidDateTime(LocalDateTime paidDate) {
        this.paidDate = DateUtils.toString(paidDate);
    }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    /**
     * Get remaining amount to pay
     * @return remaining fine amount
     */
    public double getRemainingAmount() {
        return amount - paidAmount;
    }

    /**
     * Make a payment towards the fine
     * @param paymentAmount amount to pay
     * @return true if payment successful, false otherwise
     */
    public boolean makePayment(double paymentAmount) {
        if (paymentAmount <= 0 || paymentAmount > getRemainingAmount()) {
            return false;
        }

        paidAmount += paymentAmount;
        if (paidAmount >= amount) {
            isPaid = true;
            setPaidDateTime(LocalDateTime.now());
        }
        return true;
    }
}