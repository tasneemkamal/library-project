package library.repositories;



import library.models.User;
import library.utils.GsonUtils;
import library.utils.JsonFileHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Repository for user data management using JSON file storage
 * @author Library Team
 * @version 1.0
 */
public class UserRepository {
    private static final String FILE_PATH = "data/users.json";
    private Map<String, User> users;
    private Gson gson;
    private JsonFileHandler fileHandler;

    public UserRepository() {
        this.gson = GsonUtils.createGson(); // استخدام Gson المعدل
        this.fileHandler = new JsonFileHandler();
        this.users = loadUsers();
    }

    /**
     * Load users from JSON file
     * @return map of users
     */
    private Map<String, User> loadUsers() {
        String json = fileHandler.readFromFile(FILE_PATH);
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, User>>(){}.getType();
            Map<String, User> loadedUsers = gson.fromJson(json, type);
            return loadedUsers != null ? loadedUsers : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error loading users from JSON: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Save users to JSON file
     * @return true if save successful, false otherwise
     */
    private boolean saveUsers() {
        try {
            String json = gson.toJson(users);
            return fileHandler.writeToFile(FILE_PATH, json);
        } catch (Exception e) {
            System.err.println("Error saving users to JSON: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate unique ID for user
     * @return generated ID
     */
    private String generateId() {
        return "USER_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    /**
     * Save user to repository
     * @param user user to save
     * @return true if save successful, false otherwise
     */
    public boolean save(User user) {
        if (user.getId() == null) {
            user.setId(generateId());
        }
        user.updateTimestamp();
        users.put(user.getId(), user);
        return saveUsers();
    }

    /**
     * Find user by email
     * @param email user email
     * @return user or null if not found
     */
    public User findByEmail(String email) {
        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find user by ID
     * @param id user ID
     * @return user or null if not found
     */
    public User findById(String id) {
        return users.get(id);
    }

    /**
     * Get all users
     * @return list of all users
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Delete user by ID
     * @param id user ID
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(String id) {
        User user = users.remove(id);
        if (user != null) {
            return saveUsers();
        }
        return false;
    }
}