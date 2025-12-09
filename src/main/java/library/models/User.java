package library.models;

import library.utils.DateUtils;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User model representing a library user
 * @author Library Team
 * @version 1.1
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

    public User() {
        this.isActive = true;
        this.createdAt = DateUtils.toString(LocalDateTime.now());
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }

    public User(String name, String email, String passwordHash, String role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = true;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = DateUtils.toString(now);
        this.updatedAt = DateUtils.toString(now);
    }

    // Getters and Setters
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

    // equals (for test coverage)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(email, other.email)
                && Objects.equals(name, other.name)
                && Objects.equals(role, other.role)
                && Objects.equals(passwordHash, other.passwordHash)
                && isActive == other.isActive;
    }

    // hashCode (for coverage)
    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, role, passwordHash, isActive);
    }

    // toString (for coverage)
    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "', email='" + email + "', role='" + role + "'}";
    }
}
