package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Poste;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Poste entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PosteRepository extends ReactiveCrudRepository<Poste, Long>, PosteRepositoryInternal {
    Flux<Poste> findAllBy(Pageable pageable);

    @Query("SELECT * FROM poste entity WHERE entity.user_id_id = :id")
    Flux<Poste> findByUserId(Long id);

    @Query("SELECT * FROM poste entity WHERE entity.user_id_id IS NULL")
    Flux<Poste> findAllWhereUserIdIsNull();

    @Override
    <S extends Poste> Mono<S> save(S entity);

    @Override
    Flux<Poste> findAll();

    @Override
    Mono<Poste> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PosteRepositoryInternal {
    <S extends Poste> Mono<S> save(S entity);

    Flux<Poste> findAllBy(Pageable pageable);

    Flux<Poste> findAll();

    Mono<Poste> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Poste> findAllBy(Pageable pageable, Criteria criteria);

}
