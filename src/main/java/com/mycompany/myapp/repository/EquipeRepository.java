package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Equipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Equipe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EquipeRepository extends ReactiveCrudRepository<Equipe, Long>, EquipeRepositoryInternal {
    Flux<Equipe> findAllBy(Pageable pageable);

    @Override
    Mono<Equipe> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Equipe> findAllWithEagerRelationships();

    @Override
    Flux<Equipe> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM equipe entity WHERE entity.user_id_id = :id")
    Flux<Equipe> findByUserId(Long id);

    @Query("SELECT * FROM equipe entity WHERE entity.user_id_id IS NULL")
    Flux<Equipe> findAllWhereUserIdIsNull();

    @Query(
        "SELECT entity.* FROM equipe entity JOIN rel_equipe__vote joinTable ON entity.id = joinTable.vote_id WHERE joinTable.vote_id = :id"
    )
    Flux<Equipe> findByVote(Long id);

    @Override
    <S extends Equipe> Mono<S> save(S entity);

    @Override
    Flux<Equipe> findAll();

    @Override
    Mono<Equipe> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EquipeRepositoryInternal {
    <S extends Equipe> Mono<S> save(S entity);

    Flux<Equipe> findAllBy(Pageable pageable);

    Flux<Equipe> findAll();

    Mono<Equipe> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Equipe> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Equipe> findOneWithEagerRelationships(Long id);

    Flux<Equipe> findAllWithEagerRelationships();

    Flux<Equipe> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
