package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Poste;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Poste entity.
 */
@Repository
public interface PosteRepository extends PosteRepositoryWithBagRelationships, JpaRepository<Poste, Long>, JpaSpecificationExecutor<Poste> {
    default Optional<Poste> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Poste> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Poste> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
