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
package server;

import commons.Admin;
import commons.PasswordGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import server.database.AdminRepository;

@SpringBootApplication
@EntityScan(basePackages = {"commons", "server"})
public class Main {

    /**
     * The main of our server
     * @param args to be passed to our app
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        AdminRepository repo = context.getBean(AdminRepository.class);

        PasswordGenerator generator = new PasswordGenerator(8);
        String username = "admin";
        String password = generator.generate();
        if (repo.existsById(username)) {
            repo.deleteById(username);
        }
        repo.save(new Admin(username, password, ""));
        System.out.println("Connect to the admin overview with the following credentials:\n - username: "
            + username + "\n - password: " + password + "\n");
    }
}
