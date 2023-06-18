package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.VoteRepository;
import com.mycompany.myapp.service.VoteQueryService;
import com.mycompany.myapp.service.VoteService;
import com.mycompany.myapp.service.criteria.VoteCriteria;
import com.mycompany.myapp.service.dto.VoteDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Vote}.
 */
@RestController
@RequestMapping("/api")
public class VoteResource {

    private final Logger log = LoggerFactory.getLogger(VoteResource.class);

    private static final String ENTITY_NAME = "vote";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VoteService voteService;

    private final VoteRepository voteRepository;

    private final VoteQueryService voteQueryService;

    public VoteResource(VoteService voteService, VoteRepository voteRepository, VoteQueryService voteQueryService) {
        this.voteService = voteService;
        this.voteRepository = voteRepository;
        this.voteQueryService = voteQueryService;
    }

    /**
     * {@code POST  /votes} : Create a new vote.
     *
     * @param voteDTO the voteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new voteDTO, or with status {@code 400 (Bad Request)} if the vote has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/votes")
    public ResponseEntity<VoteDTO> createVote(@Valid @RequestBody VoteDTO voteDTO) throws URISyntaxException {
        log.debug("REST request to save Vote : {}", voteDTO);
        if (voteDTO.getId() != null) {
            throw new BadRequestAlertException("A new vote cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VoteDTO result = voteService.save(voteDTO);
        return ResponseEntity
            .created(new URI("/api/votes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /votes/:id} : Updates an existing vote.
     *
     * @param id the id of the voteDTO to save.
     * @param voteDTO the voteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated voteDTO,
     * or with status {@code 400 (Bad Request)} if the voteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the voteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/votes/{id}")
    public ResponseEntity<VoteDTO> updateVote(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VoteDTO voteDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Vote : {}, {}", id, voteDTO);
        if (voteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, voteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!voteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        VoteDTO result = voteService.update(voteDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, voteDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /votes/:id} : Partial updates given fields of an existing vote, field will ignore if it is null
     *
     * @param id the id of the voteDTO to save.
     * @param voteDTO the voteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated voteDTO,
     * or with status {@code 400 (Bad Request)} if the voteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the voteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the voteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/votes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VoteDTO> partialUpdateVote(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VoteDTO voteDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Vote partially : {}, {}", id, voteDTO);
        if (voteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, voteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!voteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VoteDTO> result = voteService.partialUpdate(voteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, voteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /votes} : get all the votes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of votes in body.
     */
    @GetMapping("/votes")
    public ResponseEntity<List<VoteDTO>> getAllVotes(
        VoteCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Votes by criteria: {}", criteria);
        Page<VoteDTO> page = voteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /votes/count} : count all the votes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/votes/count")
    public ResponseEntity<Long> countVotes(VoteCriteria criteria) {
        log.debug("REST request to count Votes by criteria: {}", criteria);
        return ResponseEntity.ok().body(voteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /votes/:id} : get the "id" vote.
     *
     * @param id the id of the voteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the voteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/votes/{id}")
    public ResponseEntity<VoteDTO> getVote(@PathVariable Long id) {
        log.debug("REST request to get Vote : {}", id);
        Optional<VoteDTO> voteDTO = voteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(voteDTO);
    }

    /**
     * {@code DELETE  /votes/:id} : delete the "id" vote.
     *
     * @param id the id of the voteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/votes/{id}")
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        log.debug("REST request to delete Vote : {}", id);
        voteService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
