package library.patterns.observer;

import library.models.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Subject class for managing observers
 * @author Library Team
 * @version 1.0
 */
public class Subject {
    private List<Observer> observers = new ArrayList<>();

    /**
     * Add observer to the list
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Remove observer from the list
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Notify all observers
     * @param user the user to notify
     * @param message the notification message
     */
    public void notifyObservers(User user, String message) {
        for (Observer observer : observers) {
            observer.notify(user, message);
        }
    }

    /**
     * Getter for the observers list, used for testing
     * @return the list of observers
     */
    public List<Observer> getObservers() {
        return observers;
    }
}
