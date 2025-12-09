package library.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import library.config.EmailConfig;
import library.models.User;

class NotificationServiceTest {

    private NotificationService notificationService;
    private User user;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
        user = new User("1", "Kamal", "kamal@test.com", "MEMBER");
    }

    // ----------------------------- MOCK MODE EMAIL TEST -----------------------------
    @Test
    void testSendEmail_MockMode() {
        notificationService.setRealMode(false);

        boolean result = notificationService.sendEmail(
                "test@test.com",
                "Test Subject",
                "Test Body"
        );

        assertTrue(result);
    }

    // ----------------------------- REAL MODE WITH INVALID CONFIG -----------------------------
    @Test
    void testSendEmail_RealMode_InvalidConfig() {
        notificationService.setRealMode(true);

        EmailConfig config = new EmailConfig(); // missing host/port
        notificationService.setEmailConfig(config);

        boolean result = notificationService.sendEmail(
                "test@test.com",
                "Test Subject",
                "Test Body"
        );

        assertFalse(result);
    }

    // ----------------------------- NEW: ENABLED = FALSE -----------------------------
    @Test
    void testSendEmail_WhenDisabled() {
        notificationService.setEnabled(false);

        boolean result = notificationService.sendEmail(
                "x@test.com",
                "Sub",
                "Body"
        );

        assertFalse(result);
    }

    // ----------------------------- NEW: NULL "to" -----------------------------
    @Test
    void testSendEmail_NullTo() {
        boolean result = notificationService.sendEmail(
                null,
                "Sub",
                "Body"
        );

        assertFalse(result);
    }

    // ----------------------------- NEW: NULL subject -----------------------------
    @Test
    void testSendEmail_NullSubject() {
        boolean result = notificationService.sendEmail(
                "a@a.com",
                null,
                "Body"
        );

        assertFalse(result);
    }

    // ----------------------------- NEW: NULL body -----------------------------
    @Test
    void testSendEmail_NullBody() {
        boolean result = notificationService.sendEmail(
                "a@a.com",
                "Sub",
                null
        );

        assertFalse(result);
    }

    // ----------------------------- NEW: emailConfig = null -----------------------------
    @Test
    void testSendEmail_NoConfig_RealMode() {
        notificationService.setRealMode(true);
        notificationService.setEmailConfig(null);

        boolean result = notificationService.sendEmail(
                "a@a.com",
                "Sub",
                "Body"
        );

        assertFalse(result);
    }

    // ----------------------------- NEW: Real mode with valid config -----------------------------
    @Test
    void testSendEmail_RealMode_ValidConfig() {
        EmailConfig config = new EmailConfig();
        config.setHost("smtp.server.com");
        config.setPort("587");               // <<< هنا تم تعديلها إلى String
        config.setUsername("user");
        config.setPassword("pass");

        notificationService.setRealMode(true);
        notificationService.setEmailConfig(config);

        boolean result = notificationService.sendEmail(
                "a@a.com",
                "Subject",
                "Body"
        );

        assertTrue(result);
    }

    // ----------------------------- SEND OVERDUE REMINDER -----------------------------
    @Test
    void testSendOverdueReminder_UserObject() {
        boolean result = notificationService.sendOverdueReminder(
                user, 3, 12.50
        );
        assertTrue(result);
    }

    // ----------------------------- String version -----------------------------
    @Test
    void testSendOverdueReminder_StringVersion() {
        boolean result = notificationService.sendOverdueReminder(
                "kamal@test.com", "Kamal", 2, 5.0
        );
        assertTrue(result);
    }

    // ----------------------------- WELCOME EMAIL -----------------------------
    @Test
    void testSendWelcomeEmail() {
        boolean result = notificationService.sendWelcomeEmail(
                user, "temp123"
        );
        assertTrue(result);
    }

    // ----------------------------- RETURN REMINDER -----------------------------
    @Test
    void testSendReturnReminder() {
        boolean result = notificationService.sendReturnReminder(
                user, "Clean Code", "2025-12-15"
        );
        assertTrue(result);
    }

    // ----------------------------- PAYMENT CONFIRMATION -----------------------------
    @Test
    void testSendPaymentConfirmation() {
        boolean result = notificationService.sendPaymentConfirmation(
                user, 20.0, 5.0
        );
        assertTrue(result);
    }

    // ----------------------------- GETTERS / SETTERS TEST -----------------------------
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

    // ----------------------------- CONSTRUCTOR TEST -----------------------------
    @Test
    void testConstructorWithParams() {
        EmailConfig config = new EmailConfig();
        NotificationService service = new NotificationService(config, true);

        assertEquals(config, service.getEmailConfig());
        assertTrue(service.isRealMode());
        assertTrue(service.isEnabled());
    }
}
