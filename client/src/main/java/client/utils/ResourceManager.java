package client.utils;

import client.scenes.MainCtrl;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ResourceManager {
    private final MainCtrl mainCtrl;
    private ResourceBundle resourceBundle;

    /**
     * constructs a ResourceManager object.
     * @param mainCtrl the MainCtrl instance
     *                 associated with this ResourceManager
     */
    public ResourceManager (MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.findResourceBundle();
    }

    private ResourceBundle findResourceBundle() {
        Optional<Locale> currentLocale = mainCtrl.getCurrentLocale();
        Locale locale = currentLocale.orElse(Locale.of("en","EN"));
        try {
            this.resourceBundle = ResourceBundle.getBundle("bundles.Splitty", locale);
        } catch (Exception e) {
            this.resourceBundle = null;
        }
        return this.resourceBundle;
    }

    /**
     * gets the string value associated with
     * the given key from the resource bundle.
     * @param key the key for the desired string value
     * @return the string value associated with the
     * given key, or an empty string if the key is not found
     */
    public String getStringForKey(String key) {
        String string;
        try {
            string = resourceBundle.getString(key);
        } catch (Exception exception) {
            string = "";
        }
        return string;
    }
}
