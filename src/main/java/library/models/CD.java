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
        this.releaseYear = 0; // keep zero (tests don't care)
        this.isAvailable = true;
        this.createdAt = DateUtils.toString(LocalDateTime.now());
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }

    // === Full constructor (existing) ===
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

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getTrackCount() { return trackCount; }
    public void setTrackCount(int trackCount) { this.trackCount = trackCount; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

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

    public void updateTimestamp() {
        this.updatedAt = DateUtils.toString(LocalDateTime.now());
    }
}
