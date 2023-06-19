package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Departement;
import com.mycompany.myapp.repository.DepartementRepository;
import com.mycompany.myapp.service.criteria.DepartementCriteria;
import com.mycompany.myapp.service.dto.DepartementDTO;
import com.mycompany.myapp.service.mapper.DepartementMapper;
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
 * Service for executing complex queries for {@link Departement} entities in the database.
 * The main input is a {@link DepartementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DepartementDTO} or a {@link Page} of {@link DepartementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DepartementQueryService extends QueryService<Departement> {

    private final Logger log = LoggerFactory.getLogger(DepartementQueryService.class);

    private final DepartementRepository departementRepository;

    private final DepartementMapper departementMapper;

    public DepartementQueryService(DepartementRepository departementRepository, DepartementMapper departementMapper) {
        this.departementRepository = departementRepository;
        this.departementMapper = departementMapper;
    }

    /**
     * Return a {@link List} of {@link DepartementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DepartementDTO> findByCriteria(DepartementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Departement> specification = createSpecification(criteria);
        return departementMapper.toDto(departementRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DepartementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DepartementDTO> findByCriteria(DepartementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Departement> specification = createSpecification(criteria);
        return departementRepository.findAll(specification, page).map(departementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DepartementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Departement> specification = createSpecification(criteria);
        return departementRepository.count(specification);
    }

    /**
     * Function to convert {@link DepartementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Departement> createSpecification(DepartementCriteria criteria) {
        Specification<Departement> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Departement_.id));
            }
            if (criteria.getNom() != null) {
                specification = specification.and(buildSpecification(criteria.getNom(), Departement_.nom));
            }
            if (criteria.getUserUuid() != null) {
                specification = specification.and(buildSpecification(criteria.getUserUuid(), Departement_.userUuid));
            }
            if (criteria.getUsersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUsersId(), root -> root.join(Departement_.users, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
