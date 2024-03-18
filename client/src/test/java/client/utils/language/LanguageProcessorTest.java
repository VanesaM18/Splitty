package client.utils.language;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LanguageProcessorTest {

    @Test
    void getInterfaceImplementations() {
        LanguageProcessor languageProcessor = new LanguageProcessor();
        var implementations = languageProcessor.getLanguages();
        System.out.println(implementations);
        assertEquals(true, implementations.size() != 0);
    }
}
