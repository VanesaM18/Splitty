package client.utils.language;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class LanguageProcessor {

    private final List<Language> languages;
    private final Map<String, Language> actions = new HashMap<>();;

    /**
     * constructs a LanguageProcessor instance.
     * initializes the LanguageProcessor by finding
     * all implementations of the Language interface
     * and creating corresponding actions for each language.
     */
    public LanguageProcessor() {
        this.languages = findInterfaceImplementations();
        languages.sort(Language::compareTo);
        createActions();
    }
    /**
     * Inject the languages by using the ClassPathScanner
     * @return list of language objects
     * @param <T> wildcard standing for the common class for the injected objects
     */
    private <T> List<T> findInterfaceImplementations() {
        List<T> implementations = new ArrayList<>();
        var interfaceClass = Language.class;
        var packageName = "client.utils.language.implementations";

        try {
            ClassPathScanner classPathScanner = new ClassPathScanner(packageName);
            List<Class<?>> classes = classPathScanner.getClasses();

            for (Class<?> clazz : classes) {
                if (interfaceClass.isAssignableFrom(clazz)
                        && !clazz.isInterface()
                        && !clazz.isEnum()) {
                    try {
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        T instance = (T) constructor.newInstance();
                        implementations.add(instance);
                    } catch (InstantiationException
                            | IllegalAccessException
                            | NoSuchMethodException
                            | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return implementations;
    }

    /**
     * gets the languages supported by the LanguageProcessor.
     * @return a list of Language objects representing the supported languages.
     */
    public List<Language> getLanguages() {
        return new ArrayList<>(languages);
    }

    private void createActions() {
        this.languages.forEach(language ->
                actions.put(language.getText(), language));
    }

    /**
     * gets the language actions supported by the LanguageProcessor.
     * map type params: the keys represent language identifiers,
     * and the values represent corresponding actions.
     * @return a map of language actions.
     */
    public Map<String, Runnable> getActions() {
        return new HashMap<>(actions);
    }

    /**
     * VBox for displaying the language buttons
     * @return VBox populated with language buttons
     */
    public VBox getButtons() {
        VBox root = new VBox(20);
        HBox bbox = new HBox(toArray(this.languages));
        bbox.setSpacing(10);
        root.getChildren().add(bbox);
        root.getChildren().add(new StackPane());
        return root;
    }

    /**
     * Helper method to transform the list of languages objects
     * to an array of corresponding buttons.
     * @param languages the list of languages
     * @return button array
     */
    public static Button[] toArray(List<Language> languages) {
        var  n = languages.size();
        Button[] array = new Button[n];
        int i = 0;
        for (Language language : languages) {
            array[i] = language.getButton();
            i += 1;
        }
        return array;
    }

    /**
     * scroll pane with all the language buttons.
     * @return ScrollPane containing language buttons.
     */
    public ScrollPane getLanguageSelector() {
        ScrollPane scrollPane = new ScrollPane();
        VBox languageButtons = new VBox(20);
        languageButtons.getChildren().addAll(toArray(languages));
        scrollPane.setContent(languageButtons);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }


    /**
     * populates a titled pane with all the language buttons.
     * @param titledPane titled pane to populate.
     * @param currentLocale current locale of the application.
     */
    public void populateTitledPane(TitledPane titledPane, Locale currentLocale) {
        titledPane.setPadding(new Insets(0,10,0,10));
        Language currentLanguage = this.languages
                .stream()
                .filter(language
                        -> language.getLocale().equals(currentLocale))
                .toList().get(0);
        Image img = currentLanguage.getFlag();
        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(23);
        imageView.setPreserveRatio(true);
        titledPane.setGraphic(imageView);
        VBox languageButtons = new VBox(20);
        Button[] languageArray = toArray(languages);
        for (Button button : languageArray) {
            if(button.getText().contains(currentLanguage.getText())) {
                button.setText(button.getText() + "\n (active)");
            }
        }
        languageButtons.getChildren().addAll(languageArray);
        titledPane.setContent(languageButtons);
        titledPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if(!oldValue && newValue && observable.getValue()) {
                titledPane.setPrefWidth(250);
                titledPane.setPrefHeight(300);
            } else if (oldValue && !newValue && !observable.getValue()) {
                titledPane.setPrefWidth(0);
                titledPane.setPrefHeight(0);
            }
        });
    }

}
