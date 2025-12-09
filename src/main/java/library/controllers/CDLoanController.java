package library.controllers;

import library.services.CDLoanService;
import library.services.CDFineService;
import library.repositories.UserRepository;
import library.repositories.CDRepository;
import library.models.CDLoan;
import library.models.CDFine;
import library.models.CD;

import java.util.List;

public class CDLoanController {

    private final CDLoanService cdLoanService;
    private final CDFineService cdFineService;
    private final UserRepository userRepository;
    private final CDRepository cdRepository;

    public CDLoanController(CDLoanService cdLoanService, CDFineService cdFineService,
                            UserRepository userRepository, CDRepository cdRepository) {
        this.cdLoanService = cdLoanService;
        this.cdFineService = cdFineService;
        this.userRepository = userRepository;
        this.cdRepository = cdRepository;
    }

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

    public void borrowCD(String userId, String cdId) {
        boolean success = cdLoanService.borrowCD(userId, cdId);
        System.out.println(success ? "CD borrowed successfully" : "Failed to borrow CD");
    }

    public void returnCD(String cdLoanId) {
        boolean success = cdLoanService.returnCD(cdLoanId);
        System.out.println(success ? "CD returned successfully" : "Failed to return CD");
    }

    public void payCDFine(String cdFineId, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid payment amount");
            return;
        }

        boolean success = cdFineService.payCDFine(cdFineId, amount);
        System.out.println(success ? "CD fine payment processed successfully"
                                   : "Failed to process CD fine payment");
    }

    private String shortenString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str != null ? str : "";
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}

