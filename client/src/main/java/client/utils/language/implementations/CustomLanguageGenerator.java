package client.utils.language.implementations;

import client.MyFXML;
import client.utils.language.LanguageTemplate;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Locale;

public class CustomLanguageGenerator extends LanguageTemplate {

    /**
     * returns the text for the custom language option
     * @return text
     */
    @Override
    public String getText() {
        return "Download \n Template";
    }

    /**
     * getting the placeholder for the language
     * @return the language string.
     */
    @Override
    protected String getLanguage() {
        return "?";
    }

    /**
     * getting the placeholder method for the country.
     * @return country string.
     */
    @Override
    protected String getCountry() {
        return "?";
    }

    /**
     * initiates the language switching process.
     */
    @Override
    public void switchLanguage() {
        Locale locale = Locale.of("en","EN");
        String fileName = "language_template_" + locale.toString() + ".properties";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("");
        fileChooser.getExtensionFilters()
                .add(new FileChooser
                        .ExtensionFilter("Resource Bundles (*.properties)"
                        , "*.properties"));
        fileChooser.setInitialFileName(fileName);
        File selectedFile = fileChooser.showSaveDialog(null);
        new MyFXML(null).writeResourceBundleFile(locale,selectedFile);

        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("File chooser dialog canceled.");
        }
    }

    /**
     * method for keeping the position of
     * the language object
     *
     * @return position
     */
    @Override
    public int getPriority() {
        return 1;
    }
}
