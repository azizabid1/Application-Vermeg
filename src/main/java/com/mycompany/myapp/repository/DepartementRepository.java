package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Departement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Departement entity.
 */
@Repository
public interface DepartementRepository
    extends DepartementRepositoryWithBagRelationships, JpaRepository<Departement, Long>, JpaSpecificationExecutor<Departement> {
    default Optional<Departement> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Departement> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Departement> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
