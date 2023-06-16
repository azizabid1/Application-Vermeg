package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.DepartementDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Departement}.
 */
public interface DepartementService {
    /**
     * Save a departement.
     *
     * @param departementDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<DepartementDTO> save(DepartementDTO departementDTO);

    /**
     * Updates a departement.
     *
     * @param departementDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<DepartementDTO> update(DepartementDTO departementDTO);

    /**
     * Partially updates a departement.
     *
     * @param departementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<DepartementDTO> partialUpdate(DepartementDTO departementDTO);

    /**
     * Get all the departements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<DepartementDTO> findAll(Pageable pageable);

    /**
     * Returns the number of departements available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" departement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<DepartementDTO> findOne(Long id);

    /**
     * Delete the "id" departement.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
