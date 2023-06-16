package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ProjetDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Projet}.
 */
public interface ProjetService {
    /**
     * Save a projet.
     *
     * @param projetDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProjetDTO> save(ProjetDTO projetDTO);

    /**
     * Updates a projet.
     *
     * @param projetDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProjetDTO> update(ProjetDTO projetDTO);

    /**
     * Partially updates a projet.
     *
     * @param projetDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProjetDTO> partialUpdate(ProjetDTO projetDTO);

    /**
     * Get all the projets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProjetDTO> findAll(Pageable pageable);

    /**
     * Returns the number of projets available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" projet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProjetDTO> findOne(Long id);

    /**
     * Delete the "id" projet.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
