package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Equipe;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class EquipeRepositoryWithBagRelationshipsImpl implements EquipeRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Equipe> fetchBagRelationships(Optional<Equipe> equipe) {
        return equipe.map(this::fetchUsers);
    }

    @Override
    public Page<Equipe> fetchBagRelationships(Page<Equipe> equipes) {
        return new PageImpl<>(fetchBagRelationships(equipes.getContent()), equipes.getPageable(), equipes.getTotalElements());
    }

    @Override
    public List<Equipe> fetchBagRelationships(List<Equipe> equipes) {
        return Optional.of(equipes).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    Equipe fetchUsers(Equipe result) {
        return entityManager
            .createQuery("select equipe from Equipe equipe left join fetch equipe.users where equipe is :equipe", Equipe.class)
            .setParameter("equipe", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Equipe> fetchUsers(List<Equipe> equipes) {
        return entityManager
            .createQuery("select distinct equipe from Equipe equipe left join fetch equipe.users where equipe in :equipes", Equipe.class)
            .setParameter("equipes", equipes)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
