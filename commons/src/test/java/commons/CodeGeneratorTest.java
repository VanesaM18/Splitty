package commons;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

class CodeGeneratorTest {

    CodeGenerator generator;

    @BeforeEach
    void setup() {
        SecureRandom random = new SecureRandom(new byte[] {1, 2});
        generator = new CodeGenerator(random);
    }

    @Test
    void generateCode_negativeLength_throwsError() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateCode(-1));
    }

    @Test
    void generateCode_positiveLength_codeIsAlphanumeric() {
        String code = generator.generateCode(100);

        final String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (char ch : code.toCharArray()) {
            assertTrue(allowedChars.contains(String.valueOf(ch)));
        }
    }

    @Test
    void generateCode_positiveLength_codeIsExpectedLength() {
        String code = generator.generateCode(1);
        assertEquals(1, code.length());

        code = generator.generateCode(6);
        assertEquals(6, code.length());

        code = generator.generateCode(12);
        assertEquals(12, code.length());
    }

    @Test
    void generateCode_multipleCalls_codesAreDifferent() {
        // NOTE: The generator is created using a SecureRandom object with a set seed to prevent
        // this
        // test from failing
        // sometimes in some insanely rare circumstances.
        String code1 = generator.generateCode(12);
        String code2 = generator.generateCode(12);

        assertNotEquals(code1, code2);
    }
}
