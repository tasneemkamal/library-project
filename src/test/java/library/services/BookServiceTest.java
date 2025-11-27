package library.services;



import library.models.Book;
import library.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * Comprehensive unit tests for BookService
 * @author Library Team
 * @version 1.0
 */
@DisplayName("BookService Tests")
class BookServiceTest {
    private BookService bookService;
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Nested
    @DisplayName("Add Book Tests")
    class AddBookTests {
        @Test
        @DisplayName("Should add book successfully with valid data")
        void testAddBookSuccess() {
            // Arrange
            String title = "Test Book";
            String author = "Test Author";
            String isbn = "1234567890";
            String type = "BOOK";

            when(bookRepository.findByIsbn(isbn)).thenReturn(null);
            when(bookRepository.save(any(Book.class))).thenReturn(true);

            // Act
            boolean result = bookService.addBook(title, author, isbn, type);

            // Assert
            assertTrue(result);
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("Should fail to add book with duplicate ISBN")
        void testAddBookDuplicateIsbn() {
            // Arrange
            String title = "Test Book";
            String author = "Test Author";
            String isbn = "1234567890";
            String type = "BOOK";

            Book existingBook = new Book("Existing Book", "Existing Author", isbn, "BOOK");
            when(bookRepository.findByIsbn(isbn)).thenReturn(existingBook);

            // Act
            boolean result = bookService.addBook(title, author, isbn, type);

            // Assert
            assertFalse(result);
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should add CD with correct type")
        void testAddCD() {
            // Arrange
            String title = "Test CD";
            String author = "Test Artist";
            String isbn = "0987654321";
            String type = "CD";

            when(bookRepository.findByIsbn(isbn)).thenReturn(null);
            when(bookRepository.save(any(Book.class))).thenReturn(true);

            // Act
            boolean result = bookService.addBook(title, author, isbn, type);

            // Assert
            assertTrue(result);
            verify(bookRepository).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("Search Book Tests")
    class SearchBookTests {
        @Test
        @DisplayName("Should search books by title")
        void testSearchByTitle() {
            // Arrange
            String query = "Java";
            Book book1 = new Book("Java Programming", "Author 1", "123", "BOOK");
            Book book2 = new Book("Advanced Java", "Author 2", "456", "BOOK");
            List<Book> expectedBooks = Arrays.asList(book1, book2);

            when(bookRepository.search(query)).thenReturn(expectedBooks);

            // Act
            List<Book> result = bookService.searchBooks(query);

            // Assert
            assertEquals(2, result.size());
            verify(bookRepository).search(query);
        }

        @Test
        @DisplayName("Should search books by author")
        void testSearchByAuthor() {
            // Arrange
            String query = "Martin";
            Book book = new Book("Clean Code", "Robert Martin", "789", "BOOK");
            List<Book> expectedBooks = Collections.singletonList(book);

            when(bookRepository.search(query)).thenReturn(expectedBooks);

            // Act
            List<Book> result = bookService.searchBooks(query);

            // Assert
            assertEquals(1, result.size());
            assertEquals("Robert Martin", result.get(0).getAuthor());
        }

        @Test
        @DisplayName("Should return all books for empty query")
        void testSearchEmptyQuery() {
            // Arrange
            String query = "";
            Book book1 = new Book("Book 1", "Author 1", "111", "BOOK");
            Book book2 = new Book("Book 2", "Author 2", "222", "BOOK");
            List<Book> allBooks = Arrays.asList(book1, book2);

            when(bookRepository.search(query)).thenReturn(allBooks);

            // Act
            List<Book> result = bookService.searchBooks(query);

            // Assert
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list for no matches")
        void testSearchNoMatches() {
            // Arrange
            String query = "Nonexistent";
            when(bookRepository.search(query)).thenReturn(Collections.emptyList());

            // Act
            List<Book> result = bookService.searchBooks(query);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Book Management Tests")
    class BookManagementTests {
        @Test
        @DisplayName("Should get all books")
        void testGetAllBooks() {
            // Arrange
            Book book1 = new Book("Book 1", "Author 1", "111", "BOOK");
            Book book2 = new Book("Book 2", "Author 2", "222", "CD");
            List<Book> allBooks = Arrays.asList(book1, book2);

            when(bookRepository.findAll()).thenReturn(allBooks);

            // Act
            List<Book> result = bookService.getAllBooks();

            // Assert
            assertEquals(2, result.size());
            verify(bookRepository).findAll();
        }

        @Test
        @DisplayName("Should find book by ID")
        void testFindBookById() {
            // Arrange
            String bookId = "BOOK_123";
            Book expectedBook = new Book("Test Book", "Test Author", "123", "BOOK");
            expectedBook.setId(bookId);

            when(bookRepository.findById(bookId)).thenReturn(expectedBook);

            // Act
            Book result = bookService.findBookById(bookId);

            // Assert
            assertNotNull(result);
            assertEquals(bookId, result.getId());
            verify(bookRepository).findById(bookId);
        }

        @Test
        @DisplayName("Should return null for non-existent book ID")
        void testFindNonExistentBookById() {
            // Arrange
            String nonExistentId = "NON_EXISTENT";
            when(bookRepository.findById(nonExistentId)).thenReturn(null);

            // Act
            Book result = bookService.findBookById(nonExistentId);

            // Assert
            assertNull(result);
        }
    }
}