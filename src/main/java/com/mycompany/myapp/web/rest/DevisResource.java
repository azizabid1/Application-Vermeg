package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DevisRepository;
import com.mycompany.myapp.service.DevisQueryService;
import com.mycompany.myapp.service.DevisService;
import com.mycompany.myapp.service.criteria.DevisCriteria;
import com.mycompany.myapp.service.dto.DevisDTO;
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

    private final DevisQueryService devisQueryService;

    public DevisResource(DevisService devisService, DevisRepository devisRepository, DevisQueryService devisQueryService) {
        this.devisService = devisService;
        this.devisRepository = devisRepository;
        this.devisQueryService = devisQueryService;
    }

    /**
     * {@code POST  /devis} : Create a new devis.
     *
     * @param devisDTO the devisDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new devisDTO, or with status {@code 400 (Bad Request)} if the devis has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/devis")
    public ResponseEntity<DevisDTO> createDevis(@Valid @RequestBody DevisDTO devisDTO) throws URISyntaxException {
        log.debug("REST request to save Devis : {}", devisDTO);
        if (devisDTO.getId() != null) {
            throw new BadRequestAlertException("A new devis cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DevisDTO result = devisService.save(devisDTO);
        return ResponseEntity
            .created(new URI("/api/devis/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<DevisDTO> updateDevis(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DevisDTO devisDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Devis : {}, {}", id, devisDTO);
        if (devisDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, devisDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!devisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DevisDTO result = devisService.update(devisDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, devisDTO.getId().toString()))
            .body(result);
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
    public ResponseEntity<DevisDTO> partialUpdateDevis(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DevisDTO devisDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Devis partially : {}, {}", id, devisDTO);
        if (devisDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, devisDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!devisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DevisDTO> result = devisService.partialUpdate(devisDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, devisDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /devis} : get all the devis.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of devis in body.
     */
    @GetMapping("/devis")
    public ResponseEntity<List<DevisDTO>> getAllDevis(
        DevisCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Devis by criteria: {}", criteria);
        Page<DevisDTO> page = devisQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /devis/count} : count all the devis.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/devis/count")
    public ResponseEntity<Long> countDevis(DevisCriteria criteria) {
        log.debug("REST request to count Devis by criteria: {}", criteria);
        return ResponseEntity.ok().body(devisQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /devis/:id} : get the "id" devis.
     *
     * @param id the id of the devisDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the devisDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/devis/{id}")
    public ResponseEntity<DevisDTO> getDevis(@PathVariable Long id) {
        log.debug("REST request to get Devis : {}", id);
        Optional<DevisDTO> devisDTO = devisService.findOne(id);
        return ResponseUtil.wrapOrNotFound(devisDTO);
    }

    /**
     * {@code DELETE  /devis/:id} : delete the "id" devis.
     *
     * @param id the id of the devisDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/devis/{id}")
    public ResponseEntity<Void> deleteDevis(@PathVariable Long id) {
        log.debug("REST request to delete Devis : {}", id);
        devisService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
