package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.DevisDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Devis}.
 */
public interface DevisService {
    /**
     * Save a devis.
     *
     * @param devisDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DevisDTO> save(DevisDTO devisDTO);

    /**
     * Updates a devis.
     *
     * @param devisDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DevisDTO> update(DevisDTO devisDTO);

    /**
     * Partially updates a devis.
     *
     * @param devisDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DevisDTO> partialUpdate(DevisDTO devisDTO);

    /**
     * Get all the devis.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DevisDTO> findAll(Pageable pageable);

    /**
     * Returns the number of devis available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" devis.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DevisDTO> findOne(Long id);

    /**
     * Delete the "id" devis.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
