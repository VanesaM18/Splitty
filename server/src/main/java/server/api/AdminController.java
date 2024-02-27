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

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import commons.Admin;
import server.database.AdminRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminRepository repo;

    public AdminController(AdminRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = { "", "/" })
    public List<Admin> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{username}")
    public ResponseEntity<Admin> getByUsername(@PathVariable("username") String username) {
        if (username.isEmpty() || !repo.existsById(username)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(username).get());
    }

    @PostMapping(path = { "", "/" })
    public ResponseEntity<Admin> add(@RequestBody Admin admin) {
        if (admin.username == null || repo.existsById(admin.username)) {
            return ResponseEntity.badRequest().build();
        }

        Admin saved = repo.save(admin);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> delete(@PathVariable("username") String username) {
        if (isNullOrEmpty(username) || !repo.existsById(username)) {
            return ResponseEntity.badRequest().body("Can't delete the admin account");
        }
        repo.deleteById(username);
        return ResponseEntity.ok().body("Deleted successfully");
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody Admin admin) {
        if (admin.username.isEmpty() || !repo.existsById(admin.username)) {
            return ResponseEntity.badRequest().body("Missing credentials");
        }
        Admin exist = repo.findById(admin.username).orElse(null);
        if (exist != null && exist.password.equals(admin.password)) {
            return ResponseEntity.ok().body("Login successfully");
        }
        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}