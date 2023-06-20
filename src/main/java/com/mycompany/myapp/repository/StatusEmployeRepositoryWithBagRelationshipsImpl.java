package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.StatusEmploye;
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
public class StatusEmployeRepositoryWithBagRelationshipsImpl implements StatusEmployeRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<StatusEmploye> fetchBagRelationships(Optional<StatusEmploye> statusEmploye) {
        return statusEmploye.map(this::fetchUsers);
    }

    @Override
    public Page<StatusEmploye> fetchBagRelationships(Page<StatusEmploye> statusEmployes) {
        return new PageImpl<>(
            fetchBagRelationships(statusEmployes.getContent()),
            statusEmployes.getPageable(),
            statusEmployes.getTotalElements()
        );
    }

    @Override
    public List<StatusEmploye> fetchBagRelationships(List<StatusEmploye> statusEmployes) {
        return Optional.of(statusEmployes).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    StatusEmploye fetchUsers(StatusEmploye result) {
        return entityManager
            .createQuery(
                "select statusEmploye from StatusEmploye statusEmploye left join fetch statusEmploye.users where statusEmploye is :statusEmploye",
                StatusEmploye.class
            )
            .setParameter("statusEmploye", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<StatusEmploye> fetchUsers(List<StatusEmploye> statusEmployes) {
        return entityManager
            .createQuery(
                "select distinct statusEmploye from StatusEmploye statusEmploye left join fetch statusEmploye.users where statusEmploye in :statusEmployes",
                StatusEmploye.class
            )
            .setParameter("statusEmployes", statusEmployes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
