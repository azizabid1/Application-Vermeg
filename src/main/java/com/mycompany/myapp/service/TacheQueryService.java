package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.service.criteria.TacheCriteria;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.mapper.TacheMapper;
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
 * Service for executing complex queries for {@link Tache} entities in the database.
 * The main input is a {@link TacheCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TacheDTO} or a {@link Page} of {@link TacheDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TacheQueryService extends QueryService<Tache> {

    private final Logger log = LoggerFactory.getLogger(TacheQueryService.class);

    private final TacheRepository tacheRepository;

    private final TacheMapper tacheMapper;

    public TacheQueryService(TacheRepository tacheRepository, TacheMapper tacheMapper) {
        this.tacheRepository = tacheRepository;
        this.tacheMapper = tacheMapper;
    }

    /**
     * Return a {@link List} of {@link TacheDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TacheDTO> findByCriteria(TacheCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Tache> specification = createSpecification(criteria);
        return tacheMapper.toDto(tacheRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TacheDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TacheDTO> findByCriteria(TacheCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tache> specification = createSpecification(criteria);
        return tacheRepository.findAll(specification, page).map(tacheMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TacheCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Tache> specification = createSpecification(criteria);
        return tacheRepository.count(specification);
    }

    /**
     * Function to convert {@link TacheCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Tache> createSpecification(TacheCriteria criteria) {
        Specification<Tache> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Tache_.id));
            }
            if (criteria.getUserUuid() != null) {
                specification = specification.and(buildSpecification(criteria.getUserUuid(), Tache_.userUuid));
            }
            if (criteria.getTitre() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitre(), Tache_.titre));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Tache_.description));
            }
            if (criteria.getStatusTache() != null) {
                specification = specification.and(buildSpecification(criteria.getStatusTache(), Tache_.statusTache));
            }
            if (criteria.getProjetsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getProjetsId(), root -> root.join(Tache_.projets, JoinType.LEFT).get(Projet_.id))
                    );
            }
        }
        return specification;
    }
}
