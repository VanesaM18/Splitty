package commons;

import jakarta.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonManagedReference;
@Entity
public class Event {

    private static final int INVITE_CODE_LENGTH = 6;

    @Id
    private String inviteCode;

    private String name;

    private LocalDateTime dateTime;
    private LocalDateTime creationTime;
    private LocalDateTime lastUpdateTime;

    @ManyToMany(fetch = FetchType.EAGER,
        cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    private Set<Participant> participants;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
            mappedBy = "event")
    @JsonManagedReference
    private Set<ExpenseType> tags;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
            mappedBy = "event")
    @JsonManagedReference
    private Set<Expense> expenses;

    /**
     * Adds new expense type.
     * @param type tag to be added.
     */
    public void addType(ExpenseType type) {
        tags.add(type);
    }

    /**
     * Getter of the tags.
     *
     * @return all the tags.
     */
    public Set<ExpenseType> getTags() {
        return tags;
    }

    /**
     * Setter of the tags.
     * @param tags new tags.
     */
    public void setTags(Set<ExpenseType> tags) {
        this.tags = tags;
    }

    /**
     * Get expenses with this event
     * 
     * @return The events' expenses
     */
    public Set<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Set the expenses on this event
     * 
     * @param expenses The expenses to set
     */
    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

    /**
     * Create an Event with the given details.
     *
     * @param inviteCode   The inviteCode that can be used to join the event.
     * @param name         The name/title of the event.
     * @param dateTime     The date and time of the event.
     * @param participants A set of the participants in the event.
     * @param tags         A set of the expense types in the event.
     */
    public Event(String inviteCode, String name, LocalDateTime dateTime,
            Set<Participant> participants, Set<ExpenseType> tags) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.dateTime = dateTime;
        this.participants = participants;
        this.creationTime = LocalDateTime.now();
        this.lastUpdateTime = LocalDateTime.now();
        this.tags = tags;
    }

    /**
     * For object mapper
     */
    public Event() {
        // for object mapper
    }

    /**
     * Get the name (title) of the event.
     *
     * @return the name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this event.
     *
     * @param name The new name of this event.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the date and time of the event.
     *
     * @return the starting time of the event.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Set the starting date and time of this event.
     *
     * @param dateTime The new starting date and time.
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Removes expense type from the tags.
     * @param tag type to be removed.
     */
    public void removeTag(ExpenseType tag) {
        tags.remove(tag);
        for(Expense expense : expenses) {
            expense.removeTag(tag);
        }
    }

    /**
     * Get the list of participants partaking in this Event.
     *
     * @return a set containing all participants in this event.
     */
    public Set<Participant> getParticipants() {
        return participants;
    }

    /**
     * Change the list of participants in this event.
     *
     * @param participants A set of Participants that participate in this event.
     */
    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Add a participant to the list of participants for this event.
     *
     * @param participant The participant to add.
     */
    public void addParticipant(Participant participant) {
        this.participants.add(participant);
    }

    /**
     * Remove a participant from the list of participants for this event.
     *
     * @param participant the participant to remove.
     */
    public void removeParticipant(Participant participant) {
        this.participants.remove(participant);
    }

    /**
     * Get the invite code for this event.
     * This code can be used to join this event.
     * 
     * @return the invite code for this event.
     */
    public String getInviteCode() {
        return inviteCode;
    }

    /**
     * Set the invite code for this event. This code can be used to join this event.
     *
     * @param inviteCode The new invite code.
     */
    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    /**
     * gets the date and time of the event's creation
     * 
     * @return creation date and time of the event
     */
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    /**
     * gets the date and time of the event's last update
     * 
     * @return last update date and time of the event
     */
    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * sets the date and time of the event's last update
     * 
     * @param lastUpdateTime last update date and time
     */
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * Generate a new invite code. The code is generated using a new SecureRandom
     * instance. The
     * generated code is then automatically set as the new invite code for this
     * event.
     */
    public void generateInviteCode() {
        CodeGenerator generator = new CodeGenerator(new SecureRandom());
        String code = generator.generateCode(INVITE_CODE_LENGTH);

        setInviteCode(code);
    }

    /**
     * Check whether two events are the same. Returns true iff o is also an event,
     * and if all
     * properties match.
     *
     * @param o The object to compare to this event.
     * @return true iff o is also an event, and all properties match.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Event event = (Event) o;
        return Objects.equals(getName(), event.getName())
                && Objects.equals(getInviteCode(), event.getInviteCode())
                && Objects.equals(getDateTime(), event.getDateTime())
                && Objects.equals(getParticipants(), event.getParticipants())
                && Objects.equals(getTags(), event.getTags())
                && Objects.equals(getExpenses(), event.getExpenses());
    }

    /**
     * Generate a numerical hashCode based on the properties of this event.
     *
     * @return a hashcode based on the properties of this event.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getInviteCode(), getDateTime(), getParticipants());
    }

    /**
     * maps and calculates the payments
     * @param event the current event
     * @return a map that maps the debtor, amount owed and creditor to each other
     */
    public static Map<Map<Participant, Participant>, Monetary> calculatePayments(Event event){
        Set<Expense> eventExpenses = event.getExpenses();
        Iterator<Expense> iteratorExpense = eventExpenses.iterator();
        Map<Map<Participant, Participant>, Monetary> allDebts = new HashMap<>();

        while(iteratorExpense.hasNext()){
            Expense expense = iteratorExpense.next();
            Set<Participant> debtors = expense.getSplitBetween();
            long amount = expense.getAmount().getInternalValue() / (debtors.size());
            Participant creditor = expense.getCreator();
            Iterator<Participant> iteratorDebtors = debtors.iterator();

            while(iteratorDebtors.hasNext()){
                Map<Participant, Participant> currentMap = new HashMap<Participant, Participant>();
                currentMap.put(iteratorDebtors.next(), creditor);
                Monetary currentMonetary = new Monetary(amount);
                if (allDebts.get(currentMap) == null) {
                    allDebts.put(currentMap, currentMonetary);
                }else {
                    Monetary newMonetary = allDebts.get(currentMap);
                    allDebts.put(currentMap, Monetary.add(currentMonetary, newMonetary));
                }

            }
        }
        return  allDebts;
    }

    /**
     * converts the map into a list of debts
     * @param event the current event
     * @return list of all debts
     */
    public static List<Debt> paymentsToDebt(Event event) {
        Map<Map<Participant, Participant>, Monetary> allDebts = calculatePayments(event);
        List<Debt> listDebt = new ArrayList<>();

        allDebts.entrySet().forEach(entry -> {
            Map<Participant, Participant> pair = entry.getKey();
            Monetary monetaryValue = entry.getValue();

            pair.entrySet().forEach(innerEntry -> {
                Participant debtorId = innerEntry.getKey();
                Participant creditorId = innerEntry.getValue();
                listDebt.add(new Debt(debtorId, monetaryValue, creditorId));
            });
        });
        return listDebt;
    }

    /**
     * This method performs the final calculation of debts.
     * It first obtains all debts using the paymentsToDebt method.
     * Then, it calculates the total debt per participant.
     * After calculating total debts, it sorts participants based on their balances.
     * It populates maps for debtors and creditors based on sorted balances.
     * Finally, it iterates over debtors and creditors to settle debts
     * and creates a list of Debt objects representing settled debts
     * @param event the current event
     * @return a list of (N-1) debts
     */
    public static List<Debt> finalCalculation(Event event) {
        List<Debt> totalDebts = paymentsToDebt(event);
        Map<Participant, Long> debtPP = new HashMap<>();
        Set<Participant> setParticipants = event.getParticipants();

        // Calculate the total debt per participant
        for (Participant participant : setParticipants) {
            long amount = 0;
            for (Debt debt : totalDebts) {
                if(debt.getDebtor().equals(participant) & debt.getCreditor().equals(participant)){
                    amount += 0;
                } else if (debt.getCreditor().equals(participant)) {
                    amount += debt.getAmount().getInternalValue();
                } else if (debt.getDebtor().equals(participant)) {
                    amount -= debt.getAmount().getInternalValue();
                }
            }
            debtPP.put(participant, amount);
        }
        List<Debt> totalDebts2 = new ArrayList<>();

        // Sort the debts in descending order based on the amount
        List<Map.Entry<Participant, Long>> sortedEntries = new ArrayList<>(debtPP.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Create maps to track debtors and creditors
        Map<Participant, Long> debtors = new HashMap<>();
        Map<Participant, Long> creditors = new HashMap<>();

        // Populate debtors and creditors maps
        populateDebts(sortedEntries, debtors, creditors);

        mappingDebts(debtors, creditors, totalDebts2);

        return totalDebts2;
    }


    /**
     * populates debtor and creditor maps based on sorted balances.
     * This is a helper method for the final calculation's method.
     * @param sortedEntries a map of the debts in descending order
     * @param debtors an empty map of debtors
     * @param creditors an empty map of creditors
     */
    private static void populateDebts(List<Map.Entry<Participant, Long>> sortedEntries,
                                      Map<Participant, Long> debtors,
                                      Map<Participant, Long> creditors) {
        for (Map.Entry<Participant, Long> entry : sortedEntries) {
            if (entry.getValue() < 0) {
                debtors.put(entry.getKey(), entry.getValue());
            } else if (entry.getValue() > 0) {
                creditors.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * populates debtor and creditor maps based on sorted balances.
     * This is similar to populate debts a helper method for final calculations.
     * @param debtors the populated list of debtors
     * @param creditors the populated list of creditors
     * @param totalDebts2 an empty arraylist of debts
     */
    private static void mappingDebts(Map<Participant, Long> debtors, Map<Participant,
            Long> creditors, List<Debt> totalDebts2) {
        // Iterate over debtors and creditors to settle debts
        for (Map.Entry<Participant, Long> debtorEntry : debtors.entrySet()) {
            Participant debtor = debtorEntry.getKey();
            long debtorBalance = debtorEntry.getValue();

            Iterator<Map.Entry<Participant, Long>> iteratorCreditors
                    = creditors.entrySet().iterator();

            while (iteratorCreditors.hasNext() && debtorBalance < 0) {
                Map.Entry<Participant, Long> creditorEntry = iteratorCreditors.next();
                Participant creditor = creditorEntry.getKey();
                long creditorBalance = creditorEntry.getValue();

                long amountToPay = Math.min(-debtorBalance, creditorBalance);
                totalDebts2.add(new Debt(debtor, new Monetary(amountToPay), creditor));

                // Update debtor and creditor balances
                debtorBalance += amountToPay;
                creditorBalance -= amountToPay;

                // Update balances in the respective maps
                debtorEntry.setValue(debtorBalance);
                creditors.put(creditor, creditorBalance);

                // Remove creditor if they no longer need to receive any payments
                if (creditorBalance <= 0) {
                    iteratorCreditors.remove(); // Remove creditor from the iterator
                }
            }
        }
    }

    /** turns this into a readable string
     *
     * @return string representation of the monetary value
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
