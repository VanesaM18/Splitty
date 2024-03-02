package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicAuthParserTest {

    @Test
    void parse() {
        String header = "Basic QWxhZGRpbjpvcGVuc2VzYW1l";
        BasicAuthParser.UsernamePassword correct = new BasicAuthParser.UsernamePassword("Aladdin", "opensesame");

        BasicAuthParser.UsernamePassword parsed = BasicAuthParser.parse(header);
        assertEquals(correct, parsed);
    }
}