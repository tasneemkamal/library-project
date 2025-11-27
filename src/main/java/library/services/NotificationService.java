package library.services;


import library.config.EmailConfig;
import library.models.User;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;


/**
 * Service for handling email notifications
 * @author Library Team
 * @version 1.0
 */
public class NotificationService {
    private EmailConfig emailConfig;
    private boolean enabled;
    private boolean realMode;

    public NotificationService() {
        this.emailConfig = new EmailConfig();
        this.enabled = true;
        this.realMode = false; // Default to mock mode for safety
    }

    public NotificationService(EmailConfig emailConfig, boolean realMode) {
        this.emailConfig = emailConfig;
        this.enabled = true;
        this.realMode = realMode;
    }

    /**
     * Send email notification
     * @param to recipient email
     * @param subject email subject
     * @param body email body
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmail(String to, String subject, String body) {
        if (!enabled) {
            System.out.println("Email notifications are disabled.");
            return false;
        }

        if (!realMode) {
            // Mock mode - just print to console
            System.out.println("\n=== MOCK EMAIL NOTIFICATION ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("=== END MOCK EMAIL ===\n");
            return true;
        }

        // Real mode - send actual email
        if (!emailConfig.isValid()) {
            System.err.println("Email configuration is incomplete. Cannot send real emails.");
            return false;
        }

        try {
            Properties props = emailConfig.getProperties();
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                }
            });

            // Enable debug mode for troubleshooting
            session.setDebug(true);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfig.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
            return true;
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to " + to + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Send overdue reminder to user
     * @param user user object
     * @param overdueCount number of overdue items
     * @param totalFine total fine amount
     * @return true if reminder sent successfully, false otherwise
     */
    public boolean sendOverdueReminder(User user, int overdueCount, double totalFine) {
        String subject = "📚 Library Overdue Items Reminder";
        String body = String.format(
            "Dear %s,\n\n" +
            "You have %d overdue item(s) in the library.\n" +
            "Total fine: $%.2f\n\n" +
            "Please return the items as soon as possible to avoid additional charges.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            user.getName(), overdueCount, totalFine
        );

        return sendEmail(user.getEmail(), subject, body);
    }

    /**
     * Send welcome email to new user
     * @param user new user
     * @param temporaryPassword temporary password (if any)
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendWelcomeEmail(User user, String temporaryPassword) {
        String subject = "👋 Welcome to Library Management System";
        String body = String.format(
            "Dear %s,\n\n" +
            "Welcome to our Library Management System!\n\n" +
            "Your account has been successfully created.\n" +
            "Email: %s\n" +
            "Role: %s\n\n" +
            "You can now login and start using our library services.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            user.getName(), user.getEmail(), user.getRole()
        );

        if (temporaryPassword != null) {
            body += "\nTemporary Password: " + temporaryPassword + " (Please change after first login)";
        }

        return sendEmail(user.getEmail(), subject, body);
    }

    /**
     * Send book return reminder
     * @param user user object
     * @param bookTitle book title
     * @param dueDate due date
     * @return true if reminder sent successfully, false otherwise
     */
    public boolean sendReturnReminder(User user, String bookTitle, String dueDate) {
        String subject = "⏰ Book Return Reminder";
        String body = String.format(
            "Dear %s,\n\n" +
            "This is a friendly reminder that your book \"%s\" is due on %s.\n\n" +
            "Please return it on time to avoid overdue fines.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            user.getName(), bookTitle, dueDate
        );

        return sendEmail(user.getEmail(), subject, body);
    }

    /**
     * Send fine payment confirmation
     * @param user user object
     * @param paymentAmount payment amount
     * @param remainingBalance remaining balance
     * @return true if confirmation sent successfully, false otherwise
     */
    public boolean sendPaymentConfirmation(User user, double paymentAmount, double remainingBalance) {
        String subject = "✅ Fine Payment Confirmation";
        String body = String.format(
            "Dear %s,\n\n" +
            "Your payment of $%.2f has been received successfully.\n" +
            "Remaining balance: $%.2f\n\n" +
            "Thank you for your payment.\n\n" +
            "Best regards,\n" +
            "Library Management System",
            user.getName(), paymentAmount, remainingBalance
        );

        return sendEmail(user.getEmail(), subject, body);
    }

    // Getters and setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isRealMode() { return realMode; }
    public void setRealMode(boolean realMode) { this.realMode = realMode; }

    public EmailConfig getEmailConfig() { return emailConfig; }
    public void setEmailConfig(EmailConfig emailConfig) { this.emailConfig = emailConfig; }


    

   

   
        

    /**
     * Send overdue reminder to user
     * @param userEmail user's email
     * @param userName user's name
     * @param overdueCount number of overdue items
     * @param totalFine total fine amount
     * @return true if reminder sent successfully, false otherwise
     */
    public boolean sendOverdueReminder(String userEmail, String userName, int overdueCount, double totalFine) {
        String subject = "Library Overdue Items Reminder";
        String body = String.format(
            "Dear %s,\n\nYou have %d overdue item(s) in the library.\nTotal fine: $%.2f\n\nPlease return the items as soon as possible to avoid additional charges.\n\nBest regards,\nLibrary Management System",
            userName, overdueCount, totalFine
        );

        return sendEmail(userEmail, subject, body);
    }

}
