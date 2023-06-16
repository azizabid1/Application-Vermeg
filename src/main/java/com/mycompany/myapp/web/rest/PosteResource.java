package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PosteRepository;
import com.mycompany.myapp.service.PosteService;
import com.mycompany.myapp.service.dto.PosteDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Poste}.
 */
@RestController
@RequestMapping("/api")
public class PosteResource {

    private final Logger log = LoggerFactory.getLogger(PosteResource.class);

    private static final String ENTITY_NAME = "poste";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PosteService posteService;

    private final PosteRepository posteRepository;

    public PosteResource(PosteService posteService, PosteRepository posteRepository) {
        this.posteService = posteService;
        this.posteRepository = posteRepository;
    }

    /**
     * {@code POST  /postes} : Create a new poste.
     *
     * @param posteDTO the posteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new posteDTO, or with status {@code 400 (Bad Request)} if the poste has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/postes")
    public Mono<ResponseEntity<PosteDTO>> createPoste(@RequestBody PosteDTO posteDTO) throws URISyntaxException {
        log.debug("REST request to save Poste : {}", posteDTO);
        if (posteDTO.getId() != null) {
            throw new BadRequestAlertException("A new poste cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return posteService
            .save(posteDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/postes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /postes/:id} : Updates an existing poste.
     *
     * @param id the id of the posteDTO to save.
     * @param posteDTO the posteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posteDTO,
     * or with status {@code 400 (Bad Request)} if the posteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the posteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/postes/{id}")
    public Mono<ResponseEntity<PosteDTO>> updatePoste(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PosteDTO posteDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Poste : {}, {}", id, posteDTO);
        if (posteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, posteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return posteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return posteService
                    .update(posteDTO)
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
     * {@code PATCH  /postes/:id} : Partial updates given fields of an existing poste, field will ignore if it is null
     *
     * @param id the id of the posteDTO to save.
     * @param posteDTO the posteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posteDTO,
     * or with status {@code 400 (Bad Request)} if the posteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the posteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the posteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/postes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PosteDTO>> partialUpdatePoste(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PosteDTO posteDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Poste partially : {}, {}", id, posteDTO);
        if (posteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, posteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return posteRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PosteDTO> result = posteService.partialUpdate(posteDTO);

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
     * {@code GET  /postes} : get all the postes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of postes in body.
     */
    @GetMapping("/postes")
    public Mono<ResponseEntity<List<PosteDTO>>> getAllPostes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Postes");
        return posteService
            .countAll()
            .zipWith(posteService.findAll(pageable).collectList())
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
     * {@code GET  /postes/:id} : get the "id" poste.
     *
     * @param id the id of the posteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the posteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/postes/{id}")
    public Mono<ResponseEntity<PosteDTO>> getPoste(@PathVariable Long id) {
        log.debug("REST request to get Poste : {}", id);
        Mono<PosteDTO> posteDTO = posteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(posteDTO);
    }

    /**
     * {@code DELETE  /postes/:id} : delete the "id" poste.
     *
     * @param id the id of the posteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/postes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePoste(@PathVariable Long id) {
        log.debug("REST request to delete Poste : {}", id);
        return posteService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
