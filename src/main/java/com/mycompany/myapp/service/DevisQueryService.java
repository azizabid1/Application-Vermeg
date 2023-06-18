package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.repository.DevisRepository;
import com.mycompany.myapp.service.criteria.DevisCriteria;
import com.mycompany.myapp.service.dto.DevisDTO;
import com.mycompany.myapp.service.mapper.DevisMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Devis} entities in the database.
 * The main input is a {@link DevisCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DevisDTO} or a {@link Page} of {@link DevisDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DevisQueryService extends QueryService<Devis> {

    private final Logger log = LoggerFactory.getLogger(DevisQueryService.class);

    private final DevisRepository devisRepository;

    private final DevisMapper devisMapper;

    public DevisQueryService(DevisRepository devisRepository, DevisMapper devisMapper) {
        this.devisRepository = devisRepository;
        this.devisMapper = devisMapper;
    }

    /**
     * Return a {@link List} of {@link DevisDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DevisDTO> findByCriteria(DevisCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Devis> specification = createSpecification(criteria);
        return devisMapper.toDto(devisRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DevisDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DevisDTO> findByCriteria(DevisCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Devis> specification = createSpecification(criteria);
        return devisRepository.findAll(specification, page).map(devisMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DevisCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Devis> specification = createSpecification(criteria);
        return devisRepository.count(specification);
    }

    /**
     * Function to convert {@link DevisCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Devis> createSpecification(DevisCriteria criteria) {
        Specification<Devis> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Devis_.id));
            }
            if (criteria.getPrixTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrixTotal(), Devis_.prixTotal));
            }
            if (criteria.getPrixHT() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrixHT(), Devis_.prixHT));
            }
            if (criteria.getPrixService() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrixService(), Devis_.prixService));
            }
            if (criteria.getDureeProjet() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDureeProjet(), Devis_.dureeProjet));
            }
            if (criteria.getUserUuid() != null) {
                specification = specification.and(buildSpecification(criteria.getUserUuid(), Devis_.userUuid));
            }
        }
        return specification;
    }
}
