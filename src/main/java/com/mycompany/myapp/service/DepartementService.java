package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.DepartementDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    DepartementDTO save(DepartementDTO departementDTO);

    /**
     * Updates a departement.
     *
     * @param departementDTO the entity to update.
     * @return the persisted entity.
     */
    DepartementDTO update(DepartementDTO departementDTO);

    /**
     * Partially updates a departement.
     *
     * @param departementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DepartementDTO> partialUpdate(DepartementDTO departementDTO);

    /**
     * Get all the departements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DepartementDTO> findAll(Pageable pageable);

    /**
     * Get all the departements with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DepartementDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" departement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DepartementDTO> findOne(Long id);

    /**
     * Delete the "id" departement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
