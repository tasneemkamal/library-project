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
import java.util.logging.Logger;

public class NotificationManager {

    private static final Logger LOGGER = Logger.getLogger(NotificationManager.class.getName());
    private static final String SENT_PREFIX = "Sent ";

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

    // ============================================
    //  Overdue Reminders
    // ============================================
    public int sendOverdueReminders() {
        List<Loan> overdueBookLoans = loanRepository.findOverdueLoans();
        List<CDLoan> overdueCDLoans = cdLoanRepository.findOverdueCDLoans();
        int sentCount = 0;

        // Books
        for (Loan loan : overdueBookLoans) {
            User user = userRepository.findById(loan.getUserId());
            if (user != null && user.isActive()) {

                List<Loan> userOverdueBooks = loanRepository.findByUserId(user.getId())
                        .stream().filter(Loan::isOverdue).toList();

                double totalBookFines = fineRepository.findByUserId(user.getId())
                        .stream().filter(f -> !f.isPaid())
                        .mapToDouble(Fine::getRemainingAmount).sum();

                String message = String.format(
                        "You have %d overdue book(s).\nTotal book fines: $%.2f\n\nPlease return the items.",
                        userOverdueBooks.size(), totalBookFines
                );

                if (notificationService.sendEmail(user.getEmail(), "📚 Book Overdue Reminder", message)) {
                    sentCount++;
                    LOGGER.info(SENT_PREFIX + "book overdue reminder to: " + user.getEmail());
                }
            }
        }

        // CDs
        for (CDLoan cdLoan : overdueCDLoans) {
            User user = userRepository.findById(cdLoan.getUserId());
            if (user != null && user.isActive()) {

                List<CDLoan> userOverdueCDs = cdLoanRepository.findByUserId(user.getId())
                        .stream().filter(CDLoan::isOverdue).toList();

                double totalCDFines = cdFineRepository.findByUserId(user.getId())
                        .stream().filter(fine -> !fine.isPaid())
                        .mapToDouble(CDFine::getRemainingAmount).sum();

                String message = String.format(
                        "You have %d overdue CD(s).\nTotal CD fines: $%.2f\n\nPlease return the items.",
                        userOverdueCDs.size(), totalCDFines
                );

                if (notificationService.sendEmail(user.getEmail(), "💿 CD Overdue Reminder", message)) {
                    sentCount++;
                    LOGGER.info(SENT_PREFIX + "CD overdue reminder to: " + user.getEmail());
                }
            }
        }

        LOGGER.info(SENT_PREFIX + sentCount + " overdue reminders.");
        return sentCount;
    }

    // ============================================
    // Combined Reminders
    // ============================================
    public int sendCombinedReminders() {
        List<User> allUsers = userRepository.findAll();
        int sentCount = 0;

        for (User user : allUsers) {
            if (!user.isActive()) continue;

            List<Loan> userOverdueBooks = loanRepository.findByUserId(user.getId())
                    .stream().filter(Loan::isOverdue).toList();

            List<CDLoan> userOverdueCDs = cdLoanRepository.findByUserId(user.getId())
                    .stream().filter(CDLoan::isOverdue).toList();

            if (userOverdueBooks.isEmpty() && userOverdueCDs.isEmpty()) continue;

            double totalBookFines = fineRepository.findByUserId(user.getId())
                    .stream().filter(f -> !f.isPaid())
                    .mapToDouble(Fine::getRemainingAmount).sum();

            double totalCDFines = cdFineRepository.findByUserId(user.getId())
                    .stream().filter(f -> !f.isPaid())
                    .mapToDouble(CDFine::getRemainingAmount).sum();

            double totalFines = totalBookFines + totalCDFines;

            String message = String.format(
                    "Dear %s,\n\nYou have:\n- %d overdue book(s)\n- %d overdue CD(s)\nTotal fines: $%.2f\n\nPlease return them.\n\nLibrary System",
                    user.getName(), userOverdueBooks.size(), userOverdueCDs.size(), totalFines
            );

            if (notificationService.sendEmail(user.getEmail(), "📚💿 Combined Reminder", message)) {
                sentCount++;
                LOGGER.info(SENT_PREFIX + "combined reminder to: " + user.getEmail());
            }
        }

        LOGGER.info(SENT_PREFIX + sentCount + " combined reminders.");
        return sentCount;
    }

    // ============================================
    // Return Reminders
    // ============================================
    public int sendReturnReminders(int daysBefore) {
        List<Loan> activeLoans = loanRepository.findAll()
                .stream().filter(l -> !l.isReturned()).toList();

        int sentCount = 0;

        var reminderDate = java.time.LocalDateTime.now().plusDays(daysBefore);

        for (Loan loan : activeLoans) {
            if (!loan.getDueDateTime().isBefore(reminderDate)
                    && !loan.getDueDateTime().isEqual(reminderDate)) continue;

            User user = userRepository.findById(loan.getUserId());
            if (user != null && user.isActive()) {

                if (notificationService.sendReturnReminder(
                        user,
                        "Book ID: " + loan.getBookId(),
                        loan.getDueDateTime().toString()
                )) {
                    sentCount++;
                    LOGGER.info(SENT_PREFIX + "return reminder to: " + user.getEmail());
                }
            }
        }

        LOGGER.info(SENT_PREFIX + sentCount + " return reminders.");
        return sentCount;
    }

    // ============================================
    // Small Notifications
    // ============================================
    public boolean sendWelcomeNotification(User user) {
        return notificationService.sendWelcomeEmail(user, null);
    }

    public boolean sendPaymentNotification(User user, double paymentAmount, double remainingBalance) {
        return notificationService.sendPaymentConfirmation(user, paymentAmount, remainingBalance);
    }

    // ============================================
    // Email Configuration Test
    // ============================================
    public boolean testEmailConfiguration(String testEmail) {

        LOGGER.info("Testing email configuration...");

        if (!notificationService.isRealMode()) {
            LOGGER.info("Currently in MOCK mode. Switching to REAL mode...");
            notificationService.setRealMode(true);
        }

        boolean success = notificationService.sendEmail(
                testEmail,
                "Library System Test Email",
                "This is a test email."
        );

        LOGGER.info(success ?
                "Email configuration test PASSED" :
                "Email configuration test FAILED"
        );

        return success;
    }
}
