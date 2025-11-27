package library.patterns.observer;



import library.models.User;

/**
 * Observer interface for notification system
 * @author Library Team
 * @version 1.0
 */
public interface Observer {
    /**
     * Send notification to user
     * @param user the user to notify
     * @param message the notification message
     */
    void notify(User user, String message);
}