package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.VoteRepository;
import com.mycompany.myapp.service.VoteService;
import com.mycompany.myapp.service.dto.VoteDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

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

    public VoteResource(VoteService voteService, VoteRepository voteRepository) {
        this.voteService = voteService;
        this.voteRepository = voteRepository;
    }

    /**
     * {@code POST  /votes} : Create a new vote.
     *
     * @param voteDTO the voteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new voteDTO, or with status {@code 400 (Bad Request)} if the vote has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/votes")
    public Mono<ResponseEntity<VoteDTO>> createVote(@RequestBody VoteDTO voteDTO) throws URISyntaxException {
        log.debug("REST request to save Vote : {}", voteDTO);
        if (voteDTO.getId() != null) {
            throw new BadRequestAlertException("A new vote cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return voteService
            .save(voteDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/votes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
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
    public Mono<ResponseEntity<VoteDTO>> updateVote(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VoteDTO voteDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Vote : {}, {}", id, voteDTO);
        if (voteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, voteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return voteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return voteService
                    .update(voteDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
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
    public Mono<ResponseEntity<VoteDTO>> partialUpdateVote(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody VoteDTO voteDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Vote partially : {}, {}", id, voteDTO);
        if (voteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, voteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return voteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<VoteDTO> result = voteService.partialUpdate(voteDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /votes} : get all the votes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of votes in body.
     */
    @GetMapping("/votes")
    public Mono<ResponseEntity<List<VoteDTO>>> getAllVotes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Votes");
        return voteService
            .countAll()
            .zipWith(voteService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /votes/:id} : get the "id" vote.
     *
     * @param id the id of the voteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the voteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/votes/{id}")
    public Mono<ResponseEntity<VoteDTO>> getVote(@PathVariable Long id) {
        log.debug("REST request to get Vote : {}", id);
        Mono<VoteDTO> voteDTO = voteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(voteDTO);
    }

    /**
     * {@code DELETE  /votes/:id} : delete the "id" vote.
     *
     * @param id the id of the voteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/votes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteVote(@PathVariable Long id) {
        log.debug("REST request to delete Vote : {}", id);
        return voteService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
