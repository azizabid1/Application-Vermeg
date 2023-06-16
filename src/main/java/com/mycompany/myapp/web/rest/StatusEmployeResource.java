package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.StatusEmployeRepository;
import com.mycompany.myapp.service.StatusEmployeService;
import com.mycompany.myapp.service.dto.StatusEmployeDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.StatusEmploye}.
 */
@RestController
@RequestMapping("/api")
public class StatusEmployeResource {

    private final Logger log = LoggerFactory.getLogger(StatusEmployeResource.class);

    private static final String ENTITY_NAME = "statusEmploye";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatusEmployeService statusEmployeService;

    private final StatusEmployeRepository statusEmployeRepository;

    public StatusEmployeResource(StatusEmployeService statusEmployeService, StatusEmployeRepository statusEmployeRepository) {
        this.statusEmployeService = statusEmployeService;
        this.statusEmployeRepository = statusEmployeRepository;
    }

    /**
     * {@code POST  /status-employes} : Create a new statusEmploye.
     *
     * @param statusEmployeDTO the statusEmployeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new statusEmployeDTO, or with status {@code 400 (Bad Request)} if the statusEmploye has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/status-employes")
    public Mono<ResponseEntity<StatusEmployeDTO>> createStatusEmploye(@RequestBody StatusEmployeDTO statusEmployeDTO)
        throws URISyntaxException {
        log.debug("REST request to save StatusEmploye : {}", statusEmployeDTO);
        if (statusEmployeDTO.getId() != null) {
            throw new BadRequestAlertException("A new statusEmploye cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return statusEmployeService
            .save(statusEmployeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/status-employes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /status-employes/:id} : Updates an existing statusEmploye.
     *
     * @param id the id of the statusEmployeDTO to save.
     * @param statusEmployeDTO the statusEmployeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusEmployeDTO,
     * or with status {@code 400 (Bad Request)} if the statusEmployeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the statusEmployeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/status-employes/{id}")
    public Mono<ResponseEntity<StatusEmployeDTO>> updateStatusEmploye(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StatusEmployeDTO statusEmployeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StatusEmploye : {}, {}", id, statusEmployeDTO);
        if (statusEmployeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusEmployeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return statusEmployeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return statusEmployeService
                    .update(statusEmployeDTO)
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
     * {@code PATCH  /status-employes/:id} : Partial updates given fields of an existing statusEmploye, field will ignore if it is null
     *
     * @param id the id of the statusEmployeDTO to save.
     * @param statusEmployeDTO the statusEmployeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated statusEmployeDTO,
     * or with status {@code 400 (Bad Request)} if the statusEmployeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the statusEmployeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the statusEmployeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/status-employes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<StatusEmployeDTO>> partialUpdateStatusEmploye(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StatusEmployeDTO statusEmployeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StatusEmploye partially : {}, {}", id, statusEmployeDTO);
        if (statusEmployeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, statusEmployeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return statusEmployeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<StatusEmployeDTO> result = statusEmployeService.partialUpdate(statusEmployeDTO);

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
     * {@code GET  /status-employes} : get all the statusEmployes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statusEmployes in body.
     */
    @GetMapping("/status-employes")
    public Mono<ResponseEntity<List<StatusEmployeDTO>>> getAllStatusEmployes(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of StatusEmployes");
        return statusEmployeService
            .countAll()
            .zipWith(statusEmployeService.findAll(pageable).collectList())
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
     * {@code GET  /status-employes/:id} : get the "id" statusEmploye.
     *
     * @param id the id of the statusEmployeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the statusEmployeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/status-employes/{id}")
    public Mono<ResponseEntity<StatusEmployeDTO>> getStatusEmploye(@PathVariable Long id) {
        log.debug("REST request to get StatusEmploye : {}", id);
        Mono<StatusEmployeDTO> statusEmployeDTO = statusEmployeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(statusEmployeDTO);
    }

    /**
     * {@code DELETE  /status-employes/:id} : delete the "id" statusEmploye.
     *
     * @param id the id of the statusEmployeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/status-employes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteStatusEmploye(@PathVariable Long id) {
        log.debug("REST request to delete StatusEmploye : {}", id);
        return statusEmployeService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
