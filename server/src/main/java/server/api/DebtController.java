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


import commons.Monetary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.database.DebtRepository;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/debts")
public class DebtController {

    private final Random random;
    private final DebtRepository repo;
    private final ExpenseController expenseController;
    /**
     * Constructs a DebtController with the specified random generator and debt repository.
     *
     * @param random            An instance of Random for generating random values.
     * @param repo              An instance of DebtRepository for accessing debt data.
     * @param expenseController Allows us to use the expense controller
     */
    public DebtController(Random random, DebtRepository repo, ExpenseController expenseController) {
        this.random = random;
        this.repo = repo;
        this.expenseController = expenseController;
    }

    /**
     * Retrieves all debts.
     * @return A list of Debt objects representing all debts.
     */
    @GetMapping(path = { "", "/" })
    public List<Debt> getAll() {
        return repo.findAll();
    }

    /**
     * Retrieves a debt by its ID.
     * @param id The ID of the debt to retrieve.
     * @return ResponseEntity containing the retrieved debt if found,
     * or a bad request response if the ID is invalid or the debt does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Debt> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * calculate the total amount of debt
     *
     * @param id the debt id
     * @return monetary total debt
     */
    public Monetary calculateTotalDebt(@PathVariable("id") long id) {
        List<Debt> debts = Collections.singletonList(repo.getById(id));
        Monetary totalDebt = debts.stream()
                .map(Debt::getAmount)
                .reduce(Monetary.ZERO, Monetary::add);
        return totalDebt;
    }

    /**
     * Adds a new quote.
     *
     * @param debt The Debts object to add.
     *
     * @return ResponseEntity containing the added debts if successful,
     * or a bad request response if the debt data is invalid.
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Debt> add(@RequestBody Debt debt) {

        if (debt.getDebtor() == null
                || debt.getCreditor() == null || debt.getAmount() == null) {
            return ResponseEntity.badRequest().build();
        }

        Debt saved = repo.save(debt);
        return ResponseEntity.ok(saved);
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
        var debts = repo.findAll();
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(debts.get(idx));
    }
}
