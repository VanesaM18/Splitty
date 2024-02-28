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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import commons.Admin;
import commons.Participant;
import commons.Quote;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

import org.glassfish.jersey.client.ClientConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";

    /**
     * Get quotes the hard way
     * @throws IOException
     * @throws URISyntaxException
     */
    public void getQuotesTheHardWay() throws IOException, URISyntaxException {
        var url = new URI("http://localhost:8080/api/quotes").toURL();
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Get all the quotes stored in the database
     * @return all the quotes
     */
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER)
                .path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    /**
     * Add a quote
     * @param quote to be added
     * @return the added quote
     */
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER)
                .path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    /**
     * Adds a participant
     * @param p the participant to be added
     * @return the added participant
     */
    public Participant addParticipant(Participant p) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER)
                .path("api/participants") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(p, APPLICATION_JSON), Participant.class);
    }

    /**
     * Adds an admin
     * @param admin to be added
     * @return the added admin
     */
    public Admin addAdmin(Admin admin) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER)
                .path("api/admin") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(admin, APPLICATION_JSON), Admin.class);
    }

    /**
     * For login
     * @param admin credentials to be logged in with
     * @return if was successful or not
     */
    public String loginAdmin(Admin admin) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER)
                .path("api/admin/login") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(admin, APPLICATION_JSON), String.class);
    }
}
