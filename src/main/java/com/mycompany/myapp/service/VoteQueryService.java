package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Vote;
import com.mycompany.myapp.repository.VoteRepository;
import com.mycompany.myapp.service.criteria.VoteCriteria;
import com.mycompany.myapp.service.dto.VoteDTO;
import com.mycompany.myapp.service.mapper.VoteMapper;
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
 * Service for executing complex queries for {@link Vote} entities in the database.
 * The main input is a {@link VoteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link VoteDTO} or a {@link Page} of {@link VoteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VoteQueryService extends QueryService<Vote> {

    private final Logger log = LoggerFactory.getLogger(VoteQueryService.class);

    private final VoteRepository voteRepository;

    private final VoteMapper voteMapper;

    public VoteQueryService(VoteRepository voteRepository, VoteMapper voteMapper) {
        this.voteRepository = voteRepository;
        this.voteMapper = voteMapper;
    }

    /**
     * Return a {@link List} of {@link VoteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<VoteDTO> findByCriteria(VoteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Vote> specification = createSpecification(criteria);
        return voteMapper.toDto(voteRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link VoteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VoteDTO> findByCriteria(VoteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Vote> specification = createSpecification(criteria);
        return voteRepository.findAll(specification, page).map(voteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VoteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Vote> specification = createSpecification(criteria);
        return voteRepository.count(specification);
    }

    /**
     * Function to convert {@link VoteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Vote> createSpecification(VoteCriteria criteria) {
        Specification<Vote> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Vote_.id));
            }
            if (criteria.getUserUuid() != null) {
                specification = specification.and(buildSpecification(criteria.getUserUuid(), Vote_.userUuid));
            }
            if (criteria.getTypeVote() != null) {
                specification = specification.and(buildSpecification(criteria.getTypeVote(), Vote_.typeVote));
            }
            if (criteria.getEquipeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEquipeId(), root -> root.join(Vote_.equipes, JoinType.LEFT).get(Equipe_.id))
                    );
            }
        }
        return specification;
    }
}
