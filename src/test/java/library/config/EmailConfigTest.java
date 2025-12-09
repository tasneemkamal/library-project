package library.config;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class EmailConfigTest {

    @Test
    void testDefaultConstructor() {
        EmailConfig config = new EmailConfig();

        assertEquals("smtp.gmail.com", config.getHost());
        assertEquals("587", config.getPort());
        assertFalse(config.isEnableSSL());
        assertTrue(config.isEnableTLS());

        // username + password should be null by default
        assertNull(config.getUsername());
        assertNull(config.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        EmailConfig config = new EmailConfig();

        config.setHost("example.com");
        config.setPort("465");
        config.setUsername("user@test.com");
        config.setPassword("pass123");
        config.setEnableSSL(true);
        config.setEnableTLS(false);

        assertEquals("example.com", config.getHost());
        assertEquals("465", config.getPort());
        assertEquals("user@test.com", config.getUsername());
        assertEquals("pass123", config.getPassword());
        assertTrue(config.isEnableSSL());
        assertFalse(config.isEnableTLS());
    }

    @Test
    void testSetPortInt() {
        EmailConfig config = new EmailConfig();
        config.setPort(2525);

        assertEquals("2525", config.getPort());
    }

    @Test
    void testGetProperties() {
        EmailConfig config = new EmailConfig();

        config.setUsername("test@gmail.com");
        config.setPassword("123456");

        Properties p = config.getProperties();

        assertEquals("smtp.gmail.com", p.getProperty("mail.smtp.host"));
        assertEquals("587", p.getProperty("mail.smtp.port"));
        assertEquals("true", p.getProperty("mail.smtp.auth"));
        assertEquals("false", p.getProperty("mail.smtp.ssl.enable"));
        assertEquals("true", p.getProperty("mail.smtp.starttls.enable"));
        assertEquals("smtp.gmail.com", p.getProperty("mail.smtp.ssl.trust"));
    }

    @Test
    void testIsValid_False() {
        EmailConfig config = new EmailConfig();

        // defaults: username=null, password=null → invalid
        assertFalse(config.isValid());
    }

    @Test
    void testIsValid_True() {
        EmailConfig config = new EmailConfig();
        config.setHost("smtp.gmail.com");
        config.setPort("587");
        config.setUsername("u");
        config.setPassword("p");

        assertTrue(config.isValid());
    }
}
