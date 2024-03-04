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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Person person;

    private String quote;

    /**
     * Creates a quote.
     * Used for object mapping.
     */
    public Quote() {
        // for object mapper
    }

    /**
     * Retrieves the ID associated with this quote.
     * @return the quote's ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID associated with this quote.
     * @param id The new id to be set.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the person associated with this quote.
     * @return the quote's publisher.
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the person associated with this quote.
     * @param person The new publisher to be set.
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Retrieves the String content of this quote.
     * @return the quote's text.
     */
    public String getQuote() {
        return quote;
    }

    /**
     * Sets the content of this quote.
     * @param quote The new text to be set.
     */
    public void setQuote(String quote) {
        this.quote = quote;
    }

    /**
     * Creates a quote written by a publisher.
     * @param person Publisher of the quote.
     * @param quote Content of the quote.
     */
    public Quote(Person person, String quote) {
        this.person = person;
        this.quote = quote;
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
     * Turns the quote into a string.
     * @return String representation of the object.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
