package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.PosteDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Poste}.
 */
public interface PosteService {
    /**
     * Save a poste.
     *
     * @param posteDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PosteDTO> save(PosteDTO posteDTO);

    /**
     * Updates a poste.
     *
     * @param posteDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PosteDTO> update(PosteDTO posteDTO);

    /**
     * Partially updates a poste.
     *
     * @param posteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PosteDTO> partialUpdate(PosteDTO posteDTO);

    /**
     * Get all the postes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PosteDTO> findAll(Pageable pageable);

    /**
     * Returns the number of postes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" poste.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PosteDTO> findOne(Long id);

    /**
     * Delete the "id" poste.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
