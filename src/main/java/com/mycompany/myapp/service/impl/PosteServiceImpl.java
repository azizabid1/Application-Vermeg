package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Poste;
import com.mycompany.myapp.repository.PosteRepository;
import com.mycompany.myapp.service.PosteService;
import com.mycompany.myapp.service.dto.PosteDTO;
import com.mycompany.myapp.service.mapper.PosteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Poste}.
 */
@Service
@Transactional
public class PosteServiceImpl implements PosteService {

    private final Logger log = LoggerFactory.getLogger(PosteServiceImpl.class);

    private final PosteRepository posteRepository;

    private final PosteMapper posteMapper;

    public PosteServiceImpl(PosteRepository posteRepository, PosteMapper posteMapper) {
        this.posteRepository = posteRepository;
        this.posteMapper = posteMapper;
    }

    @Override
    public PosteDTO save(PosteDTO posteDTO) {
        log.debug("Request to save Poste : {}", posteDTO);
        Poste poste = posteMapper.toEntity(posteDTO);
        poste = posteRepository.save(poste);
        return posteMapper.toDto(poste);
    }

    @Override
    public PosteDTO update(PosteDTO posteDTO) {
        log.debug("Request to save Poste : {}", posteDTO);
        Poste poste = posteMapper.toEntity(posteDTO);
        poste = posteRepository.save(poste);
        return posteMapper.toDto(poste);
    }

    @Override
    public Optional<PosteDTO> partialUpdate(PosteDTO posteDTO) {
        log.debug("Request to partially update Poste : {}", posteDTO);

        return posteRepository
            .findById(posteDTO.getId())
            .map(existingPoste -> {
                posteMapper.partialUpdate(existingPoste, posteDTO);

                return existingPoste;
            })
            .map(posteRepository::save)
            .map(posteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PosteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Postes");
        return posteRepository.findAll(pageable).map(posteMapper::toDto);
    }

    public Page<PosteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return posteRepository.findAllWithEagerRelationships(pageable).map(posteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PosteDTO> findOne(Long id) {
        log.debug("Request to get Poste : {}", id);
        return posteRepository.findOneWithEagerRelationships(id).map(posteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Poste : {}", id);
        posteRepository.deleteById(id);
    }
}
