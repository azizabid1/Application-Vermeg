package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Poste;
import com.mycompany.myapp.repository.PosteRepository;
import com.mycompany.myapp.service.criteria.PosteCriteria;
import com.mycompany.myapp.service.dto.PosteDTO;
import com.mycompany.myapp.service.mapper.PosteMapper;
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
 * Service for executing complex queries for {@link Poste} entities in the database.
 * The main input is a {@link PosteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PosteDTO} or a {@link Page} of {@link PosteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PosteQueryService extends QueryService<Poste> {

    private final Logger log = LoggerFactory.getLogger(PosteQueryService.class);

    private final PosteRepository posteRepository;

    private final PosteMapper posteMapper;

    public PosteQueryService(PosteRepository posteRepository, PosteMapper posteMapper) {
        this.posteRepository = posteRepository;
        this.posteMapper = posteMapper;
    }

    /**
     * Return a {@link List} of {@link PosteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PosteDTO> findByCriteria(PosteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Poste> specification = createSpecification(criteria);
        return posteMapper.toDto(posteRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PosteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PosteDTO> findByCriteria(PosteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Poste> specification = createSpecification(criteria);
        return posteRepository.findAll(specification, page).map(posteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PosteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Poste> specification = createSpecification(criteria);
        return posteRepository.count(specification);
    }

    /**
     * Function to convert {@link PosteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Poste> createSpecification(PosteCriteria criteria) {
        Specification<Poste> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Poste_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Poste_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Poste_.description));
            }
            if (criteria.getUsersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUsersId(), root -> root.join(Poste_.users, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
