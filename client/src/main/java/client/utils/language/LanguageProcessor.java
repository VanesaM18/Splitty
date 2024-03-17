package client.utils.language;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageProcessor {

    private final List<Language> languages;
    private final Map<String, Language> actions = new HashMap<>();;

    public LanguageProcessor() {
        this.languages = findInterfaceImplementations();
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

    public List<Language> getLanguages() {
        return new ArrayList<>(languages);
    }

    private void createActions() {
        this.languages.forEach(language ->
                actions.put(language.getText(), language));
    }

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

}
