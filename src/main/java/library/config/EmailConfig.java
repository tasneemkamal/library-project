package library.config;

import java.util.Properties;

/**
 * Configuration class for email settings
 * @author Library Team
 * @version 1.0
 */
public class EmailConfig {
    private String host;
    private String port;
    private String username;
    private String password;
    private boolean enableSSL;
    private boolean enableTLS;

    public EmailConfig() {
        // Default configuration for Gmail
        this.host = "smtp.gmail.com";
        this.port = "587";
        this.enableSSL = false;
        this.enableTLS = true;
    }

    /**
     * Get email properties
     * @return properties for email configuration
     */
    public Properties getProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", String.valueOf(enableSSL));
        props.put("mail.smtp.starttls.enable", String.valueOf(enableTLS));
        props.put("mail.smtp.ssl.trust", host);
        return props;
    }

    /**
     * Validate email configuration
     * @return true if configuration is valid, false otherwise
     */
    public boolean isValid() {
        return host != null && !host.trim().isEmpty() &&
               port != null && !port.trim().isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }

    // Getters and setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    // 🔥🔥🔥 إضافة الدالة المطلوبة بدون التأثير على أي شيء
    public void setPort(int port) { 
        this.port = String.valueOf(port);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnableSSL() { return enableSSL; }
    public void setEnableSSL(boolean enableSSL) { this.enableSSL = enableSSL; }

    public boolean isEnableTLS() { return enableTLS; }
    public void setEnableTLS(boolean enableTLS) { this.enableTLS = enableTLS; }
}
