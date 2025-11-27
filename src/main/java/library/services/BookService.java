package library.services;


import library.models.Book;
import library.repositories.BookRepository;

import java.util.List;

/**
 * Service for handling book operations
 * @author Library Team
 * @version 1.0
 */
public class BookService {
    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Add a new book to the library
     * @param title book title
     * @param author book author
     * @param isbn book ISBN
     * @param type book type (BOOK or CD)
     * @return true if book added successfully, false otherwise
     */
    public boolean addBook(String title, String author, String isbn, String type) {
        if (bookRepository.findByIsbn(isbn) != null) {
            System.out.println("Book with this ISBN already exists!");
            return false;
        }

        Book book = new Book(title, author, isbn, type);
        boolean success = bookRepository.save(book);
        if (success) {
            System.out.println("Book added successfully!");
        }
        return success;
    }

    /**
     * Search books by title, author, or ISBN
     * @param query search query
     * @return list of matching books
     */
    public List<Book> searchBooks(String query) {
        return bookRepository.search(query);
    }

    /**
     * Get all books
     * @return list of all books
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Find book by ID
     * @param id book ID
     * @return book or null if not found
     */
    public Book findBookById(String id) {
        return bookRepository.findById(id);
    }
}