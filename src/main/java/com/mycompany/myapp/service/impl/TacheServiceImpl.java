package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.service.TacheService;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.mapper.TacheMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TacheDTO save(TacheDTO tacheDTO) {
        log.debug("Request to save Tache : {}", tacheDTO);
        Tache tache = tacheMapper.toEntity(tacheDTO);
        tache = tacheRepository.save(tache);
        return tacheMapper.toDto(tache);
    }

    @Override
    public TacheDTO update(TacheDTO tacheDTO) {
        log.debug("Request to save Tache : {}", tacheDTO);
        Tache tache = tacheMapper.toEntity(tacheDTO);
        tache = tacheRepository.save(tache);
        return tacheMapper.toDto(tache);
    }

    @Override
    public Optional<TacheDTO> partialUpdate(TacheDTO tacheDTO) {
        log.debug("Request to partially update Tache : {}", tacheDTO);

        return tacheRepository
            .findById(tacheDTO.getId())
            .map(existingTache -> {
                tacheMapper.partialUpdate(existingTache, tacheDTO);

                return existingTache;
            })
            .map(tacheRepository::save)
            .map(tacheMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TacheDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Taches");
        return tacheRepository.findAll(pageable).map(tacheMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TacheDTO> findOne(Long id) {
        log.debug("Request to get Tache : {}", id);
        return tacheRepository.findById(id).map(tacheMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tache : {}", id);
        tacheRepository.deleteById(id);
    }
}
