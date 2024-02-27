package commons;

import java.security.SecureRandom;
import java.util.*;

public class PasswordGenerator {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*_=+-/.?<>)";
    private static final String COMBINED = LOWERCASE + UPPERCASE + NUMBERS + SPECIAL_CHARACTERS;
    private final int length;
    private final SecureRandom random = new SecureRandom();
    /*
        For a password to be considered secure it should contain:
            - at least one uppercase letter
            - at least one lowercase letter
            - to have at least length 8
            - to have at least one special character
    */
    public PasswordGenerator(int length) {
        this.length = length;
    }
    public String generate() {
        StringBuilder create = new StringBuilder(this.length);

        create.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        create.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        create.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        create.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        for (int i = 4; i < this.length; ++i) {
            create.append(COMBINED.charAt(random.nextInt(COMBINED.length())));
        }

        String password = create.toString();
        List<Character> charArray = new ArrayList<>();
        for (char c : password.toCharArray()) {
            charArray.add(c);
        }

        int nrTimes = random.nextInt(5);
        while (nrTimes != 0) {
            Collections.shuffle(charArray);
            nrTimes--;
        }

        StringBuilder shuffledPassword = new StringBuilder();
        for (Character ch : charArray) {
            shuffledPassword.append(ch);
        }

        return shuffledPassword.toString();
    }
}
