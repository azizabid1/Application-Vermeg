package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.VoteDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Vote}.
 */
public interface VoteService {
    /**
     * Save a vote.
     *
     * @param voteDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<VoteDTO> save(VoteDTO voteDTO);

    /**
     * Updates a vote.
     *
     * @param voteDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<VoteDTO> update(VoteDTO voteDTO);

    /**
     * Partially updates a vote.
     *
     * @param voteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<VoteDTO> partialUpdate(VoteDTO voteDTO);

    /**
     * Get all the votes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<VoteDTO> findAll(Pageable pageable);

    /**
     * Returns the number of votes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" vote.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<VoteDTO> findOne(Long id);

    /**
     * Delete the "id" vote.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
