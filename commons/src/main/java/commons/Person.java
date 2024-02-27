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
package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;

    private String lastName;

    /**
     * Creates a person who can publish quotes.
     * @param firstName The first name of the person.
     * @param lastName The last name of the person.
     */
    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Creates a person.
     * Used for object mapping.
     */
    public Person() {
        // for object mapper
    }

    /**
     * Retrieves the ID associated with this person.
     * @return the person's ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID associated with this person.
     * @param id The new id to be set.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the first name associated with this person.
     * @return the person's first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name associated with this quote.
     * @param firstName The new first name to be set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Retrieves the last name associated with this person.
     * @return the person's last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name associated with this quote.
     * @param lastName The new last name to be set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Checks if another object is exactly the same as this one.
     * @param obj Object that needs to be checked.
     * @return Weather the given object is equal to this one.
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Computes a hash code for this object.
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Turns the person into a string.
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
