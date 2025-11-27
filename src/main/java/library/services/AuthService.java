package library.services;



import library.models.User;
import library.repositories.UserRepository;
import library.utils.ValidationUtils;

/**
 * Service for handling authentication operations
 * @author Library Team
 * @version 1.0
 */
public class AuthService {
    private UserRepository userRepository;
    private SecurityService securityService;
    private User currentUser;

    public AuthService(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    /**
     * Register a new user
     * @param name user's name
     * @param email user's email
     * @param password user's password
     * @param role user's role
     * @return true if registration successful, false otherwise
     */
    public boolean register(String name, String email, String password, String role) {
        if (!securityService.isValidEmail(email)) {
            System.out.println("Invalid email format!");
            return false;
        }

        if (userRepository.findByEmail(email) != null) {
            System.out.println("User with this email already exists!");
            return false;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            System.out.println("Password must be at least 6 characters long!");
            return false;
        }

        String passwordHash = securityService.hashPassword(password);
        User user = new User(name, email, passwordHash, role);
        return userRepository.save(user);
    }

    /**
     * Login user
     * @param email user's email
     * @param password user's password
     * @return true if login successful, false otherwise
     */
    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.isActive()) {
            System.out.println("User not found or inactive!");
            return false;
        }

        if (securityService.verifyPassword(password, user.getPasswordHash())) {
            this.currentUser = user;
            System.out.println("Login successful! Welcome, " + user.getName());
            return true;
        } else {
            System.out.println("Invalid credentials!");
            return false;
        }
    }

    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser.getName() + "!");
            currentUser = null;
        }
    }

    /**
     * Get current logged in user
     * @return current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if current user is admin
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }
}