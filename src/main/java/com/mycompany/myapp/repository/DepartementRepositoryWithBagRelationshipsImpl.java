package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Departement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class DepartementRepositoryWithBagRelationshipsImpl implements DepartementRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Departement> fetchBagRelationships(Optional<Departement> departement) {
        return departement.map(this::fetchUsers);
    }

    @Override
    public Page<Departement> fetchBagRelationships(Page<Departement> departements) {
        return new PageImpl<>(
            fetchBagRelationships(departements.getContent()),
            departements.getPageable(),
            departements.getTotalElements()
        );
    }

    @Override
    public List<Departement> fetchBagRelationships(List<Departement> departements) {
        return Optional.of(departements).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    Departement fetchUsers(Departement result) {
        return entityManager
            .createQuery(
                "select departement from Departement departement left join fetch departement.users where departement is :departement",
                Departement.class
            )
            .setParameter("departement", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Departement> fetchUsers(List<Departement> departements) {
        return entityManager
            .createQuery(
                "select distinct departement from Departement departement left join fetch departement.users where departement in :departements",
                Departement.class
            )
            .setParameter("departements", departements)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
