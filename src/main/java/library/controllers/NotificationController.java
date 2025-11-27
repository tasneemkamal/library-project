package library.controllers;

import library.services.NotificationManager;
import library.services.NotificationService;
import library.config.EmailConfig;

/**
 * Controller for notification operations
 * @author Library Team
 * @version 1.0
 */
public class NotificationController {
    private NotificationManager notificationManager;
    private NotificationService notificationService;

    public NotificationController(NotificationManager notificationManager, 
                                 NotificationService notificationService) {
        this.notificationManager = notificationManager;
        this.notificationService = notificationService;
    }

    /**
     * Send overdue reminders to all users
     */
    public void sendOverdueReminders() {
        System.out.println("Sending overdue reminders...");
        int sentCount = notificationManager.sendOverdueReminders();
        System.out.println("Completed sending " + sentCount + " overdue reminders.");
    }

    /**
     * Send return reminders for items due soon
     * @param daysBefore days before due date to send reminder
     */
    public void sendReturnReminders(int daysBefore) {
        System.out.println("Sending return reminders for items due in " + daysBefore + " days...");
        int sentCount = notificationManager.sendReturnReminders(daysBefore);
        System.out.println("Completed sending " + sentCount + " return reminders.");
    }

    /**
     * Configure email settings for real notifications
     * @param host SMTP host
     * @param port SMTP port
     * @param username email username
     * @param password email password
     * @param enableTLS enable TLS
     */
    public void configureEmail(String host, String port, String username, String password, boolean enableTLS) {
        EmailConfig config = new EmailConfig();
        config.setHost(host);
        config.setPort(port);
        config.setUsername(username);
        config.setPassword(password);
        config.setEnableTLS(enableTLS);

        notificationService.setEmailConfig(config);
        notificationService.setRealMode(true);

        System.out.println("✅ Email configuration updated.");
        System.out.println("📧 Real email notifications are now ENABLED.");
    }

    /**
     * Enable mock mode (no real emails sent)
     */
    public void enableMockMode() {
        notificationService.setRealMode(false);
        System.out.println("🔄 Switched to MOCK mode. No real emails will be sent.");
    }

    /**
     * Enable real email mode
     */
    public void enableRealMode() {
        if (notificationService.getEmailConfig().isValid()) {
            notificationService.setRealMode(true);
            System.out.println("✅ Real email mode ENABLED.");
        } else {
            System.out.println("❌ Cannot enable real mode. Email configuration is incomplete.");
        }
    }

    /**
     * Test email configuration
     * @param testEmail email to send test to
     */
    public void testEmail(String testEmail) {
        System.out.println("Testing email configuration...");
        boolean success = notificationManager.testEmailConfiguration(testEmail);
        
        if (success) {
            System.out.println("🎉 Email system is working correctly!");
        } else {
            System.out.println("💡 Tips for troubleshooting:");
            System.out.println("1. Check your email credentials");
            System.out.println("2. Ensure SMTP is enabled in your email account");
            System.out.println("3. For Gmail, you might need an App Password");
            System.out.println("4. Check firewall and network settings");
        }
    }

    /**
     * Get current notification status
     */
    public void getStatus() {
        System.out.println("\n=== Notification System Status ===");
        System.out.println("Mode: " + (notificationService.isRealMode() ? "REAL" : "MOCK"));
        System.out.println("Enabled: " + (notificationService.isEnabled() ? "YES" : "NO"));
        
        if (notificationService.isRealMode()) {
            EmailConfig config = notificationService.getEmailConfig();
            System.out.println("SMTP Host: " + config.getHost());
            System.out.println("SMTP Port: " + config.getPort());
            System.out.println("Username: " + config.getUsername());
            System.out.println("Configuration Valid: " + (config.isValid() ? "YES" : "NO"));
        }
    }
}
