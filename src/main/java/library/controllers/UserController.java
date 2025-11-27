package library.controllers;



import library.services.AuthService;
import library.repositories.UserRepository;
import library.models.User;
import java.util.List;

/**
 * Controller for user management operations
 * @author Library Team
 * @version 1.0
 */
public class UserController {
    private UserRepository userRepository;
    private AuthService authService;

    public UserController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    /**
     * View all users
     */
    public void viewAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n=== All Users ===");
        System.out.printf("%-20s %-25s %-10s %-10s%n", "Name", "Email", "Role", "Status");
        System.out.println("------------------------------------------------------------------------");
        
        for (User user : users) {
            String status = user.isActive() ? "Active" : "Inactive";
            System.out.printf("%-20s %-25s %-10s %-10s%n",
                user.getName(),
                user.getEmail(),
                user.getRole(),
                status);
        }
    }

    /**
     * Deactivate a user
     * @param userId user ID to deactivate
     */
    public void deactivateUser(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        if (!user.isActive()) {
            System.out.println("User is already inactive!");
            return;
        }

        user.setActive(false);
        boolean success = userRepository.save(user);
        if (success) {
            System.out.println("User deactivated successfully!");
        } else {
            System.out.println("Failed to deactivate user!");
        }
    }

    /**
     * Activate a user
     * @param userId user ID to activate
     */
    public void activateUser(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        if (user.isActive()) {
            System.out.println("User is already active!");
            return;
        }

        user.setActive(true);
        boolean success = userRepository.save(user);
        if (success) {
            System.out.println("User activated successfully!");
        } else {
            System.out.println("Failed to activate user!");
        }
    }

    /**
     * View user statistics
     */
    public void viewUserStatistics() {
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        long activeUsers = users.stream().filter(User::isActive).count();
        long adminUsers = users.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long regularUsers = users.stream().filter(u -> "USER".equals(u.getRole())).count();

        System.out.println("\n=== User Statistics ===");
        System.out.println("Total Users: " + totalUsers);
        System.out.println("Active Users: " + activeUsers);
        System.out.println("Admin Users: " + adminUsers);
        System.out.println("Regular Users: " + regularUsers);
        System.out.println("Inactive Users: " + (totalUsers - activeUsers));
    }
}