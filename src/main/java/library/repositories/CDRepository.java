package library.repositories;



import library.models.CD;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for CD data management using JSON file storage
 * @author Library Team
 * @version 1.0
 */
public class CDRepository {
    private static final String FILE_PATH = "data/cds.json";
    private Map<String, CD> cds;
    private Gson gson;
    private JsonFileHandler fileHandler;

    public CDRepository() {
        this.gson = GsonUtils.createGson();
        this.fileHandler = new JsonFileHandler();
        this.cds = loadCDs();
    }

    /**
     * Load CDs from JSON file
     * @return map of CDs
     */
    private Map<String, CD> loadCDs() {
        String json = fileHandler.readFromFile(FILE_PATH);
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, CD>>(){}.getType();
            Map<String, CD> loadedCDs = gson.fromJson(json, type);
            return loadedCDs != null ? loadedCDs : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error loading CDs from JSON: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Save CDs to JSON file
     * @return true if save successful, false otherwise
     */
    private boolean saveCDs() {
        try {
            String json = gson.toJson(cds);
            return fileHandler.writeToFile(FILE_PATH, json);
        } catch (Exception e) {
            System.err.println("Error saving CDs to JSON: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate unique ID for CD
     * @return generated ID
     */
    private String generateId() {
        return "CD_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    /**
     * Save CD to repository
     * @param cd CD to save
     * @return true if save successful, false otherwise
     */
    public boolean save(CD cd) {
        if (cd.getId() == null) {
            cd.setId(generateId());
        }
        cd.updateTimestamp();
        cds.put(cd.getId(), cd);
        return saveCDs();
    }

    /**
     * Find CD by ID
     * @param id CD ID
     * @return CD or null if not found
     */
    public CD findById(String id) {
        return cds.get(id);
    }

    /**
     * Get all CDs
     * @return list of all CDs
     */
    public List<CD> findAll() {
        return new ArrayList<>(cds.values());
    }

    /**
     * Search CDs by title, artist, or genre
     * @param query search query
     * @return list of matching CDs
     */
    public List<CD> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(cds.values());
        }

        String searchTerm = query.toLowerCase().trim();
        return cds.values().stream()
                .filter(cd -> 
                    cd.getTitle().toLowerCase().contains(searchTerm) ||
                    cd.getArtist().toLowerCase().contains(searchTerm) ||
                    cd.getGenre().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    /**
     * Update CD in repository
     * @param cd CD to update
     * @return true if update successful, false otherwise
     */
    public boolean update(CD cd) {
        if (cds.containsKey(cd.getId())) {
            cd.updateTimestamp();
            cds.put(cd.getId(), cd);
            return saveCDs();
        }
        return false;
    }

    /**
     * Delete CD by ID
     * @param id CD ID
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(String id) {
        CD cd = cds.remove(id);
        if (cd != null) {
            return saveCDs();
        }
        return false;
    }

    /**
     * Find CDs by artist
     * @param artist artist name
     * @return list of CDs by the artist
     */
    public List<CD> findByArtist(String artist) {
        return cds.values().stream()
                .filter(cd -> artist.equalsIgnoreCase(cd.getArtist()))
                .collect(Collectors.toList());
    }

    /**
     * Find CDs by genre
     * @param genre music genre
     * @return list of CDs in the genre
     */
    public List<CD> findByGenre(String genre) {
        return cds.values().stream()
                .filter(cd -> genre.equalsIgnoreCase(cd.getGenre()))
                .collect(Collectors.toList());
    }
}