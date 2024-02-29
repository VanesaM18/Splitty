package server;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ContextConfigurator extends ServerEndpointConfig.Configurator {

    private static volatile ApplicationContext context;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ContextConfigurator.context = applicationContext;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return context.getBean(clazz);
    }
}
