package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Equipe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Equipe entity.
 */
@Repository
public interface EquipeRepository
    extends EquipeRepositoryWithBagRelationships, JpaRepository<Equipe, Long>, JpaSpecificationExecutor<Equipe> {
    default Optional<Equipe> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Equipe> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Equipe> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
