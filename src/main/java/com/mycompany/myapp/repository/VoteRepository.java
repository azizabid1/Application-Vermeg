package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Vote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Vote entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VoteRepository extends ReactiveCrudRepository<Vote, Long>, VoteRepositoryInternal {
    Flux<Vote> findAllBy(Pageable pageable);

    @Override
    <S extends Vote> Mono<S> save(S entity);

    @Override
    Flux<Vote> findAll();

    @Override
    Mono<Vote> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface VoteRepositoryInternal {
    <S extends Vote> Mono<S> save(S entity);

    Flux<Vote> findAllBy(Pageable pageable);

    Flux<Vote> findAll();

    Mono<Vote> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Vote> findAllBy(Pageable pageable, Criteria criteria);

}
