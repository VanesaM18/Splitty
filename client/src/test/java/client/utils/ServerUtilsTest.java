package client.utils;

import client.ConfigLoader;
import client.MyWebSocketClient;
import commons.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        int eventCount = (int) ( Math.random() * 100);

        var pz = func.apply(1).get(0);
        var inviteCode = pz.getInviteCode();
        var wsm = new WebSocketMessage();
        wsm.setData(pz);
        var future = CompletableFuture.supplyAsync(() -> wsm);
        MyWebSocketClient wap = mock(MyWebSocketClient.class);
        ConfigLoader cnf = mock(ConfigLoader.class);
        when(wap.addPendingRequests(any())).thenReturn(future);
        ServerUtils severe = new ServerUtils(wap, cnf);
        ServerUtils.adminAuth(new Admin("","",""));
        var response = severe.getEventById(inviteCode);
        assertNotNull(response);
        var responseId = response.getInviteCode();
        assertEquals(inviteCode, responseId);

    }

    /**
     * method to create and send a random event.
     * @return randomly generated event.
     */
    public static Event generateRandomEvent() {
        String randomInviteCode = UUID.randomUUID().toString().substring(0, INVITE_CODE_LENGTH);
        String randomName = "Random Event " + UUID.randomUUID().toString().substring(0, 5);
        LocalDateTime randomDateTime = LocalDateTime.now().plusDays((long) (Math.random() * 30));
        Set<Participant> randomParticipants = generateRandomParticipants();

        Event randomEvent = new Event(randomInviteCode, randomName, randomDateTime, randomParticipants, new HashSet<>());
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

            participants.add(new Participant(randomParticipantName, randomParticipantEmail, randomParticipantIban, randomParticipantBic));
        }

        return participants;
    }

    @Test
    void paymentsToDebt() {
        LocalDateTime dateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);
        MyWebSocketClient wap = mock(MyWebSocketClient.class);
        ConfigLoader cnf = mock(ConfigLoader.class);;
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
        List<Debt> expected = new ArrayList<>();
        assertEquals(expected, severe.calculateDebts(event1));
    }

    private static String generateRandomIban() {
        return "IBAN" + UUID.randomUUID().toString().substring(0, 10);
    }

    private static String generateRandomBic() {
        return "BIC" + UUID.randomUUID().toString().substring(0, 8);
    }

}