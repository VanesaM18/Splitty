package client;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties configProperties;

    /**
     * Creates a class which loads the config file
     */
    public ConfigLoader() {
        loadProperties();
    }

    /**
     * Parses the file with the properties
     */
    private void loadProperties() {
        try (InputStream input =
                 getClass().getClassLoader().getResourceAsStream("config.properties")) {
            configProperties = new Properties();

            if (input == null) {
                System.out.println("Unable to find config.properties");
                return;
            }

            configProperties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a specific property from the file
     * @param key the property
     * @return the value of the property
     */
    public String getProperty(String key) {
        return configProperties.getProperty(key);
    }
}