package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.repository.EquipeRepository;
import com.mycompany.myapp.service.criteria.EquipeCriteria;
import com.mycompany.myapp.service.dto.EquipeDTO;
import com.mycompany.myapp.service.mapper.EquipeMapper;
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
 * Service for executing complex queries for {@link Equipe} entities in the database.
 * The main input is a {@link EquipeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EquipeDTO} or a {@link Page} of {@link EquipeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EquipeQueryService extends QueryService<Equipe> {

    private final Logger log = LoggerFactory.getLogger(EquipeQueryService.class);

    private final EquipeRepository equipeRepository;

    private final EquipeMapper equipeMapper;

    public EquipeQueryService(EquipeRepository equipeRepository, EquipeMapper equipeMapper) {
        this.equipeRepository = equipeRepository;
        this.equipeMapper = equipeMapper;
    }

    /**
     * Return a {@link List} of {@link EquipeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EquipeDTO> findByCriteria(EquipeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Equipe> specification = createSpecification(criteria);
        return equipeMapper.toDto(equipeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EquipeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EquipeDTO> findByCriteria(EquipeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Equipe> specification = createSpecification(criteria);
        return equipeRepository.findAll(specification, page).map(equipeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EquipeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Equipe> specification = createSpecification(criteria);
        return equipeRepository.count(specification);
    }

    /**
     * Function to convert {@link EquipeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Equipe> createSpecification(EquipeCriteria criteria) {
        Specification<Equipe> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Equipe_.id));
            }
            if (criteria.getNom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNom(), Equipe_.nom));
            }
            if (criteria.getNombrePersonne() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNombrePersonne(), Equipe_.nombrePersonne));
            }
            if (criteria.getUsersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUsersId(), root -> root.join(Equipe_.users, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getProjetId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getProjetId(), root -> root.join(Equipe_.projet, JoinType.LEFT).get(Projet_.id))
                    );
            }
            if (criteria.getVoteId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVoteId(), root -> root.join(Equipe_.vote, JoinType.LEFT).get(Vote_.id))
                    );
            }
        }
        return specification;
    }
}
