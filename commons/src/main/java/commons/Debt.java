package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Debt {
    @Id @GeneratedValue private long id;
    @ManyToOne private Participant debtor;
    private Monetary amount;
    @ManyToOne private Participant creditor;

    /**
     * Constructs debts
     * @param debtor the person who is in debt
     * @param amount the amount of money they are in debt
     * @param creditor the person who should receive money
     */
    public Debt(Participant debtor, Monetary amount, Participant creditor) {
        this.debtor = debtor;
        this.amount = amount;
        this.creditor = creditor;
    }

    /**
     * empty constructor
     */
    public Debt() {

    }

    /**
     * Gets the debtor
     *
     * @return the participant in debt
     */
    public Participant getDebtor() {
        return debtor;
    }

    /**
     * Gets the amount
     *
     * @return the amount of debt
     */
    public Monetary getAmount() {
        return amount;
    }


    /**
     * Gets the creditor
     *
     * @return the participant who is owed money
     */
    public Participant getCreditor() {
        return creditor;
    }

    /**
     * Checks whether to debts are the same
     *
     * @param o object to compare it to debt
     * @return true if the two objects/debts are the same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return Objects.equals(debtor, debt.debtor) && Objects.equals(amount, debt.amount)
                && Objects.equals(creditor, debt.creditor);
    }

    /**
     * gets the debt id
     * @return id of debts
     */
    public long getId(){
        return id;
    }

    /**
     * Generates a hashcode for the object
     *
     * @return a hashcode for the debt object
     */
    @Override
    public int hashCode() {
        return Objects.hash(debtor, amount, creditor);
    }

    /**
     * Converts debt into a readable string format
     *
     * @return a string of the debt
     */
    @Override
    public String toString() {
        return  debtor.getName() + " gives " + amount.getInternalValue() + " to " + creditor.getName();
    }
}
