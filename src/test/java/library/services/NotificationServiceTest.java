package library.services;

import library.config.EmailConfig;
import library.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private NotificationService notificationService;
    private User user;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
        user = new User("1", "Kamal", "kamal@test.com", "MEMBER");
    }

    // -----------------------------
    // MOCK MODE EMAIL TEST
    // -----------------------------
    @Test
    void testSendEmail_MockMode() {
        notificationService.setRealMode(false);

        boolean result = notificationService.sendEmail(
                "test@test.com",
                "Test Subject",
                "Test Body"
        );

        assertTrue(result); // mock mode always returns true
    }

    // -----------------------------
    // REAL MODE WITH INVALID CONFIG
    // -----------------------------
    @Test
    void testSendEmail_RealMode_InvalidConfig() {
        notificationService.setRealMode(true);

        EmailConfig config = new EmailConfig(); // invalid config
        notificationService.setEmailConfig(config);

        boolean result = notificationService.sendEmail(
                "test@test.com",
                "Test Subject",
                "Test Body"
        );

        assertFalse(result); // real mode + invalid config → false
    }

    // -----------------------------
    // SEND OVERDUE REMINDER
    // -----------------------------
    @Test
    void testSendOverdueReminder_UserObject() {
        boolean result = notificationService.sendOverdueReminder(
                user, 3, 12.50
        );
        assertTrue(result);
    }

    // -----------------------------
    // SEND OVERDUE REMINDER (String version)
    // -----------------------------
    @Test
    void testSendOverdueReminder_StringVersion() {
        boolean result = notificationService.sendOverdueReminder(
                "kamal@test.com", "Kamal", 2, 5.0
        );
        assertTrue(result);
    }

    // -----------------------------
    // SEND WELCOME EMAIL
    // -----------------------------
    @Test
    void testSendWelcomeEmail() {
        boolean result = notificationService.sendWelcomeEmail(
                user, "temp123"
        );
        assertTrue(result);
    }

    // -----------------------------
    // SEND RETURN REMINDER
    // -----------------------------
    @Test
    void testSendReturnReminder() {
        boolean result = notificationService.sendReturnReminder(
                user, "Clean Code", "2025-12-15"
        );
        assertTrue(result);
    }

    // -----------------------------
    // SEND PAYMENT CONFIRMATION
    // -----------------------------
    @Test
    void testSendPaymentConfirmation() {
        boolean result = notificationService.sendPaymentConfirmation(
                user, 20.0, 5.0
        );
        assertTrue(result);
    }

    // -----------------------------
    // GETTERS / SETTERS TEST
    // -----------------------------
    @Test
    void testGettersAndSetters() {
        notificationService.setEnabled(false);
        assertFalse(notificationService.isEnabled());

        notificationService.setRealMode(true);
        assertTrue(notificationService.isRealMode());

        EmailConfig config = new EmailConfig();
        config.setHost("smtp.server.com");
        notificationService.setEmailConfig(config);
        assertEquals("smtp.server.com", notificationService.getEmailConfig().getHost());
    }

    // -----------------------------
    // CONSTRUCTOR TEST
    // -----------------------------
    @Test
    void testConstructorWithParams() {
        EmailConfig config = new EmailConfig();
        NotificationService service = new NotificationService(config, true);

        assertEquals(config, service.getEmailConfig());
        assertTrue(service.isRealMode());
        assertTrue(service.isEnabled());
    }
}
