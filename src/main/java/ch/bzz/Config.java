package ch.bzz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Properties;

public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private static Properties properties;

    public static Properties loadConfiguration() {
        if (properties != null) {
            return properties;
        }

        properties = new Properties();

        // 1) JVM system property override: -Dconfig.file=path/to/config.properties
        String overridePath = System.getProperty("config.file");
        if (overridePath == null || overridePath.isBlank()) {
            // 2) Environment variable override: CONFIG_FILE=path/to/config.properties
            overridePath = System.getenv("CONFIG_FILE");
        }

        File configFile = null;
        if (overridePath != null && !overridePath.isBlank()) {
            configFile = new File(overridePath);
        } else {
            // 3) Current working directory
            File cwdCandidate = new File("config.properties");
            if (cwdCandidate.exists()) {
                configFile = cwdCandidate;
            } else {
                try {
                    // 4) Next to the running JAR (or classes dir in dev)
                    File codeSource = new File(Config.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());
                    File baseDir = codeSource.isFile() ? codeSource.getParentFile() : codeSource;
                    File jarSibling = new File(baseDir, "config.properties");
                    if (jarSibling.exists()) {
                        configFile = jarSibling;
                    }
                } catch (Exception ignored) {
                    // Fallback continues below
                }
            }
        }

        if (configFile == null) {
            log.error("Error loading config.properties: file not found in any known location");
            log.error("Provide via -Dconfig.file=... or CONFIG_FILE env var, or place config.properties in the working directory or next to the JAR.");
            return null;
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            log.info("Successfully loaded configuration from: {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error loading config.properties: {}", e.getMessage(), e);
            log.error("Tried path: {}", configFile.getAbsolutePath());
            return null;
        }
        return properties;
    }

    public static String getProperty(String key) {
        Properties config = loadConfiguration();
        return config != null ? config.getProperty(key) : null;
    }

    public static String getProperty(String key, String defaultValue) {
        Properties config = loadConfiguration();
        return config != null ? config.getProperty(key, defaultValue) : defaultValue;
    }
}

