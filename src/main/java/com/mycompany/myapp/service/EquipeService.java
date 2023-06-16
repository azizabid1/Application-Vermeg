package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.EquipeDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Equipe}.
 */
public interface EquipeService {
    /**
     * Save a equipe.
     *
     * @param equipeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EquipeDTO> save(EquipeDTO equipeDTO);

    /**
     * Updates a equipe.
     *
     * @param equipeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EquipeDTO> update(EquipeDTO equipeDTO);

    /**
     * Partially updates a equipe.
     *
     * @param equipeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EquipeDTO> partialUpdate(EquipeDTO equipeDTO);

    /**
     * Get all the equipes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EquipeDTO> findAll(Pageable pageable);

    /**
     * Get all the equipes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EquipeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of equipes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" equipe.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EquipeDTO> findOne(Long id);

    /**
     * Delete the "id" equipe.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
