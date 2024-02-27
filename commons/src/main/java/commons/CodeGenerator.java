package commons;

import java.security.SecureRandom;

/**
 * This class provides a method for the generation of a random alphanumeric string of a given
 * length.
 */
public class CodeGenerator {

    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final SecureRandom random;

    /**
     * Create a new CodeGenerator.
     * This classes uses the supplied SecureRandom to generate random alphanumeric codes.
     *
     * @param random the RNG to use for code generation.
     */
    public CodeGenerator(SecureRandom random) {
        this.random = random;
    }

    /**
     * Generate an alphanumeric code of the given length.
     *
     * @param length the length of the code
     * @return the randomly generated code
     */
    public String generateCode(int length) {
        if (length <= 0) throw new IllegalArgumentException("length must be > 0");

        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(characters.length());
            char ch = characters.charAt(idx);

            result.append(ch);
        }

        return result.toString();
    }
}
