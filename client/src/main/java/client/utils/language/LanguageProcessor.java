package client.utils.language;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class LanguageProcessor {

    public static <T> List<T> getInterfaceImplementations() {
        List<T> implementations = new ArrayList<>();
        var interfaceClass = Language.class;
        var packageName = "client/src/main/java/client/utils/language/implementations";

        try {
            // Get all classes in the given package
            ClassPathScanner classPathScanner = new ClassPathScanner(packageName);
            List<Class<?>> classes = classPathScanner.getClasses();

            // Iterate through classes and find implementations of the given interface
            for (Class<?> clazz : classes) {
                if (interfaceClass.isAssignableFrom(clazz) && !clazz.isInterface() && !clazz.isEnum()) {
                    try {
                        // Create an instance of the implementation
                        Constructor<?> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        T instance = (T) constructor.newInstance();
                        implementations.add(instance);
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace(); // Handle or log the exception as needed
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }

        return implementations;
    }

}
