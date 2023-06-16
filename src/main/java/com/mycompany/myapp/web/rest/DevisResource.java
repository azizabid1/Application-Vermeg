package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DevisRepository;
import com.mycompany.myapp.service.DevisService;
import com.mycompany.myapp.service.dto.DevisDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Devis}.
 */
@RestController
@RequestMapping("/api")
public class DevisResource {

    private final Logger log = LoggerFactory.getLogger(DevisResource.class);

    private static final String ENTITY_NAME = "devis";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DevisService devisService;

    private final DevisRepository devisRepository;

    public DevisResource(DevisService devisService, DevisRepository devisRepository) {
        this.devisService = devisService;
        this.devisRepository = devisRepository;
    }

    /**
     * {@code POST  /devis} : Create a new devis.
     *
     * @param devisDTO the devisDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new devisDTO, or with status {@code 400 (Bad Request)} if the devis has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/devis")
    public Mono<ResponseEntity<DevisDTO>> createDevis(@RequestBody DevisDTO devisDTO) throws URISyntaxException {
        log.debug("REST request to save Devis : {}", devisDTO);
        if (devisDTO.getId() != null) {
            throw new BadRequestAlertException("A new devis cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return devisService
            .save(devisDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/devis/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /devis/:id} : Updates an existing devis.
     *
     * @param id the id of the devisDTO to save.
     * @param devisDTO the devisDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated devisDTO,
     * or with status {@code 400 (Bad Request)} if the devisDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the devisDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/devis/{id}")
    public Mono<ResponseEntity<DevisDTO>> updateDevis(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DevisDTO devisDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Devis : {}, {}", id, devisDTO);
        if (devisDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, devisDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return devisRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return devisService
                    .update(devisDTO)
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
     * {@code PATCH  /devis/:id} : Partial updates given fields of an existing devis, field will ignore if it is null
     *
     * @param id the id of the devisDTO to save.
     * @param devisDTO the devisDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated devisDTO,
     * or with status {@code 400 (Bad Request)} if the devisDTO is not valid,
     * or with status {@code 404 (Not Found)} if the devisDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the devisDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/devis/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DevisDTO>> partialUpdateDevis(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DevisDTO devisDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Devis partially : {}, {}", id, devisDTO);
        if (devisDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, devisDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return devisRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DevisDTO> result = devisService.partialUpdate(devisDTO);

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
     * {@code GET  /devis} : get all the devis.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of devis in body.
     */
    @GetMapping("/devis")
    public Mono<ResponseEntity<List<DevisDTO>>> getAllDevis(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Devis");
        return devisService
            .countAll()
            .zipWith(devisService.findAll(pageable).collectList())
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
     * {@code GET  /devis/:id} : get the "id" devis.
     *
     * @param id the id of the devisDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the devisDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/devis/{id}")
    public Mono<ResponseEntity<DevisDTO>> getDevis(@PathVariable Long id) {
        log.debug("REST request to get Devis : {}", id);
        Mono<DevisDTO> devisDTO = devisService.findOne(id);
        return ResponseUtil.wrapOrNotFound(devisDTO);
    }

    /**
     * {@code DELETE  /devis/:id} : delete the "id" devis.
     *
     * @param id the id of the devisDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/devis/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteDevis(@PathVariable Long id) {
        log.debug("REST request to delete Devis : {}", id);
        return devisService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
