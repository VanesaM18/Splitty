package server;

import java.util.Base64;
import java.util.Objects;

/**
 * This clas can be used to parse basic authentication headers.
 */
public class BasicAuthParser {

    /**
     * This method can be used to parse basic authentication headers.
     *
     * @param header the basic authentication header, should not contain the "Authorization: " part.
     * @return the parsed username and password or null if the header is invalid
     */
    public static UsernamePassword parse(String header) {
        if (header == null) {
            return null;
        }

        String[] parts = header.split(" ");
        if (parts.length != 2) {
            return null;
        }
        if (!"Basic".equals(parts[0])) {
            return null;
        }
        String[] credentials = new String(Base64.getDecoder().decode(parts[1])).split(":");
        if (credentials.length != 2) {
            return null;
        }

        return new UsernamePassword(credentials[0], credentials[1]);
    }

    public static class UsernamePassword {
        private final String username;
        private final String password;

        /**
         * A username and password pair.
         *
         * @param username the username
         * @param password the password
         */
        public UsernamePassword(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Get the username.
         *
         * @return the username
         */
        public String getUsername() {
            return username;
        }

        /**
         * Get the password.
         *
         * @return the password
         */
        public String getPassword() {
            return password;
        }

        /**
         * Generate a string representation of this username password pair
         *
         * @return a string representation
         */
        @Override
        public String toString() {
            return "UsernamePassword{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

        /**
         * Checks if another object is equivalent to the current one.
         *
         * @param o the object to compare against
         * @return true if the objects are equivalent
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsernamePassword that = (UsernamePassword) o;
            return Objects.equals(getUsername(), that.getUsername())
                    && Objects.equals(getPassword(), that.getPassword());
        }

        /**
         * Calculates a hash code based on the username and password.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            return Objects.hash(getUsername(), getPassword());
        }
    }

}
