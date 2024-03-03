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

import commons.Debts;
import commons.Quote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.database.DebtRepository;
import server.database.QuoteRepository;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/quotes")
public class DebtController {

    private final Random random;
    private final DebtRepository repo;

    /**
     * Constructs a DebtController with the specified random generator and quote repository.
     * @param random An instance of Random for generating random values.
     * @param repo An instance of QuoteRepository for accessing quote data.
     */
    public DebtController(Random random, DebtRepository repo) {
        this.random = random;

        this.repo = repo;
    }

    /**
     * Retrieves all debts.
     * @return A list of Debt objects representing all debts.
     */
    @GetMapping(path = { "", "/" })
    public List<Debts> getAll() {
        return repo.findAll();
    }

    /**
     * Retrieves a debt by its ID.
     * @param id The ID of the quote to retrieve.
     * @return ResponseEntity containing the retrieved Quote if found,
     * or a bad request response if the ID is invalid or the quote does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Debts> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Adds a new quote.
     *
     * @param debts The Debts object to add.
     *
     * @return ResponseEntity containing the added Quote if successful,
     * or a bad request response if the quote data is invalid.
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Debts> add(@RequestBody Debts debts) {

        if (debts.getDebtor() == null
                || debts.getDebtor() == null || debts.getAmount() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Debts saved = repo.save(debts);
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
     * Retrieves a random quote.
     * @return ResponseEntity containing a randomly selected Quote object if available,
     * or a bad request response if there are no quotes in the repository.
     */
    @GetMapping("rnd")
    public ResponseEntity<Debts> getRandom() {
        var debts = repo.findAll();
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(debts.get(idx));
    }
}
