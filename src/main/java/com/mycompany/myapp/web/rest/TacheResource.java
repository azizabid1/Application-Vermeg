package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.service.TacheService;
import com.mycompany.myapp.service.dto.TacheDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Tache}.
 */
@RestController
@RequestMapping("/api")
public class TacheResource {

    private final Logger log = LoggerFactory.getLogger(TacheResource.class);

    private static final String ENTITY_NAME = "tache";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TacheService tacheService;

    private final TacheRepository tacheRepository;

    public TacheResource(TacheService tacheService, TacheRepository tacheRepository) {
        this.tacheService = tacheService;
        this.tacheRepository = tacheRepository;
    }

    /**
     * {@code POST  /taches} : Create a new tache.
     *
     * @param tacheDTO the tacheDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tacheDTO, or with status {@code 400 (Bad Request)} if the tache has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/taches")
    public Mono<ResponseEntity<TacheDTO>> createTache(@RequestBody TacheDTO tacheDTO) throws URISyntaxException {
        log.debug("REST request to save Tache : {}", tacheDTO);
        if (tacheDTO.getId() != null) {
            throw new BadRequestAlertException("A new tache cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tacheService
            .save(tacheDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/taches/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /taches/:id} : Updates an existing tache.
     *
     * @param id the id of the tacheDTO to save.
     * @param tacheDTO the tacheDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tacheDTO,
     * or with status {@code 400 (Bad Request)} if the tacheDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tacheDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/taches/{id}")
    public Mono<ResponseEntity<TacheDTO>> updateTache(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TacheDTO tacheDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Tache : {}, {}", id, tacheDTO);
        if (tacheDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tacheDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tacheRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tacheService
                    .update(tacheDTO)
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
     * {@code PATCH  /taches/:id} : Partial updates given fields of an existing tache, field will ignore if it is null
     *
     * @param id the id of the tacheDTO to save.
     * @param tacheDTO the tacheDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tacheDTO,
     * or with status {@code 400 (Bad Request)} if the tacheDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tacheDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tacheDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/taches/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TacheDTO>> partialUpdateTache(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TacheDTO tacheDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tache partially : {}, {}", id, tacheDTO);
        if (tacheDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tacheDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tacheRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TacheDTO> result = tacheService.partialUpdate(tacheDTO);

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
     * {@code GET  /taches} : get all the taches.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taches in body.
     */
    @GetMapping("/taches")
    public Mono<ResponseEntity<List<TacheDTO>>> getAllTaches(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Taches");
        return tacheService
            .countAll()
            .zipWith(tacheService.findAll(pageable).collectList())
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
     * {@code GET  /taches/:id} : get the "id" tache.
     *
     * @param id the id of the tacheDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tacheDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/taches/{id}")
    public Mono<ResponseEntity<TacheDTO>> getTache(@PathVariable Long id) {
        log.debug("REST request to get Tache : {}", id);
        Mono<TacheDTO> tacheDTO = tacheService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tacheDTO);
    }

    /**
     * {@code DELETE  /taches/:id} : delete the "id" tache.
     *
     * @param id the id of the tacheDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/taches/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteTache(@PathVariable Long id) {
        log.debug("REST request to delete Tache : {}", id);
        return tacheService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
