package commons;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    /*
       Uses SHA-256 algorithm to encrypt the password for saving in the database
    */
    private final MessageDigest encrypt;

    /**
     * Creates a class used for password hashing
     */
    public PasswordHasher() {
        try {
            this.encrypt = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Encrypts a password
     * @param password the password to be encrypted
     * @return the encrypted password
     */
    public String compute(String password) {
        byte[] encodedHash = encrypt.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder encrypted = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                encrypted.append('0');
            }
            encrypted.append(hex);
        }
        return encrypted.toString();
    }
}
