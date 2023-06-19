package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.repository.ProjetRepository;
import com.mycompany.myapp.service.criteria.ProjetCriteria;
import com.mycompany.myapp.service.dto.ProjetDTO;
import com.mycompany.myapp.service.mapper.ProjetMapper;
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
 * Service for executing complex queries for {@link Projet} entities in the database.
 * The main input is a {@link ProjetCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProjetDTO} or a {@link Page} of {@link ProjetDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjetQueryService extends QueryService<Projet> {

    private final Logger log = LoggerFactory.getLogger(ProjetQueryService.class);

    private final ProjetRepository projetRepository;

    private final ProjetMapper projetMapper;

    public ProjetQueryService(ProjetRepository projetRepository, ProjetMapper projetMapper) {
        this.projetRepository = projetRepository;
        this.projetMapper = projetMapper;
    }

    /**
     * Return a {@link List} of {@link ProjetDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProjetDTO> findByCriteria(ProjetCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Projet> specification = createSpecification(criteria);
        return projetMapper.toDto(projetRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProjetDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjetDTO> findByCriteria(ProjetCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Projet> specification = createSpecification(criteria);
        return projetRepository.findAll(specification, page).map(projetMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjetCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Projet> specification = createSpecification(criteria);
        return projetRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjetCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Projet> createSpecification(ProjetCriteria criteria) {
        Specification<Projet> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Projet_.id));
            }
            if (criteria.getUserUuid() != null) {
                specification = specification.and(buildSpecification(criteria.getUserUuid(), Projet_.userUuid));
            }
            if (criteria.getNomProjet() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomProjet(), Projet_.nomProjet));
            }
            if (criteria.getDateDebut() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateDebut(), Projet_.dateDebut));
            }
            if (criteria.getDateFin() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateFin(), Projet_.dateFin));
            }
            if (criteria.getTechnologies() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTechnologies(), Projet_.technologies));
            }
            if (criteria.getStatusProjet() != null) {
                specification = specification.and(buildSpecification(criteria.getStatusProjet(), Projet_.statusProjet));
            }
            if (criteria.getNombreTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNombreTotal(), Projet_.nombreTotal));
            }
            if (criteria.getNombreRestant() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNombreRestant(), Projet_.nombreRestant));
            }
            if (criteria.getEquipeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEquipeId(), root -> root.join(Projet_.equipe, JoinType.LEFT).get(Equipe_.id))
                    );
            }
            if (criteria.getTachesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTachesId(), root -> root.join(Projet_.taches, JoinType.LEFT).get(Tache_.id))
                    );
            }
            if (criteria.getDevisId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDevisId(), root -> root.join(Projet_.devis, JoinType.LEFT).get(Devis_.id))
                    );
            }
        }
        return specification;
    }
}
