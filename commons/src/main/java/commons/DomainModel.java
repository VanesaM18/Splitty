package commons;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class DomainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID domainUuid;

    /**
     * constructs a new DomainModel object with a randomly generated UUID.
     */
    public DomainModel() {
        this.domainUuid = UUID.randomUUID();
    }

    /**
     * gets the UUID associated with this domain model.
     * @return UUID associated with this domain model.
     */
    public UUID getDomainUuid() {
        return domainUuid;
    }
}
