package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.repository.DevisRepository;
import com.mycompany.myapp.service.DevisService;
import com.mycompany.myapp.service.dto.DevisDTO;
import com.mycompany.myapp.service.mapper.DevisMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public DevisDTO save(DevisDTO devisDTO) {
        log.debug("Request to save Devis : {}", devisDTO);
        Devis devis = devisMapper.toEntity(devisDTO);
        devis = devisRepository.save(devis);
        return devisMapper.toDto(devis);
    }

    @Override
    public DevisDTO update(DevisDTO devisDTO) {
        log.debug("Request to save Devis : {}", devisDTO);
        Devis devis = devisMapper.toEntity(devisDTO);
        devis = devisRepository.save(devis);
        return devisMapper.toDto(devis);
    }

    @Override
    public Optional<DevisDTO> partialUpdate(DevisDTO devisDTO) {
        log.debug("Request to partially update Devis : {}", devisDTO);

        return devisRepository
            .findById(devisDTO.getId())
            .map(existingDevis -> {
                devisMapper.partialUpdate(existingDevis, devisDTO);

                return existingDevis;
            })
            .map(devisRepository::save)
            .map(devisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DevisDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Devis");
        return devisRepository.findAll(pageable).map(devisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DevisDTO> findOne(Long id) {
        log.debug("Request to get Devis : {}", id);
        return devisRepository.findById(id).map(devisMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Devis : {}", id);
        devisRepository.deleteById(id);
    }
}
