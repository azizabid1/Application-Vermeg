package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.StatusEmploye;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the StatusEmploye entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusEmployeRepository extends ReactiveCrudRepository<StatusEmploye, Long>, StatusEmployeRepositoryInternal {
    Flux<StatusEmploye> findAllBy(Pageable pageable);

    @Query("SELECT * FROM status_employe entity WHERE entity.user_id_id = :id")
    Flux<StatusEmploye> findByUserId(Long id);

    @Query("SELECT * FROM status_employe entity WHERE entity.user_id_id IS NULL")
    Flux<StatusEmploye> findAllWhereUserIdIsNull();

    @Override
    <S extends StatusEmploye> Mono<S> save(S entity);

    @Override
    Flux<StatusEmploye> findAll();

    @Override
    Mono<StatusEmploye> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface StatusEmployeRepositoryInternal {
    <S extends StatusEmploye> Mono<S> save(S entity);

    Flux<StatusEmploye> findAllBy(Pageable pageable);

    Flux<StatusEmploye> findAll();

    Mono<StatusEmploye> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<StatusEmploye> findAllBy(Pageable pageable, Criteria criteria);

}
