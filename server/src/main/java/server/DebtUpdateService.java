package server;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
public class DebtUpdateService {

    private final Map<String, List<Consumer<String>>> listeners = new ConcurrentHashMap<>();
    // Map each eventId to a list of locks, where each lock corresponds to a waiting request
    private final Map<String, List<Object>> eventLocks = new ConcurrentHashMap<>();

    /**
     * Waits for an update for the specified event ID. When an update occurs,
     * the provided callback is called. This method blocks until the update occurs.
     *
     * @param eventId The ID of the event to wait for updates on.
     * @param callback The callback to invoke when an update occurs.
     */
    public void waitForUpdate(String eventId, Consumer<String> callback) {
        // Create a new lock for this request
        final Object lock = new Object();

        // Add the lock to the list of locks for this eventId
        eventLocks.computeIfAbsent(eventId, k ->
            Collections.synchronizedList(new CopyOnWriteArrayList<>())).add(lock);

        synchronized (lock) {
            try {
                // Register the listener before waiting
                listeners.computeIfAbsent(eventId, k -> new CopyOnWriteArrayList<>()).add(callback);

                // Wait on the lock for this specific request
                lock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                System.err.println("Interrupted while waiting for update on event ID: " + eventId);
            }
        }
    }

    /**
     * Notifies about an update to all listeners waiting on the specified event ID.
     * Also wakes up all locks (requests) waiting for an update on this event ID.
     *
     * @param eventId The ID of the event that has been updated.
     */
    public void notifyUpdate(String eventId) {
        List<Consumer<String>> eventListeners = listeners.remove(eventId);
        if (eventListeners != null) {
            eventListeners.forEach(listener -> listener.accept("Update for event ID: " + eventId));
        }

        // Retrieve and notify all locks for this event
        List<Object> locks = eventLocks.remove(eventId);
        if (locks != null) {
            for (Object lock : locks) {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }
    }

    /**
     * Removed a listener
     * @param eventId the event where listener was attached
     * @param listener the listener
     * @return the status of removal
     */
    public boolean removeUpdateListener(String eventId, Consumer<String> listener) {
        List<Consumer<String>> listenersList = listeners.getOrDefault(eventId, null);
        if (listenersList != null) {
            return listenersList.remove(listener);
        }
        return false;
    }
}
