package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
public class Admin {
    @Id
    private String username;
    private String password;
    private String email;

    public Admin() {
        // for object mapper
    }
    /**
     * Create an Admin with the given details.
     *
     * @param username The username of the admin.
     * @param password The password of the admin.
     * @param email The email of the admin.
     */
    public Admin(String username, String password, String email) {
        this.username = username;
        PasswordHasher psw = new PasswordHasher();
        this.password = psw.compute(password);
        this.email = email;
    }

    /**
     * Get the username of the admin.
     *
     * @return the name of the admin.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Get the email of the admin.
     *
     * @return the email of the admin.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Get the password of the admin.
     *
     * @return the password of the admin.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     *  Checks if another object is equivalent to the current one
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generate a numerical hashCode based on the properties of this admin.
     *
     * @return a hashcode based on the properties of this admin.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Generate a string with the properties of this admin
     *
     * @return a string based on the properties of this admin.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
