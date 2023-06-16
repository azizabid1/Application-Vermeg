package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Poste;
import com.mycompany.myapp.repository.PosteRepository;
import com.mycompany.myapp.service.PosteService;
import com.mycompany.myapp.service.dto.PosteDTO;
import com.mycompany.myapp.service.mapper.PosteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<PosteDTO> save(PosteDTO posteDTO) {
        log.debug("Request to save Poste : {}", posteDTO);
        return posteRepository.save(posteMapper.toEntity(posteDTO)).map(posteMapper::toDto);
    }

    @Override
    public Mono<PosteDTO> update(PosteDTO posteDTO) {
        log.debug("Request to save Poste : {}", posteDTO);
        return posteRepository.save(posteMapper.toEntity(posteDTO)).map(posteMapper::toDto);
    }

    @Override
    public Mono<PosteDTO> partialUpdate(PosteDTO posteDTO) {
        log.debug("Request to partially update Poste : {}", posteDTO);

        return posteRepository
            .findById(posteDTO.getId())
            .map(existingPoste -> {
                posteMapper.partialUpdate(existingPoste, posteDTO);

                return existingPoste;
            })
            .flatMap(posteRepository::save)
            .map(posteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PosteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Postes");
        return posteRepository.findAllBy(pageable).map(posteMapper::toDto);
    }

    public Mono<Long> countAll() {
        return posteRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PosteDTO> findOne(Long id) {
        log.debug("Request to get Poste : {}", id);
        return posteRepository.findById(id).map(posteMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Poste : {}", id);
        return posteRepository.deleteById(id);
    }
}
