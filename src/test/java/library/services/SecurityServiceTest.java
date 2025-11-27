package library.services;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SecurityService
 * @author Library Team
 * @version 1.0
 */
@DisplayName("SecurityService Tests")
class SecurityServiceTest {
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService();
    }

    @Test
    @DisplayName("Should hash and verify password correctly")
    void testPasswordHashingAndVerification() {
        // Arrange
        String password = "mySecurePassword123";

        // Act
        String hashedPassword = securityService.hashPassword(password);
        boolean verificationResult = securityService.verifyPassword(password, hashedPassword);

        // Assert
        assertNotNull(hashedPassword);
        assertNotEquals(password, hashedPassword);
        assertTrue(verificationResult);
    }

    @Test
    @DisplayName("Should reject wrong password")
    void testWrongPasswordVerification() {
        // Arrange
        String originalPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hashedPassword = securityService.hashPassword(originalPassword);

        // Act
        boolean verificationResult = securityService.verifyPassword(wrongPassword, hashedPassword);

        // Assert
        assertFalse(verificationResult);
    }

    @Test
    @DisplayName("Should validate correct email formats")
    void testValidEmailFormats() {
        // Valid emails
        assertTrue(securityService.isValidEmail("test@example.com"));
        assertTrue(securityService.isValidEmail("user.name@domain.co.uk"));
        assertTrue(securityService.isValidEmail("user+tag@example.org"));
    }

    @Test
    @DisplayName("Should reject invalid email formats")
    void testInvalidEmailFormats() {
        // Invalid emails
        assertFalse(securityService.isValidEmail("invalid-email"));
        assertFalse(securityService.isValidEmail("user@"));
        assertFalse(securityService.isValidEmail("@domain.com"));
        assertFalse(securityService.isValidEmail(""));
        assertFalse(securityService.isValidEmail(null));
    }
}