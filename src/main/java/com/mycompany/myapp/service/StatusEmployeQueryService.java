package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.StatusEmploye;
import com.mycompany.myapp.repository.StatusEmployeRepository;
import com.mycompany.myapp.service.criteria.StatusEmployeCriteria;
import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import com.mycompany.myapp.service.mapper.StatusEmployeMapper;
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
 * Service for executing complex queries for {@link StatusEmploye} entities in the database.
 * The main input is a {@link StatusEmployeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StatusEmployeDTO} or a {@link Page} of {@link StatusEmployeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StatusEmployeQueryService extends QueryService<StatusEmploye> {

    private final Logger log = LoggerFactory.getLogger(StatusEmployeQueryService.class);

    private final StatusEmployeRepository statusEmployeRepository;

    private final StatusEmployeMapper statusEmployeMapper;

    public StatusEmployeQueryService(StatusEmployeRepository statusEmployeRepository, StatusEmployeMapper statusEmployeMapper) {
        this.statusEmployeRepository = statusEmployeRepository;
        this.statusEmployeMapper = statusEmployeMapper;
    }

    /**
     * Return a {@link List} of {@link StatusEmployeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StatusEmployeDTO> findByCriteria(StatusEmployeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StatusEmploye> specification = createSpecification(criteria);
        return statusEmployeMapper.toDto(statusEmployeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StatusEmployeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StatusEmployeDTO> findByCriteria(StatusEmployeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StatusEmploye> specification = createSpecification(criteria);
        return statusEmployeRepository.findAll(specification, page).map(statusEmployeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StatusEmployeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StatusEmploye> specification = createSpecification(criteria);
        return statusEmployeRepository.count(specification);
    }

    /**
     * Function to convert {@link StatusEmployeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StatusEmploye> createSpecification(StatusEmployeCriteria criteria) {
        Specification<StatusEmploye> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StatusEmploye_.id));
            }
            if (criteria.getUserUuid() != null) {
                specification = specification.and(buildSpecification(criteria.getUserUuid(), StatusEmploye_.userUuid));
            }
            if (criteria.getDisponibilite() != null) {
                specification = specification.and(buildSpecification(criteria.getDisponibilite(), StatusEmploye_.disponibilite));
            }
            if (criteria.getMission() != null) {
                specification = specification.and(buildSpecification(criteria.getMission(), StatusEmploye_.mission));
            }
            if (criteria.getDebutConge() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDebutConge(), StatusEmploye_.debutConge));
            }
            if (criteria.getFinConge() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFinConge(), StatusEmploye_.finConge));
            }
            if (criteria.getUserIdId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserIdId(), root -> root.join(StatusEmploye_.userId, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
