package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ProjetDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    ProjetDTO save(ProjetDTO projetDTO);

    /**
     * Updates a projet.
     *
     * @param projetDTO the entity to update.
     * @return the persisted entity.
     */
    ProjetDTO update(ProjetDTO projetDTO);

    /**
     * Partially updates a projet.
     *
     * @param projetDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProjetDTO> partialUpdate(ProjetDTO projetDTO);

    /**
     * Get all the projets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjetDTO> findAll(Pageable pageable);
    /**
     * Get all the ProjetDTO where Devis is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<ProjetDTO> findAllWhereDevisIsNull();

    /**
     * Get all the projets with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProjetDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" projet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProjetDTO> findOne(Long id);

    /**
     * Delete the "id" projet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
