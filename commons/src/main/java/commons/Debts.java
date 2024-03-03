package commons;

import java.util.Objects;

public class Debts {
    private Participant debtor;
    private double amount;
    private Participant creditor;

    /**
     * Constructs debts
     * @param debtor the person who is in debt
     * @param amount the amount of money they are in debt
     * @param creditor the person who should receive money
     */
    public Debts(Participant debtor, double amount, Participant creditor) {
        this.debtor = debtor;
        this.amount = amount;
        this.creditor = creditor;
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
    public double getAmount() {
        return amount;
    }

    /**
     * Calculates the total amount of debt
     *
     * @param total total amount of debt
     * @return the total amount of debt
     */
    public double totalAmount(double total){
        return total;
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
        Debts debts = (Debts) o;
        return Double.compare(amount, debts.amount) == 0
                && Objects.equals(debtor, debts.debtor) && Objects.equals(creditor, debts.creditor);
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
     *
     * @return
     */
    @Override
    public String toString() {
        return  debtor + " gives" + amount + "to" + creditor;
    }
}
