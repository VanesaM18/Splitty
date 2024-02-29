package client.utils.language;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassPathScanner {
    private final String packageName;

    /**
     * constructs a new ClassPathScanner object
     * @param packageName name of the package
     */
    public ClassPathScanner(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Gets classes present
     * @return the list of classes present
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public List<Class<?>> getClasses() throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * Finds the existent classes in a package
     * @param directory directory
     * @param packageName package name
     * @return list of found classes found in the provided package
     * @throws ClassNotFoundException
     */
    private List<Class<?>> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(
                        Class.forName(
                                packageName
                                        + '.'
                                        + file.getName()
                                                .substring(0, file.getName().length() - 6)));
            }
        }

        return classes;
    }
}
