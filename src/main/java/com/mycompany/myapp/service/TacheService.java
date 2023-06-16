package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.TacheDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Tache}.
 */
public interface TacheService {
    /**
     * Save a tache.
     *
     * @param tacheDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TacheDTO> save(TacheDTO tacheDTO);

    /**
     * Updates a tache.
     *
     * @param tacheDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TacheDTO> update(TacheDTO tacheDTO);

    /**
     * Partially updates a tache.
     *
     * @param tacheDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TacheDTO> partialUpdate(TacheDTO tacheDTO);

    /**
     * Get all the taches.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TacheDTO> findAll(Pageable pageable);

    /**
     * Returns the number of taches available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tache.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TacheDTO> findOne(Long id);

    /**
     * Delete the "id" tache.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
