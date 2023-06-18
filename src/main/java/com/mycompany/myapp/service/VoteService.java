package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.VoteDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    VoteDTO save(VoteDTO voteDTO);

    /**
     * Updates a vote.
     *
     * @param voteDTO the entity to update.
     * @return the persisted entity.
     */
    VoteDTO update(VoteDTO voteDTO);

    /**
     * Partially updates a vote.
     *
     * @param voteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VoteDTO> partialUpdate(VoteDTO voteDTO);

    /**
     * Get all the votes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VoteDTO> findAll(Pageable pageable);

    /**
     * Get the "id" vote.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VoteDTO> findOne(Long id);

    /**
     * Delete the "id" vote.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
