package client.utils.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageProcessorTest {

    @Test
    void getInterfaceImplementations() {
        var implementations = LanguageProcessor.getInterfaceImplementations();
        System.out.println(implementations);
        assertEquals(true,implementations.size() != 0);
    }
}