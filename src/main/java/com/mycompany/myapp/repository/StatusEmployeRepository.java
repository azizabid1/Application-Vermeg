package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.StatusEmploye;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the StatusEmploye entity.
 */
@Repository
public interface StatusEmployeRepository
    extends StatusEmployeRepositoryWithBagRelationships, JpaRepository<StatusEmploye, Long>, JpaSpecificationExecutor<StatusEmploye> {
    default Optional<StatusEmploye> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<StatusEmploye> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<StatusEmploye> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
