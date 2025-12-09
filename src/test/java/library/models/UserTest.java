package library.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import library.utils.DateUtils;
import java.time.LocalDateTime;

public class UserTest {

    @Test
    public void testDefaultConstructor() {
        User user = new User();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.isActive());
    }

    @Test
    public void testParameterizedConstructor() {
        User user = new User("John", "john@example.com", "hash123", "ADMIN");

        assertEquals("John", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("hash123", user.getPasswordHash());
        assertEquals("ADMIN", user.getRole());
        assertTrue(user.isActive());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User();

        user.setId("U1");
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPasswordHash("abcd1234");
        user.setRole("USER");
        user.setActive(false);

        user.setCreatedAt("2025-01-01T10:00");
        user.setUpdatedAt("2025-01-02T09:00");

        assertEquals("U1", user.getId());
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("abcd1234", user.getPasswordHash());
        assertEquals("USER", user.getRole());
        assertFalse(user.isActive());
        assertEquals("2025-01-01T10:00", user.getCreatedAt());
        assertEquals("2025-01-02T09:00", user.getUpdatedAt());
    }

    @Test
    public void testDateTimeConverters() {
        User user = new User();

        LocalDateTime now = LocalDateTime.of(2024, 12, 25, 12, 30);
        user.setCreatedAtDateTime(now);
        user.setUpdatedAtDateTime(now);

        assertEquals(now, user.getCreatedAtDateTime());
        assertEquals(now, user.getUpdatedAtDateTime());
    }

    @Test
    public void testUpdateTimestamp() {
        User user = new User();

        String oldTimestamp = user.getUpdatedAt();
        
        // ننتظر 1 ms لضمان فرق زمني
        try { Thread.sleep(2); } catch (InterruptedException e) {}

        user.updateTimestamp();
        String newTimestamp = user.getUpdatedAt();

        assertNotNull(newTimestamp);
        assertFalse(newTimestamp.isEmpty());

        // oldTimestamp < newTimestamp
        assertTrue(
            LocalDateTime.parse(newTimestamp).isAfter(
                LocalDateTime.parse(oldTimestamp)
            )
        );
    }


    @Test
    public void testEqualsAndHashCode() {
        User u1 = new User("A", "a@a.com", "p1", "USER");
        User u2 = new User("A", "a@a.com", "p1", "USER");

        u1.setId("1");
        u2.setId("1");

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void testEqualsDifferent() {
        User u1 = new User("A", "a@a.com", "p1", "USER");
        User u2 = new User("B", "b@b.com", "p2", "ADMIN");

        u1.setId("1");
        u2.setId("2");

        assertNotEquals(u1, u2);
        assertNotEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void testToString() {
        User user = new User();
        user.setId("77");
        user.setName("TestName");
        user.setEmail("t@t.com");
        user.setRole("USER");

        String text = user.toString();

        assertTrue(text.contains("TestName"));
        assertTrue(text.contains("77"));
        assertTrue(text.contains("USER"));
    }

    @Test
    public void testEqualsWithOtherObjects() {
        User user = new User();

        assertNotEquals(user, null);
        assertNotEquals(user, "NotUser");
    }
}
