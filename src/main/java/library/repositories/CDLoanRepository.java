package library.repositories;

import library.models.CDLoan;
import library.utils.JsonFileHandler;
import library.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for CD loan data management using JSON file storage
 * @author Library Team
 * @version 1.0
 */
public class CDLoanRepository {
    private static final String FILE_PATH = "data/cdloans.json";
    private Map<String, CDLoan> cdLoans;
    private Gson gson;
    private JsonFileHandler fileHandler;

    public CDLoanRepository() {
        this.gson = GsonUtils.createGson();
        this.fileHandler = new JsonFileHandler();
        this.cdLoans = loadCDLoans();
    }

    /**
     * Load CD loans from JSON file
     * @return map of CD loans
     */
    private Map<String, CDLoan> loadCDLoans() {
        String json = fileHandler.readFromFile(FILE_PATH);
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<String, CDLoan>>(){}.getType();
            Map<String, CDLoan> loadedCDLoans = gson.fromJson(json, type);
            return loadedCDLoans != null ? loadedCDLoans : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error loading CD loans from JSON: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Save CD loans to JSON file
     * @return true if save successful, false otherwise
     */
    private boolean saveCDLoans() {
        try {
            String json = gson.toJson(cdLoans);
            return fileHandler.writeToFile(FILE_PATH, json);
        } catch (Exception e) {
            System.err.println("Error saving CD loans to JSON: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate unique ID for CD loan
     * @return generated ID
     */
    private String generateId() {
        return "CDLOAN_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    /**
     * Save CD loan to repository
     * @param cdLoan CD loan to save
     * @return true if save successful, false otherwise
     */
    public boolean save(CDLoan cdLoan) {
        if (cdLoan.getId() == null) {
            cdLoan.setId(generateId());
        }
        cdLoans.put(cdLoan.getId(), cdLoan);
        return saveCDLoans();
    }

    /**
     * Find CD loan by ID
     * @param id CD loan ID
     * @return CD loan or null if not found
     */
    public CDLoan findById(String id) {
        return cdLoans.get(id);
    }

    /**
     * Find CD loans by user ID
     * @param userId user ID
     * @return list of user's CD loans
     */
    public List<CDLoan> findByUserId(String userId) {
        return cdLoans.values().stream()
                .filter(loan -> userId.equals(loan.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Find CD loans by CD ID
     * @param cdId CD ID
     * @return list of CD's loans
     */
    public List<CDLoan> findByCDId(String cdId) {
        return cdLoans.values().stream()
                .filter(loan -> cdId.equals(loan.getCdId()))
                .collect(Collectors.toList());
    }

    /**
     * Find overdue CD loans
     * @return list of overdue CD loans
     */
    public List<CDLoan> findOverdueCDLoans() {
        return cdLoans.values().stream()
                .filter(loan -> !loan.isReturned())
                .filter(CDLoan::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Update CD loan in repository
     * @param cdLoan CD loan to update
     * @return true if update successful, false otherwise
     */
    public boolean update(CDLoan cdLoan) {
        if (cdLoans.containsKey(cdLoan.getId())) {
            cdLoans.put(cdLoan.getId(), cdLoan);
            return saveCDLoans();
        }
        return false;
    }

    /**
     * Get all CD loans
     * @return list of all CD loans
     */
    public List<CDLoan> findAll() {
        return new ArrayList<>(cdLoans.values());
    }
}