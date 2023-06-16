package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.repository.EquipeRepository;
import com.mycompany.myapp.service.EquipeService;
import com.mycompany.myapp.service.dto.EquipeDTO;
import com.mycompany.myapp.service.mapper.EquipeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Equipe}.
 */
@Service
@Transactional
public class EquipeServiceImpl implements EquipeService {

    private final Logger log = LoggerFactory.getLogger(EquipeServiceImpl.class);

    private final EquipeRepository equipeRepository;

    private final EquipeMapper equipeMapper;

    public EquipeServiceImpl(EquipeRepository equipeRepository, EquipeMapper equipeMapper) {
        this.equipeRepository = equipeRepository;
        this.equipeMapper = equipeMapper;
    }

    @Override
    public Mono<EquipeDTO> save(EquipeDTO equipeDTO) {
        log.debug("Request to save Equipe : {}", equipeDTO);
        return equipeRepository.save(equipeMapper.toEntity(equipeDTO)).map(equipeMapper::toDto);
    }

    @Override
    public Mono<EquipeDTO> update(EquipeDTO equipeDTO) {
        log.debug("Request to save Equipe : {}", equipeDTO);
        return equipeRepository.save(equipeMapper.toEntity(equipeDTO)).map(equipeMapper::toDto);
    }

    @Override
    public Mono<EquipeDTO> partialUpdate(EquipeDTO equipeDTO) {
        log.debug("Request to partially update Equipe : {}", equipeDTO);

        return equipeRepository
            .findById(equipeDTO.getId())
            .map(existingEquipe -> {
                equipeMapper.partialUpdate(existingEquipe, equipeDTO);

                return existingEquipe;
            })
            .flatMap(equipeRepository::save)
            .map(equipeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EquipeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Equipes");
        return equipeRepository.findAllBy(pageable).map(equipeMapper::toDto);
    }

    public Flux<EquipeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return equipeRepository.findAllWithEagerRelationships(pageable).map(equipeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return equipeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EquipeDTO> findOne(Long id) {
        log.debug("Request to get Equipe : {}", id);
        return equipeRepository.findOneWithEagerRelationships(id).map(equipeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Equipe : {}", id);
        return equipeRepository.deleteById(id);
    }
}
