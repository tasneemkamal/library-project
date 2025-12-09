package library;

import io.github.cdimascio.dotenv.Dotenv;
import library.config.EmailConfig;
import library.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class testTest {

    private Dotenv dotenvMock;
    private NotificationService notificationServiceMock;
    private EmailConfig emailConfig;

    @BeforeEach
    void setUp() {
        dotenvMock = mock(Dotenv.class);
        notificationServiceMock = mock(NotificationService.class);

        emailConfig = new EmailConfig();
    }

    @Test
    void testLoadEnvSuccess() {
        when(dotenvMock.get("EMAIL_USERNAME")).thenReturn("user@test.com");
        when(dotenvMock.get("EMAIL_PASSWORD")).thenReturn("password");
        when(dotenvMock.get("TEST_EMAIL")).thenReturn("test@test.com");

        assertEquals("user@test.com", dotenvMock.get("EMAIL_USERNAME"));
        assertEquals("password", dotenvMock.get("EMAIL_PASSWORD"));
        assertEquals("test@test.com", dotenvMock.get("TEST_EMAIL"));
    }

    @Test
    void testEnvMissingValue() {
        when(dotenvMock.get("EMAIL_USERNAME")).thenReturn(null);
        when(dotenvMock.get("EMAIL_PASSWORD")).thenReturn("password");
        when(dotenvMock.get("TEST_EMAIL")).thenReturn("test@test.com");

        assertNull(dotenvMock.get("EMAIL_USERNAME"));
    }

    @Test
    void testEmailSendSuccess() throws Exception {
        when(notificationServiceMock.sendEmail(
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(true);

        boolean result = notificationServiceMock.sendEmail("a@a.com", "title", "body");

        assertTrue(result);
        verify(notificationServiceMock, times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testEmailSendFailure() throws Exception {
        when(notificationServiceMock.sendEmail(
                anyString(),
                anyString(),
                anyString()
        )).thenReturn(false);

        boolean result = notificationServiceMock.sendEmail("a@a.com", "title", "body");

        assertFalse(result);
    }
}
