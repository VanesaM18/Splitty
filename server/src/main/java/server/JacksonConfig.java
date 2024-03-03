package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * creates and configures the Jackson ObjectMapper bean
     * @return configured ObjectMapper bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}