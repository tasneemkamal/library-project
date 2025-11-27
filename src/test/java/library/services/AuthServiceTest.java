package library.services;



import library.models.User;
import library.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for AuthService
 * @author Library Team
 * @version 1.0
 */
@DisplayName("AuthService Tests")
class AuthServiceTest {
    private AuthService authService;
    private UserRepository userRepository;
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        securityService = mock(SecurityService.class);
        authService = new AuthService(userRepository, securityService);
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {
        @Test
        @DisplayName("Should register user successfully with valid data")
        void testRegisterUserSuccess() {
            // Arrange
            String name = "John Doe";
            String email = "john@example.com";
            String password = "password123";
            String role = "USER";
            String hashedPassword = "hashed_password";

            when(securityService.isValidEmail(email)).thenReturn(true);
            when(userRepository.findByEmail(email)).thenReturn(null);
            when(securityService.hashPassword(password)).thenReturn(hashedPassword);
            when(userRepository.save(any(User.class))).thenReturn(true);

            // Act
            boolean result = authService.register(name, email, password, role);

            // Assert
            assertTrue(result);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail registration with invalid email")
        void testRegisterUserInvalidEmail() {
            // Arrange
            String email = "invalid-email";
            when(securityService.isValidEmail(email)).thenReturn(false);

            // Act
            boolean result = authService.register("John", email, "password", "USER");

            // Assert
            assertFalse(result);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail registration with duplicate email")
        void testRegisterUserDuplicateEmail() {
            // Arrange
            String email = "existing@example.com";
            User existingUser = new User("Existing User", email, "hash", "USER");

            when(securityService.isValidEmail(email)).thenReturn(true);
            when(userRepository.findByEmail(email)).thenReturn(existingUser);

            // Act
            boolean result = authService.register("John", email, "password", "USER");

            // Assert
            assertFalse(result);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should fail registration with weak password")
        void testRegisterUserWeakPassword() {
            // Arrange
            String weakPassword = "123";

            when(securityService.isValidEmail(anyString())).thenReturn(true);
            when(userRepository.findByEmail(anyString())).thenReturn(null);

            // Act
            boolean result = authService.register("John", "john@test.com", weakPassword, "USER");

            // Assert
            assertFalse(result);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {
        @Test
        @DisplayName("Should login successfully with valid credentials")
        void testLoginSuccess() {
            // Arrange
            String email = "john@example.com";
            String password = "password123";
            String hashedPassword = "hashed_password";
            User user = new User("John Doe", email, hashedPassword, "USER");
            user.setActive(true);

            when(userRepository.findByEmail(email)).thenReturn(user);
            when(securityService.verifyPassword(password, hashedPassword)).thenReturn(true);

            // Act
            boolean result = authService.login(email, password);

            // Assert
            assertTrue(result);
            assertNotNull(authService.getCurrentUser());
        }

        @Test
        @DisplayName("Should fail login with invalid password")
        void testLoginInvalidPassword() {
            // Arrange
            String email = "john@example.com";
            String password = "wrongpassword";
            String hashedPassword = "hashed_password";
            User user = new User("John Doe", email, hashedPassword, "USER");

            when(userRepository.findByEmail(email)).thenReturn(user);
            when(securityService.verifyPassword(password, hashedPassword)).thenReturn(false);

            // Act
            boolean result = authService.login(email, password);

            // Assert
            assertFalse(result);
            assertNull(authService.getCurrentUser());
        }

        @Test
        @DisplayName("Should fail login with non-existent email")
        void testLoginNonExistentEmail() {
            // Arrange
            String email = "nonexistent@example.com";
            when(userRepository.findByEmail(email)).thenReturn(null);

            // Act
            boolean result = authService.login(email, "password");

            // Assert
            assertFalse(result);
            assertNull(authService.getCurrentUser());
        }

        @Test
        @DisplayName("Should fail login with inactive user")
        void testLoginInactiveUser() {
            // Arrange
            String email = "inactive@example.com";
            User user = new User("Inactive User", email, "hash", "USER");
            user.setActive(false);

            when(userRepository.findByEmail(email)).thenReturn(user);

            // Act
            boolean result = authService.login(email, "password");

            // Assert
            assertFalse(result);
            assertNull(authService.getCurrentUser());
        }
    }

    @Nested
    @DisplayName("Authentication State Tests")
    class AuthenticationStateTests {
        @Test
        @DisplayName("Should return correct login state")
        void testLoginState() {
            // Initially not logged in
            assertFalse(authService.isLoggedIn());
            assertNull(authService.getCurrentUser());

            // After login
            User user = new User("John", "john@test.com", "hash", "USER");
            when(userRepository.findByEmail(anyString())).thenReturn(user);
            when(securityService.verifyPassword(anyString(), anyString())).thenReturn(true);
            
            authService.login("john@test.com", "password");
            
            assertTrue(authService.isLoggedIn());
            assertNotNull(authService.getCurrentUser());
        }

        @Test
        @DisplayName("Should logout successfully")
        void testLogout() {
            // Login first
            User user = new User("John", "john@test.com", "hash", "USER");
            when(userRepository.findByEmail(anyString())).thenReturn(user);
            when(securityService.verifyPassword(anyString(), anyString())).thenReturn(true);
            
            authService.login("john@test.com", "password");
            assertTrue(authService.isLoggedIn());

            // Logout
            authService.logout();
            assertFalse(authService.isLoggedIn());
            assertNull(authService.getCurrentUser());
        }

        @Test
        @DisplayName("Should detect admin role correctly")
        void testAdminRole() {
            // Regular user
            User regularUser = new User("User", "user@test.com", "hash", "USER");
            when(userRepository.findByEmail("user@test.com")).thenReturn(regularUser);
            when(securityService.verifyPassword(anyString(), anyString())).thenReturn(true);
            
            authService.login("user@test.com", "password");
            assertFalse(authService.isAdmin());

            // Admin user
            User adminUser = new User("Admin", "admin@test.com", "hash", "ADMIN");
            when(userRepository.findByEmail("admin@test.com")).thenReturn(adminUser);
            
            authService.login("admin@test.com", "password");
            assertTrue(authService.isAdmin());
        }
    }
}