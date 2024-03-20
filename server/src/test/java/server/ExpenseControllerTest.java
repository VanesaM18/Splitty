package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Expense;
import commons.Event;
import commons.Monetary;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.api.ExpenseController;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseRepository expenseRepository;

    @MockBean
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Expense testExpense;

    @BeforeEach
    public void setup() {
        Participant participant = new Participant("John Doe", "john.doe@example.com", "IBAN123", "BIC456");
        participant.setId(1L);
        testExpense = new Expense(
                new Event("ABCDEF", "Event 1", LocalDateTime.now(), new HashSet<>()),
                "Foo",
                participant,
                new Monetary(1000, "USD"),
                LocalDate.now(), Set.of());
        testExpense.setId(1L);
    }

    @Test
    public void getByEvent_found_shouldReturnExpenses() throws Exception {
        when(expenseRepository.getExpensesByEventInviteCode("ABCDEF"))
                .thenReturn(Collections.singletonList(testExpense));

        mockMvc.perform(get("/api/expenses/by_event/{id}", "ABCDEF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(expenseRepository, times(1)).getExpensesByEventInviteCode("ABCDEF");
    }

    @Test
    public void getByEvent_notFound_shouldReturnNotFound() throws Exception {
        when(expenseRepository.getExpensesByEventInviteCode("ABCGEG")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/expenses/by_event/{id}", "ABCGEG"))
                .andExpect(status().isNotFound());

        verify(expenseRepository, times(1)).getExpensesByEventInviteCode("ABCGEG");
    }

    @Test
    public void addExpense_valid_shouldReturnNoContent() throws Exception {
        String expenseJson = objectMapper.writeValueAsString(testExpense);
        mockMvc.perform(post("/api/expenses/by_event/{id}", testExpense.getEvent().getInviteCode())
                .contentType(MediaType.APPLICATION_JSON)
                .content(expenseJson))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    public void addExpense_invalid_shouldReturnBadRequest() throws Exception {
        Expense invalidExpense = new Expense();

        mockMvc.perform(post("/api/expenses/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidExpense)))
                .andExpect(status().isBadRequest());

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    public void updateById_existingIdValidUpdate_shouldReturnUpdatedExpense() throws Exception {
        /* will be fixed soon
        Expense updatedExpense = new Expense(null, "Bar", testExpense.getCreator(), new Monetary(1500, "USD"),
                LocalDate.now(), Set.of());
        updatedExpense.setId(1L);

        when(expenseRepository.getReferenceById(1L)).thenReturn(testExpense);
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);

        String updatedExpenseJson = objectMapper.writeValueAsString(updatedExpense);

        mockMvc.perform(put("/api/expenses/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedExpenseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount.internalValue", is(1500)));

        verify(expenseRepository, times(1)).save(any(Expense.class));
         */
    }

    @Test
    public void updateById_nonExistingId_shouldReturnNotFound() throws Exception {
        when(expenseRepository.getById(anyLong())).thenThrow(new EntityNotFoundException());

        Expense updateAttempt = new Expense();
        updateAttempt.setId(99L);

        mockMvc.perform(put("/api/expenses/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAttempt)))
                .andExpect(status().isNotFound());

        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    public void deleteById_existingId_shouldReturnNoContent() throws Exception {
        when(expenseRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/expenses/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteById_nonExistingId_shouldReturnNotFound() throws Exception {
        when(expenseRepository.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/expenses/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(expenseRepository, never()).deleteById(99L);
    }

}
