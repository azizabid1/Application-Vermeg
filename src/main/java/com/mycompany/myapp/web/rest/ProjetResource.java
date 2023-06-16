package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ProjetRepository;
import com.mycompany.myapp.service.ProjetService;
import com.mycompany.myapp.service.dto.ProjetDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Projet}.
 */
@RestController
@RequestMapping("/api")
public class ProjetResource {

    private final Logger log = LoggerFactory.getLogger(ProjetResource.class);

    private static final String ENTITY_NAME = "projet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjetService projetService;

    private final ProjetRepository projetRepository;

    public ProjetResource(ProjetService projetService, ProjetRepository projetRepository) {
        this.projetService = projetService;
        this.projetRepository = projetRepository;
    }

    /**
     * {@code POST  /projets} : Create a new projet.
     *
     * @param projetDTO the projetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projetDTO, or with status {@code 400 (Bad Request)} if the projet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/projets")
    public Mono<ResponseEntity<ProjetDTO>> createProjet(@RequestBody ProjetDTO projetDTO) throws URISyntaxException {
        log.debug("REST request to save Projet : {}", projetDTO);
        if (projetDTO.getId() != null) {
            throw new BadRequestAlertException("A new projet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projetService
            .save(projetDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/projets/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /projets/:id} : Updates an existing projet.
     *
     * @param id the id of the projetDTO to save.
     * @param projetDTO the projetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projetDTO,
     * or with status {@code 400 (Bad Request)} if the projetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/projets/{id}")
    public Mono<ResponseEntity<ProjetDTO>> updateProjet(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProjetDTO projetDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Projet : {}, {}", id, projetDTO);
        if (projetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projetRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projetService
                    .update(projetDTO)
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
     * {@code PATCH  /projets/:id} : Partial updates given fields of an existing projet, field will ignore if it is null
     *
     * @param id the id of the projetDTO to save.
     * @param projetDTO the projetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projetDTO,
     * or with status {@code 400 (Bad Request)} if the projetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/projets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjetDTO>> partialUpdateProjet(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProjetDTO projetDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Projet partially : {}, {}", id, projetDTO);
        if (projetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projetRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjetDTO> result = projetService.partialUpdate(projetDTO);

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
     * {@code GET  /projets} : get all the projets.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projets in body.
     */
    @GetMapping("/projets")
    public Mono<ResponseEntity<List<ProjetDTO>>> getAllProjets(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Projets");
        return projetService
            .countAll()
            .zipWith(projetService.findAll(pageable).collectList())
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
     * {@code GET  /projets/:id} : get the "id" projet.
     *
     * @param id the id of the projetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/projets/{id}")
    public Mono<ResponseEntity<ProjetDTO>> getProjet(@PathVariable Long id) {
        log.debug("REST request to get Projet : {}", id);
        Mono<ProjetDTO> projetDTO = projetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projetDTO);
    }

    /**
     * {@code DELETE  /projets/:id} : delete the "id" projet.
     *
     * @param id the id of the projetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/projets/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteProjet(@PathVariable Long id) {
        log.debug("REST request to delete Projet : {}", id);
        return projetService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
