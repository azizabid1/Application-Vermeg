package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.repository.EquipeRepository;
import com.mycompany.myapp.service.EquipeService;
import com.mycompany.myapp.service.dto.EquipeDTO;
import com.mycompany.myapp.service.mapper.EquipeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public EquipeDTO save(EquipeDTO equipeDTO) {
        log.debug("Request to save Equipe : {}", equipeDTO);
        Equipe equipe = equipeMapper.toEntity(equipeDTO);
        equipe = equipeRepository.save(equipe);
        return equipeMapper.toDto(equipe);
    }

    @Override
    public EquipeDTO update(EquipeDTO equipeDTO) {
        log.debug("Request to save Equipe : {}", equipeDTO);
        Equipe equipe = equipeMapper.toEntity(equipeDTO);
        equipe = equipeRepository.save(equipe);
        return equipeMapper.toDto(equipe);
    }

    @Override
    public Optional<EquipeDTO> partialUpdate(EquipeDTO equipeDTO) {
        log.debug("Request to partially update Equipe : {}", equipeDTO);

        return equipeRepository
            .findById(equipeDTO.getId())
            .map(existingEquipe -> {
                equipeMapper.partialUpdate(existingEquipe, equipeDTO);

                return existingEquipe;
            })
            .map(equipeRepository::save)
            .map(equipeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Equipes");
        return equipeRepository.findAll(pageable).map(equipeMapper::toDto);
    }

    public Page<EquipeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return equipeRepository.findAllWithEagerRelationships(pageable).map(equipeMapper::toDto);
    }

    /**
     *  Get all the equipes where Projet is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EquipeDTO> findAllWhereProjetIsNull() {
        log.debug("Request to get all equipes where Projet is null");
        return StreamSupport
            .stream(equipeRepository.findAll().spliterator(), false)
            .filter(equipe -> equipe.getProjet() == null)
            .map(equipeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EquipeDTO> findOne(Long id) {
        log.debug("Request to get Equipe : {}", id);
        return equipeRepository.findOneWithEagerRelationships(id).map(equipeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Equipe : {}", id);
        equipeRepository.deleteById(id);
    }
}
