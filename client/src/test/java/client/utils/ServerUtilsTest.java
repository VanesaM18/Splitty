package client.utils;

import client.MyWebSocketClient;
import commons.Admin;
import commons.Event;
import commons.Participant;
import commons.WebSocketMessage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerUtilsTest {

    private static final int INVITE_CODE_LENGTH = 6;

    private static final Function<Integer, List<Event>> func = (total) -> IntStream.range(0, total)
            .mapToObj(i -> generateRandomEvent())
            .collect(Collectors.toList());

    @Test
    void getAllEvents() {
        int eventCount = (int) ( Math.random() * 100);

        var pz = func.apply(eventCount);
        var wsm = new WebSocketMessage();
        wsm.setData(pz);
        var future = CompletableFuture.supplyAsync(() -> wsm);
        MyWebSocketClient wap = mock(MyWebSocketClient.class);
        when(wap.addPendingRequests(any())).thenReturn(future);
        ServerUtils severe = new ServerUtils(wap);
        ServerUtils.adminAuth(new Admin("","",""));
        var response = severe.getAllEvents();
        assertTrue(response.isPresent());
        var responseList = response.get();
        assertEquals(eventCount, responseList.size());

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

        Event randomEvent = new Event(randomInviteCode, randomName, randomDateTime, randomParticipants);
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

    private static String generateRandomIban() {
        return "IBAN" + UUID.randomUUID().toString().substring(0, 10);
    }

    private static String generateRandomBic() {
        return "BIC" + UUID.randomUUID().toString().substring(0, 8);
    }

}