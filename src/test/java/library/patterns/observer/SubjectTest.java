package library.patterns.observer;

import library.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class SubjectTest {

    private Subject subject;
    private Observer observer1;
    private Observer observer2;
    private User user;

    @BeforeEach
    void setUp() {
        subject = new Subject();
        observer1 = mock(Observer.class);
        observer2 = mock(Observer.class);
        user = new User(); // إنشاء مستخدم وهمي للاختبارات
    }

    @Test
    void testAddObserver() {
        subject.addObserver(observer1);
        assertEquals(1, subject.getObservers().size(), "Observer should be added");
    }

    @Test
    void testRemoveObserver() {
        subject.addObserver(observer1);
        subject.removeObserver(observer1);
        assertEquals(0, subject.getObservers().size(), "Observer should be removed");
    }

    @Test
    void testNotifyObservers() {
        subject.addObserver(observer1);
        subject.addObserver(observer2);

        String message = "Test Message";
        subject.notifyObservers(user, message);

        // تأكد أن كل المراقبين تلقوا الإشعار
        verify(observer1).notify(user, message);
        verify(observer2).notify(user, message);
    }

    @Test
    void testNotifyObserversWhenNoObservers() {
        subject.notifyObservers(user, "No one should be notified");
        // لا توجد مراقبين لذا لا يجب أن يحدث شيء
        verify(observer1, never()).notify(any(), any());
        verify(observer2, never()).notify(any(), any());
    }
}
