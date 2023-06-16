package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.repository.ProjetRepository;
import com.mycompany.myapp.service.ProjetService;
import com.mycompany.myapp.service.dto.ProjetDTO;
import com.mycompany.myapp.service.mapper.ProjetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<ProjetDTO> save(ProjetDTO projetDTO) {
        log.debug("Request to save Projet : {}", projetDTO);
        return projetRepository.save(projetMapper.toEntity(projetDTO)).map(projetMapper::toDto);
    }

    @Override
    public Mono<ProjetDTO> update(ProjetDTO projetDTO) {
        log.debug("Request to save Projet : {}", projetDTO);
        return projetRepository.save(projetMapper.toEntity(projetDTO)).map(projetMapper::toDto);
    }

    @Override
    public Mono<ProjetDTO> partialUpdate(ProjetDTO projetDTO) {
        log.debug("Request to partially update Projet : {}", projetDTO);

        return projetRepository
            .findById(projetDTO.getId())
            .map(existingProjet -> {
                projetMapper.partialUpdate(existingProjet, projetDTO);

                return existingProjet;
            })
            .flatMap(projetRepository::save)
            .map(projetMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjetDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Projets");
        return projetRepository.findAllBy(pageable).map(projetMapper::toDto);
    }

    public Mono<Long> countAll() {
        return projetRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProjetDTO> findOne(Long id) {
        log.debug("Request to get Projet : {}", id);
        return projetRepository.findById(id).map(projetMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Projet : {}", id);
        return projetRepository.deleteById(id);
    }
}
