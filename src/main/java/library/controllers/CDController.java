package library.controllers;



import library.models.CD;
import library.services.CDService;
import java.util.List;

/**
 * Controller for CD operations
 * @author Library Team
 * @version 1.0
 */
public class CDController {
    private CDService cdService;

    public CDController(CDService cdService) {
        this.cdService = cdService;
    }

    /**
     * Add a new CD
     * @param title CD title
     * @param artist CD artist
     * @param genre music genre
     * @param trackCount number of tracks
     * @param publisher publisher
     * @param releaseYear release year
     */
    public void addCD(String title, String artist, String genre, int trackCount, String publisher, int releaseYear) {
        boolean success = cdService.addCD(title, artist, genre, trackCount, publisher, releaseYear);
        if (!success) {
            System.out.println("Failed to add CD. Please check the input and try again.");
        }
    }

    /**
     * Search CDs
     * @param query search query
     */
    public void searchCDs(String query) {
        List<CD> cds = cdService.searchCDs(query);
        if (cds.isEmpty()) {
            System.out.println("No CDs found matching your search.");
        } else {
            System.out.println("\n=== CD Search Results ===");
            displayCDs(cds);
        }
    }

    /**
     * View all CDs
     */
    public void viewAllCDs() {
        List<CD> cds = cdService.getAllCDs();
        if (cds.isEmpty()) {
            System.out.println("No CDs available in the library.");
        } else {
            System.out.println("\n=== All CDs ===");
            displayCDs(cds);
        }
    }

    /**
     * View CDs by artist
     * @param artist artist name
     */
    public void viewCDsByArtist(String artist) {
        List<CD> cds = cdService.getCDsByArtist(artist);
        if (cds.isEmpty()) {
            System.out.println("No CDs found for artist: " + artist);
        } else {
            System.out.println("\n=== CDs by " + artist + " ===");
            displayCDs(cds);
        }
    }

    /**
     * View CDs by genre
     * @param genre music genre
     */
    public void viewCDsByGenre(String genre) {
        List<CD> cds = cdService.getCDsByGenre(genre);
        if (cds.isEmpty()) {
            System.out.println("No CDs found in genre: " + genre);
        } else {
            System.out.println("\n=== " + genre + " CDs ===");
            displayCDs(cds);
        }
    }

    /**
     * Display CDs in formatted manner
     * @param cds list of CDs to display
     */
    private void displayCDs(List<CD> cds) {
        System.out.printf("%-5s %-20s %-15s %-12s %-8s %-10s %-6s%n", 
            "ID", "Title", "Artist", "Genre", "Tracks", "Publisher", "Year");
        System.out.println("--------------------------------------------------------------------------------------");
        
        for (CD cd : cds) {
            String status = cd.isAvailable() ? "✅" : "⏳";
            System.out.printf("%-5s %-20s %-15s %-12s %-8s %-10s %-6s %s%n",
                cd.getId().substring(0, 5) + "...",
                shortenString(cd.getTitle(), 18),
                shortenString(cd.getArtist(), 13),
                shortenString(cd.getGenre(), 10),
                cd.getTrackCount(),
                shortenString(cd.getPublisher(), 8),
                cd.getReleaseYear(),
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
        if (str == null || str.length() <= maxLength) {
            return str != null ? str : "";
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}

