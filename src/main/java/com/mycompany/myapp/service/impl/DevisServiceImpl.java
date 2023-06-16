package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.repository.DevisRepository;
import com.mycompany.myapp.service.DevisService;
import com.mycompany.myapp.service.dto.DevisDTO;
import com.mycompany.myapp.service.mapper.DevisMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Devis}.
 */
@Service
@Transactional
public class DevisServiceImpl implements DevisService {

    private final Logger log = LoggerFactory.getLogger(DevisServiceImpl.class);

    private final DevisRepository devisRepository;

    private final DevisMapper devisMapper;

    public DevisServiceImpl(DevisRepository devisRepository, DevisMapper devisMapper) {
        this.devisRepository = devisRepository;
        this.devisMapper = devisMapper;
    }

    @Override
    public Mono<DevisDTO> save(DevisDTO devisDTO) {
        log.debug("Request to save Devis : {}", devisDTO);
        return devisRepository.save(devisMapper.toEntity(devisDTO)).map(devisMapper::toDto);
    }

    @Override
    public Mono<DevisDTO> update(DevisDTO devisDTO) {
        log.debug("Request to save Devis : {}", devisDTO);
        return devisRepository.save(devisMapper.toEntity(devisDTO)).map(devisMapper::toDto);
    }

    @Override
    public Mono<DevisDTO> partialUpdate(DevisDTO devisDTO) {
        log.debug("Request to partially update Devis : {}", devisDTO);

        return devisRepository
            .findById(devisDTO.getId())
            .map(existingDevis -> {
                devisMapper.partialUpdate(existingDevis, devisDTO);

                return existingDevis;
            })
            .flatMap(devisRepository::save)
            .map(devisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DevisDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Devis");
        return devisRepository.findAllBy(pageable).map(devisMapper::toDto);
    }

    public Mono<Long> countAll() {
        return devisRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DevisDTO> findOne(Long id) {
        log.debug("Request to get Devis : {}", id);
        return devisRepository.findById(id).map(devisMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Devis : {}", id);
        return devisRepository.deleteById(id);
    }
}
