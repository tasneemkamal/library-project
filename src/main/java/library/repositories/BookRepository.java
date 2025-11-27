package library.repositories;


import library.models.Book;
import library.utils.JsonFileHandler;
import  library.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for book data management using JSON file storage
 * @author Library Team
 * @version 1.0
 */
public class BookRepository {
    private static final String FILE_PATH = "data/books.json";
    private Map<String, Book> books;
    private Gson gson;
    private JsonFileHandler fileHandler;

    public BookRepository() {
        this.gson = GsonUtils.createGson(); // استخدام Gson المعدل
        this.fileHandler = new JsonFileHandler();
        this.books = loadBooks();
    }

    /**
     * Load books from JSON file
     * @return map of books
     */
    private Map<String, Book> loadBooks() {
        String json = fileHandler.readFromFile(FILE_PATH);
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, Book>>(){}.getType();
            Map<String, Book> loadedBooks = gson.fromJson(json, type);
            return loadedBooks != null ? loadedBooks : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error loading books from JSON: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Save books to JSON file
     * @return true if save successful, false otherwise
     */
    private boolean saveBooks() {
        try {
            String json = gson.toJson(books);
            return fileHandler.writeToFile(FILE_PATH, json);
        } catch (Exception e) {
            System.err.println("Error saving books to JSON: " + e.getMessage());
            return false;
        }
    }

    // باقي الدوال تبقى كما هي...
    // ... [نفس الكود السابق مع تعديلات بسيطة]
    
    /**
     * Update book in repository
     * @param book book to update
     * @return true if update successful, false otherwise
     */
    public boolean update(Book book) {
        if (books.containsKey(book.getId())) {
            book.updateTimestamp();
            books.put(book.getId(), book);
            return saveBooks();
        }
        return false;
    }
  
    
    /**
     * Generate unique ID for book
     * @return generated ID
     */
    private String generateId() {
        return "BOOK_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    /**
     * Save book to repository
     * @param book book to save
     * @return true if save successful, false otherwise
     */
    public boolean save(Book book) {
        if (book.getId() == null) {
            book.setId(generateId());
        }
        books.put(book.getId(), book);
        return saveBooks();
    }

    /**
     * Find book by ISBN
     * @param isbn book ISBN
     * @return book or null if not found
     */
    public Book findByIsbn(String isbn) {
        return books.values().stream()
                .filter(book -> isbn.equals(book.getIsbn()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find book by ID
     * @param id book ID
     * @return book or null if not found
     */
    public Book findById(String id) {
        return books.get(id);
    }

    /**
     * Get all books
     * @return list of all books
     */
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    /**
     * Search books by title, author, or ISBN
     * @param query search query
     * @return list of matching books
     */
    public List<Book> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(books.values());
        }

        String searchTerm = query.toLowerCase().trim();
        return books.values().stream()
                .filter(book -> 
                    book.getTitle().toLowerCase().contains(searchTerm) ||
                    book.getAuthor().toLowerCase().contains(searchTerm) ||
                    book.getIsbn().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

   
    /**
     * Delete book by ID
     * @param id book ID
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(String id) {
        Book book = books.remove(id);
        if (book != null) {
            return saveBooks();
        }
        return false;
    }
}