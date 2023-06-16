package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Devis;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Devis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DevisRepository extends ReactiveCrudRepository<Devis, Long>, DevisRepositoryInternal {
    Flux<Devis> findAllBy(Pageable pageable);

    @Override
    <S extends Devis> Mono<S> save(S entity);

    @Override
    Flux<Devis> findAll();

    @Override
    Mono<Devis> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DevisRepositoryInternal {
    <S extends Devis> Mono<S> save(S entity);

    Flux<Devis> findAllBy(Pageable pageable);

    Flux<Devis> findAll();

    Mono<Devis> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Devis> findAllBy(Pageable pageable, Criteria criteria);

}
