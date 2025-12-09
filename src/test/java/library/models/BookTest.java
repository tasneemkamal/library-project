package library.models;

import library.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Title A", "Author A", "12345", "BOOK");
        book.setId("B1");
    }

    @Test
    void testConstructorInitialization() {
        assertEquals("Title A", book.getTitle());
        assertEquals("Author A", book.getAuthor());
        assertEquals("12345", book.getIsbn());
        assertEquals("BOOK", book.getType());
        assertTrue(book.isAvailable());
        assertNotNull(book.getCreatedAt());
        assertNotNull(book.getUpdatedAt());
    }

    @Test
    void testDefaultConstructor() {
        Book b = new Book();
        assertNull(b.getId());
        assertNull(b.getTitle());
        assertNull(b.getAuthor());
        assertNull(b.getIsbn());
        assertFalse(b.isAvailable());
    }

    @Test
    void testGettersAndSetters() {
        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setIsbn("98765");
        book.setType("ANYTHING"); // setter does nothing logically
        book.setAvailable(false);

        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals("98765", book.getIsbn());

        // Because getType() always returns "BOOK"
        assertEquals("BOOK", book.getType());

        assertFalse(book.isAvailable());
    }


    @Test
    void testCreatedAtAndUpdatedAtDateParsing() {
        LocalDateTime now = LocalDateTime.now();
        String nowStr = DateUtils.toString(now);

        book.setCreatedAt(nowStr);
        book.setUpdatedAt(nowStr);

        assertEquals(now, book.getCreatedAtDateTime());
        assertEquals(now, book.getUpdatedAtDateTime());
    }

    @Test
    void testUpdateTimestamp() throws InterruptedException {
        String oldTimestamp = book.getUpdatedAt();

        Thread.sleep(5); // Guarantee timestamp changes

        book.updateTimestamp();

        String newTimestamp = book.getUpdatedAt();
        assertNotEquals(oldTimestamp, newTimestamp);

        // Ensure parsed LocalDateTime is valid
        assertTrue(book.getUpdatedAtDateTime().isAfter(book.getCreatedAtDateTime())
                || book.getUpdatedAtDateTime().isEqual(book.getCreatedAtDateTime()));
    }

    @Test
    void testTypeAlwaysBook() {
        assertEquals("BOOK", book.getType());
    }

    @Test
    void testIdSetAndGet() {
        book.setId("555");
        assertEquals("555", book.getId());
    }
}
