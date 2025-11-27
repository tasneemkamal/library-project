package library.patterns.observer;



import library.models.User;
import library.services.NotificationService;


/**
 * Email notifier implementation of Observer
 * @author Library Team
 * @version 1.0
 */
public class EmailNotifier implements Observer {
    private NotificationService notificationService;

    public EmailNotifier(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void notify(User user, String message) {
        notificationService.sendEmail(user.getEmail(), "Library Notification", message);
    }
}