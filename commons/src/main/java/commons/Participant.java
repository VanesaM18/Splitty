package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String email;
    private String iban;
    private String bic;

    /**
     * Creates a participant of an event.
     * @param name the participant's name.
     * @param email the participant's email for contact purposes.
     * @param iban the participant's iban - needed for transferring money.
     * @param bic the participant's bic - also needed for transferring money.
     */
    public Participant(String name, String email, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    /**
     * Creates a participant.
     * Used for object mapping.
     */
    public Participant() {
        // for object mapper
    }

    /**
     * Retrieves the id associated with this participant.
     * @return the participant's name.
     */
    public long getId() { return id; };

    /**
     * Retrieves the name associated with this participant.
     * @return the participant's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the email associated with this participant.
     * @return the participant's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retrieves the IBAN associated with this participant.
     * @return the participant's IBAN.
     */
    public String getIban() {
        return iban;
    }

    /**
     * Retrieves the BIC associated with this participant.
     * @return the participant's BIC.
     */
    public String getBic() {
        return bic;
    }

    /**
     * Sets the id associated with this participant.
     * @param id The new id to be set.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the name associated with this participant.
     * @param name The new name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the email associated with this participant.
     * @param email The new email address to be set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the IBAN associated with this participant.
     * @param iban The new IBAN to be set.
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Sets the BIC associated with this participant.
     * @param bic The new BIC to be set.
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * Checks if another object is exactly the same as this one.
     * @param o Object that needs to be checked.
     * @return Weather the given object is equal to this one.
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    /**
     * Computes a hash code for this object.
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, email, iban, bic);
    }

    /**
     * Turns the participant into a string.
     * @return String representation of the object.
     */
    @Override
    public String toString() { return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE); }

}
