package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.StatusEmploye;
import com.mycompany.myapp.repository.StatusEmployeRepository;
import com.mycompany.myapp.service.StatusEmployeService;
import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import com.mycompany.myapp.service.mapper.StatusEmployeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public StatusEmployeDTO save(StatusEmployeDTO statusEmployeDTO) {
        log.debug("Request to save StatusEmploye : {}", statusEmployeDTO);
        StatusEmploye statusEmploye = statusEmployeMapper.toEntity(statusEmployeDTO);
        statusEmploye = statusEmployeRepository.save(statusEmploye);
        return statusEmployeMapper.toDto(statusEmploye);
    }

    @Override
    public StatusEmployeDTO update(StatusEmployeDTO statusEmployeDTO) {
        log.debug("Request to save StatusEmploye : {}", statusEmployeDTO);
        StatusEmploye statusEmploye = statusEmployeMapper.toEntity(statusEmployeDTO);
        statusEmploye = statusEmployeRepository.save(statusEmploye);
        return statusEmployeMapper.toDto(statusEmploye);
    }

    @Override
    public Optional<StatusEmployeDTO> partialUpdate(StatusEmployeDTO statusEmployeDTO) {
        log.debug("Request to partially update StatusEmploye : {}", statusEmployeDTO);

        return statusEmployeRepository
            .findById(statusEmployeDTO.getId())
            .map(existingStatusEmploye -> {
                statusEmployeMapper.partialUpdate(existingStatusEmploye, statusEmployeDTO);

                return existingStatusEmploye;
            })
            .map(statusEmployeRepository::save)
            .map(statusEmployeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StatusEmployeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StatusEmployes");
        return statusEmployeRepository.findAll(pageable).map(statusEmployeMapper::toDto);
    }

    public Page<StatusEmployeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return statusEmployeRepository.findAllWithEagerRelationships(pageable).map(statusEmployeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StatusEmployeDTO> findOne(Long id) {
        log.debug("Request to get StatusEmploye : {}", id);
        return statusEmployeRepository.findOneWithEagerRelationships(id).map(statusEmployeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete StatusEmploye : {}", id);
        statusEmployeRepository.deleteById(id);
    }
}
