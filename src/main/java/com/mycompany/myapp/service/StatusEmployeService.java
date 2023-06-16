package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.StatusEmploye}.
 */
public interface StatusEmployeService {
    /**
     * Save a statusEmploye.
     *
     * @param statusEmployeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<StatusEmployeDTO> save(StatusEmployeDTO statusEmployeDTO);

    /**
     * Updates a statusEmploye.
     *
     * @param statusEmployeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<StatusEmployeDTO> update(StatusEmployeDTO statusEmployeDTO);

    /**
     * Partially updates a statusEmploye.
     *
     * @param statusEmployeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<StatusEmployeDTO> partialUpdate(StatusEmployeDTO statusEmployeDTO);

    /**
     * Get all the statusEmployes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<StatusEmployeDTO> findAll(Pageable pageable);

    /**
     * Returns the number of statusEmployes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" statusEmploye.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<StatusEmployeDTO> findOne(Long id);

    /**
     * Delete the "id" statusEmploye.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
