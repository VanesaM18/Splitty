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

import commons.DomainModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.database.DomainModelRepository;

import java.util.Random;
import java.util.UUID;

@Configuration
public class Config {
    @Autowired
    private DomainModelRepository domainModelRepository;

    /**
     * Inject Random module into our spring app
     * @return the random module
     */
    @Bean
    public Random getRandom() {
        return new Random();
    }

    /**
     * gets the UUID associated with the domain model.
     * if no domain model is found in the repository,
     * a new one is created and saved.
     * @return UUID of the domain model.
     */
    @Bean
    public UUID getDomainUuid() {
        DomainModel domainModel = domainModelRepository.findFirst();
        if(domainModel == null) {
            domainModel = new DomainModel();
            domainModelRepository.save(domainModel);
        }
        return domainModel.getDomainUuid();
    }
}
