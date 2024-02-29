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

import commons.Quote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.database.QuoteRepository;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final Random random;
    private final QuoteRepository repo;

    /**
     * Constructs a QuoteController with the specified random generator and quote repository.
     * @param random An instance of Random for generating random values.
     * @param repo An instance of QuoteRepository for accessing quote data.
     */
    public QuoteController(Random random, QuoteRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Retrieves all quotes.
     * @return A list of Quote objects representing all quotes.
     */
    @GetMapping(path = { "", "/" })
    public List<Quote> getAll() {
        return repo.findAll();
    }

    /**
     * Retrieves a quote by its ID.
     * @param id The ID of the quote to retrieve.
     * @return ResponseEntity containing the retrieved Quote if found,
     * or a bad request response if the ID is invalid or the quote does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Quote> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Adds a new quote.
     *
     * @param quote The Quote object to add.
     *
     * @return ResponseEntity containing the added Quote if successful,
     * or a bad request response if the quote data is invalid.
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Quote> add(@RequestBody Quote quote) {

        if (quote.getPerson() == null
            || isNullOrEmpty(quote.getPerson().getFirstName())
            || isNullOrEmpty(quote.getPerson().getFirstName())
            || isNullOrEmpty(quote.getQuote())) {
            return ResponseEntity.badRequest().build();
        }

        Quote saved = repo.save(quote);
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
    public ResponseEntity<Quote> getRandom() {
        var quotes = repo.findAll();
        var idx = random.nextInt((int) repo.count());
        return ResponseEntity.ok(quotes.get(idx));
    }
}
