package library.services;



import library.models.CD;
import library.repositories.CDRepository;
import java.util.List;

/**
 * Service for handling CD operations
 * @author Library Team
 * @version 1.0
 */
public class CDService {
    private CDRepository cdRepository;

    public CDService(CDRepository cdRepository) {
        this.cdRepository = cdRepository;
    }

    /**
     * Add a new CD to the library
     * @param title CD title
     * @param artist CD artist
     * @param genre music genre
     * @param trackCount number of tracks
     * @param publisher publisher
     * @param releaseYear release year
     * @return true if CD added successfully, false otherwise
     */
    public boolean addCD(String title, String artist, String genre, int trackCount, String publisher, int releaseYear) {
        if (title == null || title.trim().isEmpty() || artist == null || artist.trim().isEmpty()) {
            System.out.println("Title and artist are required!");
            return false;
        }

        if (trackCount <= 0) {
            System.out.println("Track count must be positive!");
            return false;
        }

        if (releaseYear < 1900 || releaseYear > java.time.Year.now().getValue()) {
            System.out.println("Invalid release year!");
            return false;
        }

        CD cd = new CD(title, artist, genre, trackCount, publisher, releaseYear);
        boolean success = cdRepository.save(cd);
        if (success) {
            System.out.println("CD added successfully!");
        }
        return success;
    }

    /**
     * Search CDs by title, artist, or genre
     * @param query search query
     * @return list of matching CDs
     */
    public List<CD> searchCDs(String query) {
        return cdRepository.search(query);
    }

    /**
     * Get all CDs
     * @return list of all CDs
     */
    public List<CD> getAllCDs() {
        return cdRepository.findAll();
    }

    /**
     * Find CD by ID
     * @param id CD ID
     * @return CD or null if not found
     */
    public CD findCDById(String id) {
        return cdRepository.findById(id);
    }

    /**
     * Get CDs by artist
     * @param artist artist name
     * @return list of CDs by the artist
     */
    public List<CD> getCDsByArtist(String artist) {
        return cdRepository.findByArtist(artist);
    }

    /**
     * Get CDs by genre
     * @param genre music genre
     * @return list of CDs in the genre
     */
    public List<CD> getCDsByGenre(String genre) {
        return cdRepository.findByGenre(genre);
    }

    /**
     * Update CD availability
     * @param cdId CD ID
     * @param available availability status
     * @return true if update successful, false otherwise
     */
    public boolean updateAvailability(String cdId, boolean available) {
        CD cd = cdRepository.findById(cdId);
        if (cd == null) {
            return false;
        }
        cd.setAvailable(available);
        return cdRepository.update(cd);
    }
}