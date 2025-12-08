package library.patterns.observer;

import library.models.User;
import library.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class EmailNotifierTest {

    private NotificationService notificationService;
    private EmailNotifier emailNotifier;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        emailNotifier = new EmailNotifier(notificationService);
    }

    @Test
    void testNotify_SendsEmail() {
        // Arrange
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@example.com");

        String message = "Your book is overdue!";

        // Act
        emailNotifier.notify(user, message);

        // Assert
        verify(notificationService, times(1))
                .sendEmail("test@example.com", "Library Notification", message);
    }
}
