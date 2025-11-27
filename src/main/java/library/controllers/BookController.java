package library.controllers;


import library.models.Book;
import library.services.BookService;
import java.util.List;

/**
 * Controller for book operations
 * @author Library Team
 * @version 1.0
 */
public class BookController {
    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Add a new book
     * @param title book title
     * @param author book author
     * @param isbn book ISBN
     * @param type book type
     */
    public void addBook(String title, String author, String isbn, String type) {
        boolean success = bookService.addBook(title, author, isbn, type);
        if (!success) {
            System.out.println("Failed to add book. Please check the input and try again.");
        }
    }

    /**
     * Search books
     * @param query search query
     */
    public void searchBooks(String query) {
        List<Book> books = bookService.searchBooks(query);
        if (books.isEmpty()) {
            System.out.println("No books found matching your search.");
        } else {
            System.out.println("\n=== Search Results ===");
            displayBooks(books);
        }
    }

    /**
     * View all books
     */
    public void viewAllBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books available in the library.");
        } else {
            System.out.println("\n=== All Books ===");
            displayBooks(books);
        }
    }

    /**
     * Display books in formatted manner
     * @param books list of books to display
     */
    private void displayBooks(List<Book> books) {
        System.out.printf("%-5s %-20s %-15s %-15s %-10s%n", 
            "ID", "Title", "Author", "ISBN", "Status");
        System.out.println("------------------------------------------------------------------------");
        
        for (Book book : books) {
            String status = book.isAvailable() ? "Available" : "Borrowed";
            System.out.printf("%-5s %-20s %-15s %-15s %-10s%n",
                book.getId().substring(0, 5) + "...",
                shortenString(book.getTitle(), 18),
                shortenString(book.getAuthor(), 13),
                book.getIsbn(),
                status);
        }
    }

    /**
     * Shorten string for display
     * @param str string to shorten
     * @param maxLength maximum length
     * @return shortened string
     */
    private String shortenString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}