package commons;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Admin {
    @Id
    public String username;
    public String password;
    public String email;
    private Admin() {
        // for object mapper
    }

    public Admin(String username, String password, String email) {
        this.username = username;
        PasswordHasher psw = new PasswordHasher();
        this.password = psw.compute(password);
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}