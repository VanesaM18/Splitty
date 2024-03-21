package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebtTest {
    Participant debtor = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
    Participant creditor = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
    Monetary monetary = new Monetary(100);
    Debt debt = new Debt(debtor, monetary, creditor);

    @Test
    void getDebtor() {
        Participant tom = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");

        assertTrue(debt.getDebtor().equals(tom));

    }

    @Test
    void getAmount() {
        Monetary monetary1 = new Monetary(100);
        assert(debt.getAmount().equals(monetary1));
    }

    @Test
    void getCreditor() {
        Participant john = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");

        assertTrue(debt.getCreditor().equals(john));

    }

    @Test
    void testEquals() {
        Participant debtor = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Debt debt2 = new Debt(creditor, monetary, debtor);
        assertNotEquals(debt, debt2);
    }


    @Test
    void testToString() {
        String toStringTest = "Tom gives 100 to John";
        System.out.println(toStringTest);
        System.out.printf(debt.toString());
        assertTrue(debt.toString().equals(toStringTest));

    }
}