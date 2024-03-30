package server.database;

import commons.DomainModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainModelRepository extends JpaRepository<DomainModel, Long> {

    /**
     * gets the DomainModel entity from the repository, if any.
     * @return the DomainModel entity found in the repository,
     * or null if none exists.
     */
    default DomainModel findFirst() {
        var list = this.findAll();
        return list.isEmpty()
                ? null
                : list.get(0);
    }
}
