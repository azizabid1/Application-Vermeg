package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.repository.ProjetRepository;
import com.mycompany.myapp.service.ProjetService;
import com.mycompany.myapp.service.dto.ProjetDTO;
import com.mycompany.myapp.service.mapper.ProjetMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Projet}.
 */
@Service
@Transactional
public class ProjetServiceImpl implements ProjetService {

    private final Logger log = LoggerFactory.getLogger(ProjetServiceImpl.class);

    private final ProjetRepository projetRepository;

    private final ProjetMapper projetMapper;

    public ProjetServiceImpl(ProjetRepository projetRepository, ProjetMapper projetMapper) {
        this.projetRepository = projetRepository;
        this.projetMapper = projetMapper;
    }

    @Override
    public ProjetDTO save(ProjetDTO projetDTO) {
        log.debug("Request to save Projet : {}", projetDTO);
        Projet projet = projetMapper.toEntity(projetDTO);
        projet = projetRepository.save(projet);
        return projetMapper.toDto(projet);
    }

    @Override
    public ProjetDTO update(ProjetDTO projetDTO) {
        log.debug("Request to save Projet : {}", projetDTO);
        Projet projet = projetMapper.toEntity(projetDTO);
        projet = projetRepository.save(projet);
        return projetMapper.toDto(projet);
    }

    @Override
    public Optional<ProjetDTO> partialUpdate(ProjetDTO projetDTO) {
        log.debug("Request to partially update Projet : {}", projetDTO);

        return projetRepository
            .findById(projetDTO.getId())
            .map(existingProjet -> {
                projetMapper.partialUpdate(existingProjet, projetDTO);

                return existingProjet;
            })
            .map(projetRepository::save)
            .map(projetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjetDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Projets");
        return projetRepository.findAll(pageable).map(projetMapper::toDto);
    }

    public Page<ProjetDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projetRepository.findAllWithEagerRelationships(pageable).map(projetMapper::toDto);
    }

    /**
     *  Get all the projets where Devis is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProjetDTO> findAllWhereDevisIsNull() {
        log.debug("Request to get all projets where Devis is null");
        return StreamSupport
            .stream(projetRepository.findAll().spliterator(), false)
            .filter(projet -> projet.getDevis() == null)
            .map(projetMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjetDTO> findOne(Long id) {
        log.debug("Request to get Projet : {}", id);
        return projetRepository.findOneWithEagerRelationships(id).map(projetMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Projet : {}", id);
        projetRepository.deleteById(id);
    }
}
