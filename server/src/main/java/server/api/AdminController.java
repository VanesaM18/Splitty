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

import commons.Admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.database.AdminRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminRepository repo;

    /**
     * Create a new admin controller.
     * This controller contains all api endpoints that have to do with admin.
     *
     * @param repo The repository used for creating, reading, updating and deleting admin.
     */
    public AdminController(AdminRepository repo) {
        this.repo = repo;
    }

    /**
     * Gets all admin rows in our database
     * @return the list of admins
     */
    @GetMapping(path = { "", "/" })
    public List<Admin> getAll() {
        return repo.findAll();
    }

    /**
     * Gets if it exists an admin by its username
     * @param username the username requested
     * @return the record with the specific username
     */
    @GetMapping("/{username}")
    public ResponseEntity<Admin> getByUsername(@PathVariable("username") String username) {
        if (username.isEmpty() || !repo.existsById(username)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(username).get());
    }
    /**
     * Adds and admin account if the username it's not taken already
     * @param admin the admin to be added
     * @return the added admin
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Admin> add(@RequestBody Admin admin) {
        if (admin.getUsername() == null || admin.getUsername().isEmpty()
            || repo.existsById(admin.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        Admin saved = repo.save(admin);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes an admin if is exits by username
     * @param username the username to be deleted
     * @return the status of the deletion
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<String> delete(@PathVariable("username") String username) {
        if (isNullOrEmpty(username) || !repo.existsById(username)) {
            return ResponseEntity.badRequest().body("Can't delete the admin account");
        }
        repo.deleteById(username);
        return ResponseEntity.ok().body("Deleted successfully");
    }

    /**
     * Checks if the admin account exists such that it can connect
     * @param admin the admin credentials
     * @return if the login was successful or not
     */
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody Admin admin) {
        if (admin.getUsername().isEmpty() || !repo.existsById(admin.getUsername())) {
            return ResponseEntity.badRequest().body("Missing credentials");
        }
        Admin exist = repo.findById(admin.getUsername()).orElse(null);
        if (exist != null && exist.getPassword().equals(admin.getPassword())) {
            return ResponseEntity.ok().body("Login successfully");
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }
    /**
     *  Checks if a string is null or empty
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
