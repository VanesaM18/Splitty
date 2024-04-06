package client.utils;

import client.*;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.JavaFXInterceptorUtils.JavaFxInterceptor;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.Map;

import static com.google.inject.Guice.createInjector;

/**
 * Utility class for integration testing the client. ServerUtils is mocked so we
 * don't have to start the server as well.
 * Look at {@link client.integration.StartupTest} for an example of how to use.
 */
@ExtendWith(ApplicationExtension.class)
@ExtendWith(JavaFxInterceptor.class)
public class TestClient {

    protected Injector injector;
    protected ServerUtils serverUtils;
    protected Stage stage;

    /**
     * Setup. Runs before any test has executed.
     */
    @BeforeAll
    static void setup() {
        // Make sure the tests are run headless, since the CI server does not have a WM.
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * Start method
     * 
     * @param stage Injected by JUnit
     */
    @Start
    public void start(Stage stage) {
        var configLoader = new ConfigLoader(Map.of(
                "address", "http://localhost:8080",
                "recentEvents", new ArrayList<String>(),
                "language", Main.DEFAULT_LOCALE,
                "startUpShown", "false",
                "email", "",
                "password", "")) {
            /**
             * Override to avoid messing up local config
             */
            @Override
            public void saveConfig() {
            }
        };
        serverUtils = Mockito.mock(ServerUtils.class);
        injector = createInjector(new com.google.inject.Module() {
            @Override
            public void configure(Binder binder) {
                new MyModule().configure(binder);
            }

            @Provides
            @Singleton
            public ServerUtils provideServerUtils() {
                return serverUtils;
            }

            @Provides
            @Singleton
            public ConfigLoader provideConfigLoader() {
                return configLoader;
            }
        });
        this.stage = stage;
    }
}
