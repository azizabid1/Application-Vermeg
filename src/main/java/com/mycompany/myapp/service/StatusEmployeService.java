package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    StatusEmployeDTO save(StatusEmployeDTO statusEmployeDTO);

    /**
     * Updates a statusEmploye.
     *
     * @param statusEmployeDTO the entity to update.
     * @return the persisted entity.
     */
    StatusEmployeDTO update(StatusEmployeDTO statusEmployeDTO);

    /**
     * Partially updates a statusEmploye.
     *
     * @param statusEmployeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<StatusEmployeDTO> partialUpdate(StatusEmployeDTO statusEmployeDTO);

    /**
     * Get all the statusEmployes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StatusEmployeDTO> findAll(Pageable pageable);

    /**
     * Get all the statusEmployes with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<StatusEmployeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" statusEmploye.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<StatusEmployeDTO> findOne(Long id);

    /**
     * Delete the "id" statusEmploye.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
