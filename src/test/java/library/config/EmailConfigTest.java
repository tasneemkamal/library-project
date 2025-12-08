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
    }

    @Test
    void testGetProperties() {
        EmailConfig config = new EmailConfig();
        config.setUsername("test@gmail.com");
        config.setPassword("123456");

        Properties props = config.getProperties();

        assertEquals("smtp.gmail.com", props.getProperty("mail.smtp.host"));
        assertEquals("587", props.getProperty("mail.smtp.port"));
        assertEquals("true", props.getProperty("mail.smtp.auth"));
        assertEquals("false", props.getProperty("mail.smtp.ssl.enable"));
        assertEquals("true", props.getProperty("mail.smtp.starttls.enable"));
        assertEquals("smtp.gmail.com", props.getProperty("mail.smtp.ssl.trust"));
    }

    @Test
    void testIsValid_FalseWhenMissingFields() {
        EmailConfig config = new EmailConfig();

        // defaults → username = null , password = null
        assertFalse(config.isValid());
    }

    @Test
    void testIsValid_TrueWhenAllFieldsPresent() {
        EmailConfig config = new EmailConfig();
        config.setUsername("user@test.com");
        config.setPassword("pass123");

        assertTrue(config.isValid());
    }

    @Test
    void testSettersAndGetters() {
        EmailConfig config = new EmailConfig();

        config.setHost("example.com");
        config.setPort("465");
        config.setUsername("u");
        config.setPassword("p");
        config.setEnableSSL(true);
        config.setEnableTLS(false);

        assertEquals("example.com", config.getHost());
        assertEquals("465", config.getPort());
        assertEquals("u", config.getUsername());
        assertEquals("p", config.getPassword());
        assertTrue(config.isEnableSSL());
        assertFalse(config.isEnableTLS());
    }
}

