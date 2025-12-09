package library.models;

import library.utils.DateUtils;
import java.time.LocalDateTime;

/**
 * CD Fine model representing a financial penalty for CD overdue
 * @author Library Team
 * @version 1.0
 */
public class CDFine {

    private String id;
    private String userId;
    private String cdLoanId;
    private double amount;
    private double paidAmount;
    private String issuedDate;
    private String paidDate;
    private boolean isPaid;

    // Added for compatibility with controller/tests
    private double remainingAmount;

    public CDFine() {}

    public CDFine(String userId, String cdLoanId, double amount) {
        if (userId == null || cdLoanId == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid CD fine parameters: userId, cdLoanId cannot be null and amount must be positive");
        }

        this.userId = userId;
        this.cdLoanId = cdLoanId;
        this.amount = amount;
        this.paidAmount = 0.0;
        this.issuedDate = DateUtils.toString(LocalDateTime.now());
        this.isPaid = false;

        // Initialize remaining amount
        this.remainingAmount = amount;
    }

    // Getters and Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCdLoanId() { return cdLoanId; }
    public void setCdLoanId(String cdLoanId) { this.cdLoanId = cdLoanId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        if (amount < 0) throw new IllegalArgumentException("CD fine amount cannot be negative");
        this.amount = amount;
    }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) {
        if (paidAmount < 0) throw new IllegalArgumentException("Paid amount cannot be negative");
        this.paidAmount = paidAmount;
    }

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
     * Original logic: remaining amount = amount - paidAmount
     */
    public double getRemainingAmount() {
        return amount - paidAmount;
    }

    /**
     * NEW — added for full test/controller compatibility
     */
    public double getRemainingAmountValue() {
        return remainingAmount;
    }

    /**
     * NEW — this is the missing method causing your error
     */
    public void setRemainingAmount(double remainingAmount) {
        if (remainingAmount < 0) {
            throw new IllegalArgumentException("Remaining amount cannot be negative");
        }
        this.remainingAmount = remainingAmount;
    }

    /**
     * Make a payment towards the CD fine
     */
    public boolean makePayment(double paymentAmount) {
        if (paymentAmount <= 0 || paymentAmount > getRemainingAmount()) {
            return false;
        }

        paidAmount += paymentAmount;

        // Update new remainingAmount field
        remainingAmount = getRemainingAmount();

        if (paidAmount >= amount) {
            isPaid = true;
            setPaidDateTime(LocalDateTime.now());
        }

        return true;
    }
}
