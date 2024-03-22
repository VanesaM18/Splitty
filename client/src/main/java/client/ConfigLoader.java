package client;

import client.utils.SceneEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConfigLoader {
    private Map<String, Object> configMap;
    private final ObjectMapper objectMapper;
    private final Path configPath;

    /**
     * Creates a class which loads the config file
     */
    public ConfigLoader() {
        this.configMap = new HashMap<>();
        this.configPath = getConfigFilePath();
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        loadConfig();
    }
    /**
        Saves the config at the specific path
    **/
    public void saveConfig() {
        try {
            Files.createDirectories(configPath.getParent());
            objectMapper.writeValue(configPath.toFile(), configMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Loads the config from the specific file path, if it does not exist it tries to create it
     */
    public void loadConfig() {
        try {
            if (!Files.exists(configPath)) {
                configMap.put("address", "http://localhost:8080");
                configMap.put("recentEvents", new ArrayList<String>());
                configMap.put("language", Main.DEFAULT_LOCALE);
                configMap.put("startUpShown", "false");
                saveConfig();
            } else {
                configMap = objectMapper.readValue(configPath.toFile(), new TypeReference<>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to get the config file from the specific folder
     * @return the path to the config file
     */
    private static Path getConfigFilePath() {
        String userHome = System.getProperty("user.home");
        String osName = System.getProperty("os.name").toLowerCase();
        Path configDir;

        if (osName.contains("win")) {
            configDir = Paths.get(System.getenv("APPDATA"), "Splitty");
        } else if (osName.contains("mac")) {
            configDir = Paths.get(userHome, "Library", "Application Support", "Splitty");
        } else {
            String configHome = System.getenv("XDG_CONFIG_HOME");
            if (configHome != null && !configHome.isEmpty()) {
                configDir = Paths.get(configHome, "Splitty");
            } else {
                configDir = Paths.get(userHome, ".config", "Splitty");
            }
        }

        return configDir.resolve("config.json");
    }

    /**
     * Get a specific property from the file
     * @param key the property
     * @return the value of the property
     */
    public Object getProperty(String key) {
        return configMap.get(key);
    }

    /**
     * Updates the config with a new (key, value) pair
     * @param key the key of the new property
     * @param value the value of the new property
     */
    public void updateProperty(String key, Object value) {
        configMap.put(key, value);
    }

    /**
     * gets the language stored in the config file and parses it to a Locale
     * @return the Locale of the language if parsing is possible,
     * the default Locale otherwise
     */
    public Locale getLanguage() {
        var props = this.getProperty("language");
        try {
            return (Locale) props;
        } catch (ClassCastException e) {
            try {
                String localeString = (String) props;
                var optionalLocale = parseLocale(localeString);
                if (optionalLocale.isPresent()) {
                    return optionalLocale.get();
                }
            } catch (ClassCastException ee) {

            }
        }
        return Main.DEFAULT_LOCALE;
    }

    private static Optional<Locale> parseLocale(String localeString) {
        String[] parts = localeString.split("_");
        if (parts.length == 2) {
            return Optional.of(new Locale(parts[0], parts[1]));
        }
        return Optional.empty();
    }

    /**
     * retrieves the initial scene to be displayed based on
     * the "startUpShown" property in the configuration.
     * if the "startUpShown" property is "false", returns the startup scene,
     * otherwise, returns the main start scene.
     * @return SceneEnum representing the initial scene to be displayed.
     */
    public SceneEnum getStartScene() {
        String startUpShown = (String) this.getProperty("startUpShown");
        return Objects.equals(startUpShown, "false") ? SceneEnum.STARTUP : SceneEnum.START;
    }
}