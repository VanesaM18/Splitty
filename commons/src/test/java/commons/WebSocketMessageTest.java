package commons;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebSocketMessageTest {
    WebSocketMessage message = new WebSocketMessage();

    @Test
    public void endpoint() {
        message.setEndpoint("endpoint");
        assertEquals("endpoint", message.getEndpoint());
    }

    @Test
    public void method() {
        message.setMethod("method");
        assertEquals("method", message.getMethod());
    }

    @Test
    public void id() {
        message.setId("id");
        assertEquals("id", message.getId());
    }

    @Test
    public void auth() {
        message.setAuthHeader("auth");
        assertEquals("auth", message.getAuthHeader());
    }

    @Test
    public void data() {
        message.setData("obj");
        assertEquals("obj", message.getData());
    }

    @Test
    public void parameters() {
        message.setParameters(List.of("param1"));
        assertEquals(List.of("param1"), message.getParameters());
    }

    @Test
    public void string() {
        assertEquals(message.toString(), message.toString());
    }
}
