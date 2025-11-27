package library.services;


import org.mindrot.jbcrypt.BCrypt;

/**
 * Service for handling security operations like password hashing and verification
 * @author Library Team
 * @version 1.0
 */
public class SecurityService {
    
    /**
     * Hash a password using BCrypt
     * @param password the plain text password
     * @return the hashed password
     */
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    /**
     * Verify a password against a hash
     * @param password the plain text password
     * @param hashedPassword the hashed password
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate email format
     * @param email the email to validate
     * @return true if email is valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }
}
