package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Projet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Projet entity.
 */
@Repository
public interface ProjetRepository
    extends ProjetRepositoryWithBagRelationships, JpaRepository<Projet, Long>, JpaSpecificationExecutor<Projet> {
    default Optional<Projet> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Projet> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Projet> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
