package library.models;


import library.utils.DateUtils;
import java.time.LocalDateTime;

/**
 * Book model representing a library book
 * @author Library Team
 * @version 1.0
 */
public class Book implements MediaItem {
    // ... existing fields and methods ...
    
  
    private String id;
    private String title;
    private String author;
    private String isbn;
    private boolean isAvailable;
    private String type; // "BOOK" or "CD"
    private String createdAt;
    private String updatedAt;

    public Book() {}
    @Override
    public String getType() {
        return "BOOK";
    }
    public Book(String title, String author, String isbn, String type) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.type = type;
        this.isAvailable = true;
        this.createdAt = DateUtils.toString(LocalDateTime.now());
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

   
    public void setType(String type) { this.type = type; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCreatedAtDateTime() {
        return DateUtils.fromString(createdAt);
    }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getUpdatedAtDateTime() {
        return DateUtils.fromString(updatedAt);
    }
    
    /**
     * Update the updatedAt timestamp
     */
    public void updateTimestamp() {
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }
}