package library.utils;


/**
 * Utility class for validation operations
 * @author Library Team
 * @version 1.0
 */
public class ValidationUtils {
    
    /**
     * Validate password strength
     * @param password password to validate
     * @return true if password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate ISBN format (basic validation)
     * @param isbn ISBN to validate
     * @return true if ISBN is valid, false otherwise
     */
    public static boolean isValidIsbn(String isbn) {
        return isbn != null && !isbn.trim().isEmpty();
    }
    
    /**
     * Validate name (not empty and reasonable length)
     * @param name name to validate
     * @return true if name is valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && name.length() >= 2 && name.length() <= 50;
    }
}