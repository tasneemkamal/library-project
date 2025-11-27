package library.services;



import library.models.User;
import library.models.Loan;
import library.models.CDFine;
import library.models.CDLoan;
import library.models.Fine;
import library.repositories.LoanRepository;
import library.repositories.CDFineRepository;
import library.repositories.CDLoanRepository;
import library.repositories.FineRepository;
import library.repositories.UserRepository;
import java.util.List;

/**
 * Manager for handling all notification operations
 * @author Library Team
 * @version 1.0
 */
public class NotificationManager {
	
	
	    
	    private CDLoanRepository cdLoanRepository;

    private CDFineRepository cdFineRepository;
    private NotificationService notificationService;
    private LoanRepository loanRepository;
    private FineRepository fineRepository;
    private UserRepository userRepository;


    public NotificationManager(NotificationService notificationService, 
                              LoanRepository loanRepository,
                              CDLoanRepository cdLoanRepository,
                              FineRepository fineRepository,
                              CDFineRepository cdFineRepository,
                              UserRepository userRepository) {
        this.notificationService = notificationService;
        this.loanRepository = loanRepository;
        this.cdLoanRepository = cdLoanRepository;
        this.fineRepository = fineRepository;
        this.cdFineRepository = cdFineRepository;
        this.userRepository = userRepository;
    }

    /**
     * Send overdue reminders to all users with overdue items
     * @return number of reminders sent
     */
    public int sendOverdueReminders() {
        List<Loan> overdueBookLoans = loanRepository.findOverdueLoans();
        List<CDLoan> overdueCDLoans = cdLoanRepository.findOverdueCDLoans();
        int sentCount = 0;

        // Send reminders for overdue books
        for (Loan loan : overdueBookLoans) {
            User user = userRepository.findById(loan.getUserId());
            if (user != null && user.isActive()) {
                List<Loan> userOverdueBooks = loanRepository.findByUserId(user.getId())
                        .stream()
                        .filter(Loan::isOverdue)
                        //.toList();
                        .collect(java.util.stream.Collectors.toList());


                double totalBookFines = fineRepository.findByUserId(user.getId())
                        .stream()
                        .filter(fine -> !fine.isPaid())
                        .mapToDouble(Fine::getRemainingAmount)
                        .sum();

                String message = String.format(
                    "You have %d overdue book(s).\nTotal book fines: $%.2f\n\nPlease return the items as soon as possible.",
                    userOverdueBooks.size(), totalBookFines
                );

                if (notificationService.sendEmail(user.getEmail(), "📚 Book Overdue Reminder", message)) {
                    sentCount++;
                    System.out.println("Sent book overdue reminder to: " + user.getEmail());
                }
            }
        }

        // Send reminders for overdue CDs
        for (CDLoan cdLoan : overdueCDLoans) {
            User user = userRepository.findById(cdLoan.getUserId());
            if (user != null && user.isActive()) {
                List<CDLoan> userOverdueCDs = cdLoanRepository.findByUserId(user.getId())
                        .stream()
                        .filter(CDLoan::isOverdue)
                        //.toList();
                        .collect(java.util.stream.Collectors.toList());


                double totalCDFines = cdFineRepository.findByUserId(user.getId())
                        .stream()
                        .filter(fine -> !fine.isPaid())
                        .mapToDouble(CDFine::getRemainingAmount)
                        .sum();

                String message = String.format(
                    "You have %d overdue CD(s).\nTotal CD fines: $%.2f\n\nPlease return the items as soon as possible.",
                    userOverdueCDs.size(), totalCDFines
                );

                if (notificationService.sendEmail(user.getEmail(), "💿 CD Overdue Reminder", message)) {
                    sentCount++;
                    System.out.println("Sent CD overdue reminder to: " + user.getEmail());
                }
            }
        }

        System.out.println("Sent " + sentCount + " overdue reminders.");
        return sentCount;
    }

    /**
     * Send combined reminder for users with both books and CDs overdue
     */
    public int sendCombinedReminders() {
        // Get all users with overdue items
        List<User> allUsers = userRepository.findAll();
        int sentCount = 0;

        for (User user : allUsers) {
            if (!user.isActive()) continue;

            List<Loan> userOverdueBooks = loanRepository.findByUserId(user.getId())
                    .stream()
                    .filter(Loan::isOverdue)
                    //.toList();
                    .collect(java.util.stream.Collectors.toList());


            List<CDLoan> userOverdueCDs = cdLoanRepository.findByUserId(user.getId())
                    .stream()
                    .filter(CDLoan::isOverdue)
                    //.toList();
                    .collect(java.util.stream.Collectors.toList());


            // Only send if user has any overdue items
            if (!userOverdueBooks.isEmpty() || !userOverdueCDs.isEmpty()) {
                double totalBookFines = fineRepository.findByUserId(user.getId())
                        .stream()
                        .filter(fine -> !fine.isPaid())
                        .mapToDouble(Fine::getRemainingAmount)
                        .sum();

                double totalCDFines = cdFineRepository.findByUserId(user.getId())
                        .stream()
                        .filter(fine -> !fine.isPaid())
                        .mapToDouble(CDFine::getRemainingAmount)
                        .sum();

                double totalFines = totalBookFines + totalCDFines;

                String message = String.format(
                    "Dear %s,\n\nYou have:\n" +
                    "- %d overdue book(s)\n" +
                    "- %d overdue CD(s)\n" +
                    "Total fines: $%.2f\n\n" +
                    "Please return the items as soon as possible to avoid additional charges.\n\n" +
                    "Best regards,\nLibrary Management System",
                    user.getName(), userOverdueBooks.size(), userOverdueCDs.size(), totalFines
                );

                if (notificationService.sendEmail(user.getEmail(), "📚💿 Library Overdue Items Reminder", message)) {
                    sentCount++;
                    System.out.println("Sent combined reminder to: " + user.getEmail());
                }
            }
        }

        System.out.println("Sent " + sentCount + " combined reminders.");
        return sentCount;
    }

    
    /**
     * Send return reminders for books due soon
     * @param daysBefore days before due date to send reminder
     * @return number of reminders sent
     */
    public int sendReturnReminders(int daysBefore) {
        List<Loan> activeLoans = loanRepository.findAll()
                .stream()
                .filter(loan -> !loan.isReturned())
                //.toList();
                .collect(java.util.stream.Collectors.toList());


        int sentCount = 0;
        java.time.LocalDateTime reminderDate = java.time.LocalDateTime.now().plusDays(daysBefore);

        for (Loan loan : activeLoans) {
            if (loan.getDueDateTime().isBefore(reminderDate) || 
                loan.getDueDateTime().isEqual(reminderDate)) {
                
                User user = userRepository.findById(loan.getUserId());
                if (user != null && user.isActive()) {
                    // In a real system, you'd get the book title from bookRepository
                    String bookTitle = "Book ID: " + loan.getBookId(); // Placeholder
                    String dueDate = loan.getDueDateTime().toString();
                    
                    if (notificationService.sendReturnReminder(user, bookTitle, dueDate)) {
                        sentCount++;
                        System.out.println("Sent return reminder to: " + user.getEmail());
                    }
                }
            }
        }

        System.out.println("Sent " + sentCount + " return reminders.");
        return sentCount;
    }

    /**
     * Send welcome email to new user
     * @param user new user
     * @return true if email sent successfully
     */
    public boolean sendWelcomeNotification(User user) {
        return notificationService.sendWelcomeEmail(user, null);
    }

    /**
     * Send payment confirmation
     * @param user user who made payment
     * @param paymentAmount payment amount
     * @param remainingBalance remaining balance
     * @return true if confirmation sent successfully
     */
    public boolean sendPaymentNotification(User user, double paymentAmount, double remainingBalance) {
        return notificationService.sendPaymentConfirmation(user, paymentAmount, remainingBalance);
    }

    /**
     * Test email configuration
     * @param testEmail email address to send test to
     * @return true if test successful
     */
    public boolean testEmailConfiguration(String testEmail) {
        System.out.println("Testing email configuration...");
        
        if (!notificationService.isRealMode()) {
            System.out.println("Currently in MOCK mode. Switching to REAL mode for test.");
            notificationService.setRealMode(true);
        }

        boolean success = notificationService.sendEmail(testEmail, 
            "Library System Test Email", 
            "This is a test email from Library Management System.");

        if (success) {
            System.out.println("✅ Email configuration test PASSED");
        } else {
            System.out.println("❌ Email configuration test FAILED");
        }

        return success;
    }
}