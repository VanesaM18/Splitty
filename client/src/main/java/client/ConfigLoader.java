package client;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties configProperties;

    public ConfigLoader() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
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

    public String getProperty(String key) {
        return configProperties.getProperty(key);
    }
}