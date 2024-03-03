package commons;

import java.util.Objects;

public class Debts {
    private Participant debtor;
    private double amount;
    private Participant creditor;

    public Debts(Participant debtor, double amount, Participant creditor) {
        this.debtor = debtor;
        this.amount = amount;
        this.creditor = creditor;
    }

    public Participant getDebtor() {
        return debtor;
    }

    public double getAmount() {
        return amount;
    }

    public double totalAmount(double total){
        return total;
    }

    public Participant getCreditor() {
        return creditor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debts debts = (Debts) o;
        return Double.compare(amount, debts.amount) == 0 && Objects.equals(debtor, debts.debtor) && Objects.equals(creditor, debts.creditor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(debtor, amount, creditor);
    }

    @Override
    public String toString() {
        return  debtor + " gives" + amount + "to" + creditor;
    }
}
