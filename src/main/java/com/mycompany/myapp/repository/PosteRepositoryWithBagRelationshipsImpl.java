package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Poste;
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
public class PosteRepositoryWithBagRelationshipsImpl implements PosteRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Poste> fetchBagRelationships(Optional<Poste> poste) {
        return poste.map(this::fetchUsers);
    }

    @Override
    public Page<Poste> fetchBagRelationships(Page<Poste> postes) {
        return new PageImpl<>(fetchBagRelationships(postes.getContent()), postes.getPageable(), postes.getTotalElements());
    }

    @Override
    public List<Poste> fetchBagRelationships(List<Poste> postes) {
        return Optional.of(postes).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    Poste fetchUsers(Poste result) {
        return entityManager
            .createQuery("select poste from Poste poste left join fetch poste.users where poste is :poste", Poste.class)
            .setParameter("poste", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Poste> fetchUsers(List<Poste> postes) {
        return entityManager
            .createQuery("select distinct poste from Poste poste left join fetch poste.users where poste in :postes", Poste.class)
            .setParameter("postes", postes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
