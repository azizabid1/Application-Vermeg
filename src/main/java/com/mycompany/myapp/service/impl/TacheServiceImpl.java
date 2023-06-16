package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.service.TacheService;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.mapper.TacheMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Tache}.
 */
@Service
@Transactional
public class TacheServiceImpl implements TacheService {

    private final Logger log = LoggerFactory.getLogger(TacheServiceImpl.class);

    private final TacheRepository tacheRepository;

    private final TacheMapper tacheMapper;

    public TacheServiceImpl(TacheRepository tacheRepository, TacheMapper tacheMapper) {
        this.tacheRepository = tacheRepository;
        this.tacheMapper = tacheMapper;
    }

    @Override
    public Mono<TacheDTO> save(TacheDTO tacheDTO) {
        log.debug("Request to save Tache : {}", tacheDTO);
        return tacheRepository.save(tacheMapper.toEntity(tacheDTO)).map(tacheMapper::toDto);
    }

    @Override
    public Mono<TacheDTO> update(TacheDTO tacheDTO) {
        log.debug("Request to save Tache : {}", tacheDTO);
        return tacheRepository.save(tacheMapper.toEntity(tacheDTO)).map(tacheMapper::toDto);
    }

    @Override
    public Mono<TacheDTO> partialUpdate(TacheDTO tacheDTO) {
        log.debug("Request to partially update Tache : {}", tacheDTO);

        return tacheRepository
            .findById(tacheDTO.getId())
            .map(existingTache -> {
                tacheMapper.partialUpdate(existingTache, tacheDTO);

                return existingTache;
            })
            .flatMap(tacheRepository::save)
            .map(tacheMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TacheDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Taches");
        return tacheRepository.findAllBy(pageable).map(tacheMapper::toDto);
    }

    public Mono<Long> countAll() {
        return tacheRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TacheDTO> findOne(Long id) {
        log.debug("Request to get Tache : {}", id);
        return tacheRepository.findById(id).map(tacheMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Tache : {}", id);
        return tacheRepository.deleteById(id);
    }
}
