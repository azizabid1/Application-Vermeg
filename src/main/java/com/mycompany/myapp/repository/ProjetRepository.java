package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Projet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Projet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjetRepository extends ReactiveCrudRepository<Projet, Long>, ProjetRepositoryInternal {
    Flux<Projet> findAllBy(Pageable pageable);

    @Query("SELECT * FROM projet entity WHERE entity.devis_id = :id")
    Flux<Projet> findByDevis(Long id);

    @Query("SELECT * FROM projet entity WHERE entity.devis_id IS NULL")
    Flux<Projet> findAllWhereDevisIsNull();

    @Query("SELECT * FROM projet entity WHERE entity.equipe_id = :id")
    Flux<Projet> findByEquipe(Long id);

    @Query("SELECT * FROM projet entity WHERE entity.equipe_id IS NULL")
    Flux<Projet> findAllWhereEquipeIsNull();

    @Query("SELECT * FROM projet entity WHERE entity.tache_id = :id")
    Flux<Projet> findByTache(Long id);

    @Query("SELECT * FROM projet entity WHERE entity.tache_id IS NULL")
    Flux<Projet> findAllWhereTacheIsNull();

    @Override
    <S extends Projet> Mono<S> save(S entity);

    @Override
    Flux<Projet> findAll();

    @Override
    Mono<Projet> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjetRepositoryInternal {
    <S extends Projet> Mono<S> save(S entity);

    Flux<Projet> findAllBy(Pageable pageable);

    Flux<Projet> findAll();

    Mono<Projet> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Projet> findAllBy(Pageable pageable, Criteria criteria);

}
