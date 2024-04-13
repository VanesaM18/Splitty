package client.utils;

import client.ConfigLoader;
import client.MyWebSocketClient;
import commons.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerUtilsTest {

    private static final int INVITE_CODE_LENGTH = 6;

    private static final Function<Integer, List<Event>> func = (total) -> IntStream.range(0, total)
            .mapToObj(i -> generateRandomEvent())
            .collect(Collectors.toList());

    @Test
    void getEvent() {
        int eventCount = (int) (Math.random() * 100);

        var pz = func.apply(1).get(0);
        var inviteCode = pz.getInviteCode();
        var wsm = new WebSocketMessage();
        wsm.setData(pz);
        var future = CompletableFuture.supplyAsync(() -> wsm);
        MyWebSocketClient wap = mock(MyWebSocketClient.class);
        ConfigLoader cnf = mock(ConfigLoader.class);
        when(wap.addPendingRequests(any())).thenReturn(future);
        ServerUtils severe = new ServerUtils(wap, cnf);
        ServerUtils.adminAuth(new Admin("", "", ""));
        var response = severe.getEventById(inviteCode);
        assertNotNull(response);
        var responseId = response.getInviteCode();
        assertEquals(inviteCode, responseId);

    }

    /**
     * method to create and send a random event.
     * 
     * @return randomly generated event.
     */
    public static Event generateRandomEvent() {
        String randomInviteCode = UUID.randomUUID().toString().substring(0, INVITE_CODE_LENGTH);
        String randomName = "Random Event " + UUID.randomUUID().toString().substring(0, 5);
        LocalDateTime randomDateTime = LocalDateTime.now().plusDays((long) (Math.random() * 30));
        Set<Participant> randomParticipants = generateRandomParticipants();

        Event randomEvent = new Event(randomInviteCode, randomName, randomDateTime, randomParticipants,
                new HashSet<>());
        randomEvent.generateInviteCode();
        return randomEvent;
    }

    private static Set<Participant> generateRandomParticipants() {
        Set<Participant> participants = new HashSet<>();
        int numberOfParticipants = (int) (Math.random() * 10) + 1;

        for (int i = 1; i <= numberOfParticipants; i++) {
            String randomParticipantName = "Random Participant " + i;
            String randomParticipantEmail = "participant" + i + "@example.com";
            String randomParticipantIban = generateRandomIban();
            String randomParticipantBic = generateRandomBic();

            participants.add(new Participant(randomParticipantName, randomParticipantEmail, randomParticipantIban,
                    randomParticipantBic));
        }

        return participants;
    }

    @Test
    void paymentsToDebt() {
        LocalDateTime dateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        MyWebSocketClient wap = mock(MyWebSocketClient.class);
        ConfigLoader cnf = mock(ConfigLoader.class);
        ;
        ServerUtils severe = new ServerUtils(wap, cnf);
        Event event1 = new Event("WDKFDLS", "Event1", dateTime, new HashSet<>(), new HashSet<>());
        Participant participant1 = new Participant("participant1", "email1", "iban1", "bic1");
        Participant participant2 = new Participant("participant2", "email2", "iban2", "bic2");
        Participant participant3 = new Participant("participant3", "email3", "iban3", "bic3");
        Participant participant4 = new Participant("participant4", "email4", "iban4", "bic4");
        Monetary amount1 = new Monetary(1000);
        Monetary amount2 = new Monetary(2000);
        var date = LocalDate.now();
        Set<Participant> set1 = new HashSet<>();
        set1.add(participant2);
        set1.add(participant3);
        Set<Participant> set2 = new HashSet<>();
        set2.add(participant1);
        set2.add(participant4);
        Set<Participant> set3 = new HashSet<>();
        set3.add(participant1);
        set3.add(participant2);
        set3.add(participant3);
        set3.add(participant4);
        event1.setParticipants(set3);
        Expense expense1 = new Expense(event1, "expense1", participant1, amount1, date, set1);
        Expense expense2 = new Expense(event1, "expense2", participant2, amount2, date, set2);
        Set<Expense> setExpense = new HashSet<>(List.of(expense1, expense2));
        event1.setExpenses(setExpense);
        Set<Debt> expected = Set.of(
                new Debt(participant3, new Monetary(500), participant2),
                new Debt(participant4, new Monetary(1000), participant2));

        assertEquals(expected, Set.copyOf(severe.calculateDebts(event1)));
    }

    @Test
    void testParticipantsNoInfluenceNull() {
        MyWebSocketClient myWebSocketClient = mock(MyWebSocketClient.class);
        ConfigLoader configLoader = mock(ConfigLoader.class);
        ;
        ServerUtils serverUtils = new ServerUtils(myWebSocketClient, configLoader);

        List<Participant> result = serverUtils.participantsNoInfluence(null);
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void testParticipantsNoInfluenceEmptyList() {
        MyWebSocketClient myWebSocketClient = mock(MyWebSocketClient.class);
        ConfigLoader configLoader = mock(ConfigLoader.class);
        ;
        ServerUtils serverUtils = new ServerUtils(myWebSocketClient, configLoader);

        List<Participant> result = serverUtils.participantsNoInfluence(List.of());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void testParticipantsNoInfluenceSimple() {
        MyWebSocketClient myWebSocketClient = mock(MyWebSocketClient.class);
        ConfigLoader configLoader = mock(ConfigLoader.class);
        ;
        ServerUtils serverUtils = new ServerUtils(myWebSocketClient, configLoader);

        Participant alice = new Participant("Alice", "alice@alice.com", "NLTEST", "ABCDEF12");
        Participant bob = new Participant("Bob", "bob@bob.com", "DETEST", "FEDCBA21");
        ArrayList<Participant> participants = new ArrayList<>(List.of(alice, bob));

        List<Expense> expenses = List.of(
                new Expense(null, "Event 1", alice, new Monetary(10), LocalDate.now(), Set.of(alice, bob)),
                new Expense(null, "Event 2", bob, new Monetary(10), LocalDate.now(), Set.of(alice, bob)));

        ArrayList<Participant> result = new ArrayList<>(serverUtils.participantsNoInfluence(expenses));

        // Sort the arrays since order of the arrays doesn't matter.
        participants.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        result.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));

        assertEquals(participants, result, "equal debt should cancel out");
    }

    @Test
    void testParticipantsNoInfluenceSimple2() {
        MyWebSocketClient myWebSocketClient = mock(MyWebSocketClient.class);
        ConfigLoader configLoader = mock(ConfigLoader.class);
        ;
        ServerUtils serverUtils = new ServerUtils(myWebSocketClient, configLoader);

        Participant alice = new Participant("Alice", "alice@alice.com", "NLTEST", "ABCDEF12");
        Participant bob = new Participant("Bob", "bob@bob.com", "DETEST", "FEDCBA21");

        List<Expense> expenses = List.of(
                new Expense(null, "Event 1", alice, new Monetary(20), LocalDate.now(), Set.of(alice, bob)),
                new Expense(null, "Event 2", bob, new Monetary(10), LocalDate.now(), Set.of(alice, bob)));

        ArrayList<Participant> result = new ArrayList<>(serverUtils.participantsNoInfluence(expenses));

        assertEquals(List.of(), result, "unsettled debt");
    }

    private static String generateRandomIban() {
        return "IBAN" + UUID.randomUUID().toString().substring(0, 10);
    }

    private static String generateRandomBic() {
        return "BIC" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static final Participant alice = new Participant("Alice", "alice@example.com", "NLTEST", "ABCDEF12");
    private static final Participant bob = new Participant("Bob", "bob@example.com", "DETEST", "FEDCBA21");
    private static final Participant claude = new Participant("Claude", "claude@example.com", "DETEST", "FEDCBA21");
    private static final Participant derick = new Participant("Derick", "derick@example.com", "DETEST", "FEDCBA21");
    private static final Participant eva = new Participant("Eva", "eva@example.com", "ITTEST", "FEDCBA21");
    private static final Event ev = new Event("ABC123", "Test Debt Minimization", LocalDateTime.of(2022, 4, 1, 0, 0),
            Set.of(alice, bob, claude, derick, eva), Set.of());

    {
        alice.setId(1);
        bob.setId(2);
        claude.setId(3);
        derick.setId(4);
        eva.setId(5);
        Expense e1 = new Expense(ev,
                "Expense 1",
                alice,
                new Monetary(1000),
                LocalDate.of(2022, 4, 2), Set.of(bob, claude));
        Expense e2 = new Expense(ev,
                "Expense 2",
                bob,
                new Monetary(4000),
                LocalDate.of(2022, 4, 2), Set.of(claude, derick));
        Expense e3 = new Expense(ev,
                "Expense 3",
                derick,
                new Monetary(9000),
                LocalDate.of(2022, 4, 2), Set.of(alice, claude));
        Expense e4 = new Expense(ev,
                "Expense 4",
                claude,
                new Monetary(2000),
                LocalDate.of(2022, 4, 2), Set.of(bob, claude));
        Expense e5 = new Expense(ev,
                "Expense 5",
                eva,
                new Monetary(4000),
                LocalDate.of(2022, 4, 2), Set.of(bob, claude));
        ev.setExpenses(Set.of(e1, e2, e3, e4, e5));
    }

    @Test
    void calculateDebts_calculatesComplicatedExample() {

        MyWebSocketClient wsc = mock(MyWebSocketClient.class);
        ConfigLoader cnf = mock(ConfigLoader.class);
        ServerUtils server = new ServerUtils(wsc, cnf);
        List<Debt> debts = server.calculateDebts(ev);

        HashMap<Participant, Long> participantsGet = new HashMap<>();

        for (Debt debt : debts) {
            long creditorGets = participantsGet.getOrDefault(debt.getCreditor(), 0L);
            long debtorGets = participantsGet.getOrDefault(debt.getDebtor(), 0L);

            participantsGet.put(debt.getCreditor(), creditorGets + debt.getAmount().getInternalValue());
            participantsGet.put(debt.getDebtor(), debtorGets - debt.getAmount().getInternalValue());
        }

        // -3500 + 500 - 8000 + 7000 + 4000 = 0
        assertEquals(-3500, participantsGet.get(alice), alice.getName().concat(" receives"));
        assertEquals(500, participantsGet.get(bob), bob.getName().concat(" receives"));
        assertEquals(-8000, participantsGet.get(claude), claude.getName().concat(" receives"));
        assertEquals(7000, participantsGet.get(derick), derick.getName().concat(" receives"));
        assertEquals(4000, participantsGet.get(eva), eva.getName().concat(" receives"));
    }

    /**
     * Test many different permutations, and make sure the sum always adds up to
     * zero. Use a parameterized test to get a lot of samples
     *
     * @param Event event from the random provideExpenses source
     */
    @ParameterizedTest
    @MethodSource("provideExpenses")
    void calculateDebts_noInfiniteMoneyGlitch(Event event) {
        MyWebSocketClient wsc = mock(MyWebSocketClient.class);
        ConfigLoader cnf = mock(ConfigLoader.class);
        ServerUtils server = new ServerUtils(wsc, cnf);
        List<Debt> debts = server.calculateDebts(event);

        HashMap<Participant, Long> participantsGet = new HashMap<>();

        for (Debt debt : debts) {
            long creditorGets = participantsGet.getOrDefault(debt.getCreditor(), 0L);
            long debtorGets = participantsGet.getOrDefault(debt.getDebtor(), 0L);

            participantsGet.put(debt.getCreditor(), creditorGets + debt.getAmount().getInternalValue());
            participantsGet.put(debt.getDebtor(), debtorGets - debt.getAmount().getInternalValue());
        }

        long sum = participantsGet.values().stream().mapToLong(x -> x /* unbox */).sum();

        assertEquals(0, sum, "Net sum must always be zero");

    }

    private static Expense generateRandomExpense(Random rand, Event event) {
        List<Participant> eventParticipants = event.getParticipants().stream()
                .collect(Collectors.toList());
        int amount = rand.nextInt(1, eventParticipants.size());
        List<Integer> ints = IntStream.range(0, amount).boxed().collect(Collectors.toList());
        Collections.shuffle(ints, rand);
        Set<Participant> randomParticipants = ints.stream().map(index -> eventParticipants.get(index))
                .collect(Collectors.toSet());

        Expense exp = new Expense(event, "Random Expense",
                eventParticipants.get(rand.nextInt(eventParticipants.size())),
                new Monetary(rand.nextLong(1, 10000)), LocalDate.of(2022, 4, 1), randomParticipants);
        return exp;
    }

    private static Stream<Event> provideExpenses() {
        // Seed provided by dice throwing, very random indeed
        Random random = new Random(35263245);
        // 20 lists of 0-10 entries
        return random.ints(0, 10).limit(20).mapToObj(amount -> {
            Event ev = new Event("ABC123", "Test Debt Calculation",
                    LocalDateTime.of(2022, 4, 1, 0, 0),
                    Set.of(alice, bob, claude, derick, eva),
                    Set.of());
            Set<Expense> randomExpenses = LongStream.range(0, amount)
                    .mapToObj(id -> {
                        Expense exp = generateRandomExpense(random, ev);
                        exp.setId(id);
                        return exp;
                    }).collect(Collectors.toSet());
            ev.setExpenses(randomExpenses);
            return ev;
        });
    }

}
