/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

public class MyFXML {

    private Injector injector;

    /**
     * Creates a class which injects our UI components
     * @param injector the injector used for injecting
     */
    public MyFXML(Injector injector) {
        this.injector = injector;
    }


    /**
     * Loads a FXML file specified by the given class and path parts with localization support
     *
     * @param c The class of the controller associated with the FXML file.
     * @param locale The locale to use for loading resource bundles.
     * @param parts The parts of the path to the FXML file
     * @param <T> The type of the controller class.
     * @return The loaded component
     * @throws RuntimeException if the FXML file cannot be loaded.
     */
    public <T> Pair<T, Parent> load(Class<T> c, Locale locale, String... parts) {
        try {
            var loader =
                    new FXMLLoader(
                            getLocation(parts),
                            null,
                            null,
                            new MyFactory(),
                            StandardCharsets.UTF_8);
            loader.setResources(ResourceBundle.getBundle("bundles.Splitty", locale));
            Parent parent = loader.load();
            T ctrl = loader.getController();
            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private ResourceBundle getBundle(Locale locale) {
        var bundle = ResourceBundle.getBundle("bundles.Splitty", locale);
        return bundle;
    }

    /**
     * Constructs the URL for the location of the FXML file based on the given path parts.
     *
     * @param parts The parts of the path
     * @return The URL pointing to the location of the FXML file.
     */
    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return MyFXML.class.getClassLoader().getResource(path);
    }

    private class MyFactory implements BuilderFactory, Callback<Class<?>, Object> {

        @Override
        @SuppressWarnings("rawtypes")
        public Builder<?> getBuilder(Class<?> type) {
            return new Builder() {
                @Override
                public Object build() {
                    return injector.getInstance(type);
                }
            };
        }

        @Override
        public Object call(Class<?> type) {
            return injector.getInstance(type);
        }
    }
}
