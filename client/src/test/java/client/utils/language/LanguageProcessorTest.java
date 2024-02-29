package client.utils.language;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LanguageProcessorTest {

    @Test
    void getInterfaceImplementations() {
        var implementations = LanguageProcessor.getInterfaceImplementations();
        System.out.println(implementations);
        assertEquals(true, implementations.size() != 0);
    }
}
