package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Departement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Departement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepartementRepository extends ReactiveCrudRepository<Departement, Long>, DepartementRepositoryInternal {
    Flux<Departement> findAllBy(Pageable pageable);

    @Query("SELECT * FROM departement entity WHERE entity.user_id_id = :id")
    Flux<Departement> findByUserId(Long id);

    @Query("SELECT * FROM departement entity WHERE entity.user_id_id IS NULL")
    Flux<Departement> findAllWhereUserIdIsNull();

    @Override
    <S extends Departement> Mono<S> save(S entity);

    @Override
    Flux<Departement> findAll();

    @Override
    Mono<Departement> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DepartementRepositoryInternal {
    <S extends Departement> Mono<S> save(S entity);

    Flux<Departement> findAllBy(Pageable pageable);

    Flux<Departement> findAll();

    Mono<Departement> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Departement> findAllBy(Pageable pageable, Criteria criteria);

}
