package library.models;

import library.utils.DateUtils;
import java.time.LocalDateTime;

/**
 * CD model representing a music CD in the library
 */
public class CD implements MediaItem {
    private String id;
    private String title;
    private String artist;
    private String genre;
    private int trackCount;
    private String publisher;
    private int releaseYear;
    private boolean isAvailable;
    private String createdAt;
    private String updatedAt;

    public CD() {}

    @Override
    public String getType() {
        return "CD";
    }

    // === Constructor required by the test (3 parameters) ===
    public CD(String title, String artist, String genre) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.trackCount = 0;
        this.publisher = "";
        this.releaseYear = 0;
        this.isAvailable = true;
        this.createdAt = DateUtils.toString(LocalDateTime.now());
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }

    // === Full constructor ===
    public CD(String title, String artist, String genre, int trackCount, String publisher, int releaseYear) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.trackCount = trackCount;
        this.publisher = publisher;
        this.releaseYear = releaseYear;
        this.isAvailable = true;
        this.createdAt = DateUtils.toString(LocalDateTime.now());
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }

    // ==========================
    // Getters and Setters
    // ==========================

    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
        updateTimestamp();
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        updateTimestamp();
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) {
        this.artist = artist;
        updateTimestamp();
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) {
        this.genre = genre;
        updateTimestamp();
    }

    public int getTrackCount() { return trackCount; }
    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
        updateTimestamp();
    }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
        updateTimestamp();
    }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
        updateTimestamp();
    }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) {
        isAvailable = available;
        updateTimestamp();
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        // intentionally NO updateTimestamp()
    }

    public LocalDateTime getCreatedAtDateTime() {
        return DateUtils.fromString(createdAt);
    }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        // intentionally NO updateTimestamp() لتجنب recursion
    }

    public LocalDateTime getUpdatedAtDateTime() {
        return DateUtils.fromString(updatedAt);
    }

    public void updateTimestamp() {
        String newTime = DateUtils.toString(LocalDateTime.now());

        // إذا القيمة الجديدة نفس القديمة → زدها نانو ثانية
        if (newTime.equals(updatedAt)) {
            LocalDateTime dt = DateUtils.fromString(newTime).plusNanos(1);
            newTime = DateUtils.toString(dt);
        }

        this.updatedAt = newTime;
    }

}
