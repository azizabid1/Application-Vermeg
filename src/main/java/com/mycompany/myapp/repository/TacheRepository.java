package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tache;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Tache entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TacheRepository extends ReactiveCrudRepository<Tache, Long>, TacheRepositoryInternal {
    Flux<Tache> findAllBy(Pageable pageable);

    @Override
    <S extends Tache> Mono<S> save(S entity);

    @Override
    Flux<Tache> findAll();

    @Override
    Mono<Tache> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TacheRepositoryInternal {
    <S extends Tache> Mono<S> save(S entity);

    Flux<Tache> findAllBy(Pageable pageable);

    Flux<Tache> findAll();

    Mono<Tache> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tache> findAllBy(Pageable pageable, Criteria criteria);

}
