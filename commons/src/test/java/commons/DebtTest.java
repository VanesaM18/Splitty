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
    void testEqualsDifferent() {
        Participant debtor1 = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor1 = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Monetary monetary1 = new Monetary(10);

        Participant debtor2 = new Participant("Bob", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor2 = new Participant("Alice", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Monetary monetary2 = new Monetary(10);

        Debt debt1 = new Debt(creditor1, monetary1, debtor1);
        Debt debt2 = new Debt(creditor2, monetary2, debtor2);

        assertNotEquals(debt1, debt2);
    }

    @Test
    void testEqualsEqual() {
        Participant debtor1 = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor1 = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Monetary monetary1 = new Monetary(10);

        Participant debtor2 = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor2 = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Monetary monetary2 = new Monetary(10);

        Debt debt1 = new Debt(creditor1, monetary1, debtor1);
        Debt debt2 = new Debt(creditor1, monetary2, debtor1);

        assertEquals(debt1, debt2);
    }

    @Test
    void testEqualsSame() {
        Participant debtor1 = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor1 = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Monetary monetary1 = new Monetary(10);

        Debt debt1 = new Debt(creditor1, monetary1, debtor1);

        assertEquals(debt1, debt1);
    }

    @Test
    void testHashcode() {
        Participant debtor1 = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor1 = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Monetary monetary1 = new Monetary(10);

        Participant debtor2 = new Participant("Tom", "tom23@gmail.com", "NL22345518923056", "LAUGH");
        Participant creditor2 = new Participant("John", "crew@gmail.com", "NL99128381301324", "FUNNY");
        Monetary monetary2 = new Monetary(10);

        Debt debt1 = new Debt(creditor1, monetary1, debtor1);
        Debt debt2 = new Debt(creditor1, monetary2, debtor1);

        assertEquals(debt1.hashCode(), debt2.hashCode());
    }

    @Test
    void testToString() {
        String toStringTest = "Tom gives 100 to John";
        System.out.println(toStringTest);
        System.out.printf(debt.toString());
        assertTrue(debt.toString().equals(toStringTest));

    }
}
