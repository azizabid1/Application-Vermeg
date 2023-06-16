package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.StatusEmploye;
import com.mycompany.myapp.repository.StatusEmployeRepository;
import com.mycompany.myapp.service.StatusEmployeService;
import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import com.mycompany.myapp.service.mapper.StatusEmployeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link StatusEmploye}.
 */
@Service
@Transactional
public class StatusEmployeServiceImpl implements StatusEmployeService {

    private final Logger log = LoggerFactory.getLogger(StatusEmployeServiceImpl.class);

    private final StatusEmployeRepository statusEmployeRepository;

    private final StatusEmployeMapper statusEmployeMapper;

    public StatusEmployeServiceImpl(StatusEmployeRepository statusEmployeRepository, StatusEmployeMapper statusEmployeMapper) {
        this.statusEmployeRepository = statusEmployeRepository;
        this.statusEmployeMapper = statusEmployeMapper;
    }

    @Override
    public Mono<StatusEmployeDTO> save(StatusEmployeDTO statusEmployeDTO) {
        log.debug("Request to save StatusEmploye : {}", statusEmployeDTO);
        return statusEmployeRepository.save(statusEmployeMapper.toEntity(statusEmployeDTO)).map(statusEmployeMapper::toDto);
    }

    @Override
    public Mono<StatusEmployeDTO> update(StatusEmployeDTO statusEmployeDTO) {
        log.debug("Request to save StatusEmploye : {}", statusEmployeDTO);
        return statusEmployeRepository.save(statusEmployeMapper.toEntity(statusEmployeDTO)).map(statusEmployeMapper::toDto);
    }

    @Override
    public Mono<StatusEmployeDTO> partialUpdate(StatusEmployeDTO statusEmployeDTO) {
        log.debug("Request to partially update StatusEmploye : {}", statusEmployeDTO);

        return statusEmployeRepository
            .findById(statusEmployeDTO.getId())
            .map(existingStatusEmploye -> {
                statusEmployeMapper.partialUpdate(existingStatusEmploye, statusEmployeDTO);

                return existingStatusEmploye;
            })
            .flatMap(statusEmployeRepository::save)
            .map(statusEmployeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StatusEmployeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StatusEmployes");
        return statusEmployeRepository.findAllBy(pageable).map(statusEmployeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return statusEmployeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StatusEmployeDTO> findOne(Long id) {
        log.debug("Request to get StatusEmploye : {}", id);
        return statusEmployeRepository.findById(id).map(statusEmployeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete StatusEmploye : {}", id);
        return statusEmployeRepository.deleteById(id);
    }
}
