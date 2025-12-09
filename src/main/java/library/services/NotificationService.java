package library.services;

import library.config.EmailConfig;
import library.models.User;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Service for handling email notifications
 */
public class NotificationService {
    private EmailConfig emailConfig;
    private boolean enabled;
    private boolean realMode;

    public NotificationService() {
        this.emailConfig = new EmailConfig();
        this.enabled = true;
        this.realMode = false; // Default to mock mode
    }

    public NotificationService(EmailConfig emailConfig, boolean realMode) {
        this.emailConfig = emailConfig;
        this.enabled = true;
        this.realMode = realMode;
    }

    public boolean sendEmail(String to, String subject, String body) {

        // 🚀 ADDITION 1: null-safety for test coverage
        if (to == null || subject == null || body == null) {
            return false;
        }

        if (!enabled) {
            System.out.println("Email notifications are disabled.");
            return false;
        }

        if (!realMode) {
            // Mock mode
            System.out.println("[MOCK MODE]");
            System.out.println("\n=== MOCK EMAIL NOTIFICATION ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("=== END MOCK EMAIL ===\n");
            return true;
        }

        // Real mode
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

            session.setDebug(true);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfig.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully to: " + to);
            return true;
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            return false;
        }
    }

    // Overdue reminder (User)
    public boolean sendOverdueReminder(User user, int overdueCount, double totalFine) {
        String subject = "📚 Library Overdue Items Reminder";
        String body = String.format(
            "Dear %s,\n\n" +
            "You have %d overdue item(s).\n" +
            "Total fine: $%.2f\n\n" +
            "Please return the items.\n\nBest regards,\nLibrary Management System",
            user.getName(), overdueCount, totalFine
        );

        return sendEmail(user.getEmail(), subject, body);
    }

    // Welcome email
    public boolean sendWelcomeEmail(User user, String temporaryPassword) {
        String subject = "👋 Welcome to Library Management System";
        String body = String.format(
            "Dear %s,\n\nWelcome!\nEmail: %s\nRole: %s\n",
            user.getName(), user.getEmail(), user.getRole()
        );

        if (temporaryPassword != null) {
            body += "\nTemporary Password: " + temporaryPassword;
        }

        return sendEmail(user.getEmail(), subject, body);
    }

    // Return reminder
    public boolean sendReturnReminder(User user, String bookTitle, String dueDate) {
        String subject = "⏰ Book Return Reminder";
        String body = String.format(
            "Dear %s,\n\nYour book \"%s\" is due on %s.",
            user.getName(), bookTitle, dueDate
        );

        return sendEmail(user.getEmail(), subject, body);
    }

    // Payment confirmation
    public boolean sendPaymentConfirmation(User user, double paymentAmount, double remainingBalance) {
        String subject = "✅ Fine Payment Confirmation";
        String body = String.format(
            "Dear %s,\n\nPayment: $%.2f\nRemaining: $%.2f",
            user.getName(), paymentAmount, remainingBalance
        );

        return sendEmail(user.getEmail(), subject, body);
    }

    // 🚀 FIX: Make subject consistent with method above
    public boolean sendOverdueReminder(String userEmail, String userName, int overdueCount, double totalFine) {
        String subject = "📚 Library Overdue Items Reminder";
        String body = String.format(
            "Dear %s,\n\nYou have %d overdue item(s).\nTotal fine: $%.2f\n\nPlease return them.",
            userName, overdueCount, totalFine
        );

        return sendEmail(userEmail, subject, body);
    }

    // getters / setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isRealMode() { return realMode; }
    public void setRealMode(boolean realMode) { this.realMode = realMode; }

    public EmailConfig getEmailConfig() { return emailConfig; }
    public void setEmailConfig(EmailConfig emailConfig) { this.emailConfig = emailConfig; }
}

