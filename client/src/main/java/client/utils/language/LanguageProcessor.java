package client.utils.language;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class LanguageProcessor {

    public static <T> List<T> getInterfaceImplementations() {
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

    public static VBox getButtons() {
        VBox root = new VBox(20);
        HBox bbox = new HBox(toArray(getInterfaceImplementations()));
        root.getChildren().add(bbox);
        root.getChildren().add(new StackPane());
        return root;
    }

    public static Button[] toArray(List<Language> languages) {
        var n = languages.size();
        Button[] array = new Button[n];
        int i = 0;
        for (Language language : languages) {
            array[i] = language.getButton();
            i += 1;
        }
        return array;
    }
}
