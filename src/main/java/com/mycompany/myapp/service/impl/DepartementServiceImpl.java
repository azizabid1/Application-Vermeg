package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Departement;
import com.mycompany.myapp.repository.DepartementRepository;
import com.mycompany.myapp.service.DepartementService;
import com.mycompany.myapp.service.dto.DepartementDTO;
import com.mycompany.myapp.service.mapper.DepartementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Departement}.
 */
@Service
@Transactional
public class DepartementServiceImpl implements DepartementService {

    private final Logger log = LoggerFactory.getLogger(DepartementServiceImpl.class);

    private final DepartementRepository departementRepository;

    private final DepartementMapper departementMapper;

    public DepartementServiceImpl(DepartementRepository departementRepository, DepartementMapper departementMapper) {
        this.departementRepository = departementRepository;
        this.departementMapper = departementMapper;
    }

    @Override
    public Mono<DepartementDTO> save(DepartementDTO departementDTO) {
        log.debug("Request to save Departement : {}", departementDTO);
        return departementRepository.save(departementMapper.toEntity(departementDTO)).map(departementMapper::toDto);
    }

    @Override
    public Mono<DepartementDTO> update(DepartementDTO departementDTO) {
        log.debug("Request to save Departement : {}", departementDTO);
        return departementRepository.save(departementMapper.toEntity(departementDTO)).map(departementMapper::toDto);
    }

    @Override
    public Mono<DepartementDTO> partialUpdate(DepartementDTO departementDTO) {
        log.debug("Request to partially update Departement : {}", departementDTO);

        return departementRepository
            .findById(departementDTO.getId())
            .map(existingDepartement -> {
                departementMapper.partialUpdate(existingDepartement, departementDTO);

                return existingDepartement;
            })
            .flatMap(departementRepository::save)
            .map(departementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DepartementDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Departements");
        return departementRepository.findAllBy(pageable).map(departementMapper::toDto);
    }

    public Mono<Long> countAll() {
        return departementRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DepartementDTO> findOne(Long id) {
        log.debug("Request to get Departement : {}", id);
        return departementRepository.findById(id).map(departementMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Departement : {}", id);
        return departementRepository.deleteById(id);
    }
}
