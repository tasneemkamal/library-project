package library.models;


import library.utils.DateUtils;
import java.time.LocalDateTime;

/**
 * User model representing a library user
 * @author Library Team
 * @version 1.0
 */
public class User {
    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private String role; // "ADMIN" or "USER"
    private boolean isActive;
    private String createdAt;
    private String updatedAt;

    public User() {}

    public User(String name, String email, String passwordHash, String role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = true;
        this.createdAt = DateUtils.toString(LocalDateTime.now());
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }

    // Getters and Setters for string dates
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getCreatedAtDateTime() {
        return DateUtils.fromString(createdAt);
    }
    
    public void setCreatedAtDateTime(LocalDateTime createdAt) {
        this.createdAt = DateUtils.toString(createdAt);
    }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getUpdatedAtDateTime() {
        return DateUtils.fromString(updatedAt);
    }
    
    public void setUpdatedAtDateTime(LocalDateTime updatedAt) {
        this.updatedAt = DateUtils.toString(updatedAt);
    }
    
    /**
     * Update the updatedAt timestamp
     */
    public void updateTimestamp() {
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }
}