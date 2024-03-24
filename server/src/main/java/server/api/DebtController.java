/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import commons.Debt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.async.DeferredResult;
import server.DebtUpdateService;
import server.services.DebtService;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/debts")
public class DebtController {
    private final DebtUpdateService debtUpdateService;
    private final DebtService debtService;
    /**
     * Constructs a DebtController with the specified random generator and debt repository.
     *
     * @param debtUpdateService An instance of the debt update service
     * @param debtService       An instance of DebtService for accessing debt operations.
     */
    public DebtController(DebtUpdateService debtUpdateService, DebtService debtService) {
        this.debtService = debtService;
        this.debtUpdateService = debtUpdateService;
    }

    /**
     * Retrieves all debts.
     * @return A list of Debt objects representing all debts.
     */
    @GetMapping(path = { "", "/" })
    public List<Debt> getAll() {
        return debtService.getAllDebts();
    }

    /**
     * Retrieves a debt by its ID.
     * @param id The ID of the debt to retrieve.
     * @return ResponseEntity containing the retrieved debt if found,
     * or a bad request response if the ID is invalid or the debt does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Debt> getById(@PathVariable("id") long id) {
        Optional<Debt> optional = debtService.getDebtById(id);
        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
    /**
     * Deletes a debt by its ID.
     *
     * @param id The ID of the debt to delete.
     * @return 204 if deleted successfully, 404 if not found
     * and 400 if id incorrect
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable("id") long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Debt> optional = debtService.deleteDebtById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a new debt.
     *
     * @param debt The Debts object to add.
     *
     * @return ResponseEntity containing the added debts if successful,
     * or a bad request response if the debt data is invalid.
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Debt> add(@RequestBody Debt debt) {
        Optional<Debt> optional = debtService.addDebt(debt);
        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Checks if a string is null or empty.
     * @param s The string to check.
     * @return True if the string is null or empty, otherwise false.
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Retrieves a random debt.
     * @return ResponseEntity containing a randomly selected Debt object if available,
     * or a bad request response if there are no debts in the repository.
     */
    @GetMapping("rnd")
    public ResponseEntity<Debt> getRandom() {
        Optional<Debt> randomDebt = debtService.getRandomDebt();
        return randomDebt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Notifies awaiting request that open debts for event needs to be refreshed
     * @param eventId the event id
     * @return status 200 if everything went well
     */
    @PostMapping("/{eventId}/received")
    public ResponseEntity<Void> markDebtAsReceived(@PathVariable("eventId") String eventId) {
        debtUpdateService.notifyUpdate(eventId);
        return ResponseEntity.ok().build();
    }

    /**
     * Long pools for any updates on open debt view
     * @param eventId the event invite code
     * @return the status of the await
     */
    @PostMapping("/{eventId}/updates")
    public DeferredResult<ResponseEntity<?>> getDebtUpdates(
        @PathVariable("eventId") String eventId) {
        DeferredResult<ResponseEntity<?>> deferredResult =
            new DeferredResult<>(30 * 60 * 1000L);

        Consumer<String> listener = update -> {
            if (update != null) {
                deferredResult.setResult(ResponseEntity.ok(update));
            } else {
                deferredResult.setResult(ResponseEntity.noContent().build());
            }
        };

        deferredResult.onCompletion(() -> {
            debtUpdateService.removeUpdateListener(eventId, listener);
        });

        debtUpdateService.waitForUpdate(eventId, listener);
        return deferredResult;
    }
}
