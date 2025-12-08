package library.repositories;

import library.models.CD;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class CDRepository {

    private static final String FILE_PATH = "data/cds.json";

    private Map<String, CD> cds = new HashMap<>();
    private Gson gson;
    private JsonFileHandler fileHandler;

    public CDRepository() {
        this.gson = GsonUtils.createGson();
        this.fileHandler = new JsonFileHandler();
        this.cds = loadCDs(); // بدون أي test data
    }

    private Map<String, CD> loadCDs() {
        String json = fileHandler.readFromFile(FILE_PATH);
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, CD>>() {}.getType();
            Map<String, CD> loaded = gson.fromJson(json, type);
            return loaded != null ? loaded : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private boolean saveCDs() {
        return fileHandler.writeToFile(FILE_PATH, gson.toJson(cds));
    }

    private String generateId() {
        return "CD_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    // CRUD

    public boolean save(CD cd) {
        if (cd.getId() == null) cd.setId(generateId());
        cd.updateTimestamp();
        cds.put(cd.getId(), cd);
        return saveCDs();
    }

    public CD findById(String id) {
        return cds.get(id);
    }

    public List<CD> findAll() {
        return new ArrayList<>(cds.values());
    }

    public List<CD> search(String query) {
        if (query == null || query.trim().isEmpty()) return findAll();

        String q = query.toLowerCase();
        return cds.values().stream()
                .filter(cd ->
                        cd.getTitle().toLowerCase().contains(q) ||
                        cd.getArtist().toLowerCase().contains(q) ||
                        cd.getGenre().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public boolean update(CD cd) {
        if (!cds.containsKey(cd.getId())) return false;
        cd.updateTimestamp();
        cds.put(cd.getId(), cd);
        return saveCDs();
    }

    public boolean delete(String id) {
        CD removed = cds.remove(id);
        if (removed == null) return false;
        return saveCDs();
    }

    public List<CD> findByArtist(String artist) {
        return cds.values().stream()
                .filter(cd -> artist.equalsIgnoreCase(cd.getArtist()))
                .collect(Collectors.toList());
    }

    public List<CD> findByGenre(String genre) {
        return cds.values().stream()
                .filter(cd -> genre.equalsIgnoreCase(cd.getGenre()))
                .collect(Collectors.toList());
    }
}
