package commons;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class DebtsTest {
    Participant debtor = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
    Participant creditor = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
    Debts debt = new Debts(debtor, 100, creditor);

    @Test
    void getDebtor() {
        Participant tom = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");

        assertTrue(debt.getDebtor().equals(tom));

    }

    @Test
    void testEquals() {
        Participant debtor = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Debts debt2 = new Debts(creditor, 100, debtor);
        assertNotEquals(debt, debt2);
    }
}