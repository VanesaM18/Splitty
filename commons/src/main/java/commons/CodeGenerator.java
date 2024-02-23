package commons;

import java.util.Random;

/**
 * This class provides a method for the generation of a random alphanumeric string of a given length.
 */
public class CodeGenerator {

    private final static String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final Random random;

    public CodeGenerator(Random random) {
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
