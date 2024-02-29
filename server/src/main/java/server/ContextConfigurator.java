package server;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ContextConfigurator extends ServerEndpointConfig.Configurator {

    private static volatile ApplicationContext context;

    /**
     * Sets the spring context
     * @param applicationContext the context to be set
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        ContextConfigurator.context = applicationContext;
    }

    /**
     * Gets any bean from the current application context
     * @param clazz the class of the endpoint
     * @return the bean
     * @param <T> the class type
     * @throws InstantiationException if the class is not found
     */
    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return context.getBean(clazz);
    }
}
