package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Vote;
import com.mycompany.myapp.repository.VoteRepository;
import com.mycompany.myapp.service.VoteService;
import com.mycompany.myapp.service.dto.VoteDTO;
import com.mycompany.myapp.service.mapper.VoteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Vote}.
 */
@Service
@Transactional
public class VoteServiceImpl implements VoteService {

    private final Logger log = LoggerFactory.getLogger(VoteServiceImpl.class);

    private final VoteRepository voteRepository;

    private final VoteMapper voteMapper;

    public VoteServiceImpl(VoteRepository voteRepository, VoteMapper voteMapper) {
        this.voteRepository = voteRepository;
        this.voteMapper = voteMapper;
    }

    @Override
    public VoteDTO save(VoteDTO voteDTO) {
        log.debug("Request to save Vote : {}", voteDTO);
        Vote vote = voteMapper.toEntity(voteDTO);
        vote = voteRepository.save(vote);
        return voteMapper.toDto(vote);
    }

    @Override
    public VoteDTO update(VoteDTO voteDTO) {
        log.debug("Request to save Vote : {}", voteDTO);
        Vote vote = voteMapper.toEntity(voteDTO);
        vote = voteRepository.save(vote);
        return voteMapper.toDto(vote);
    }

    @Override
    public Optional<VoteDTO> partialUpdate(VoteDTO voteDTO) {
        log.debug("Request to partially update Vote : {}", voteDTO);

        return voteRepository
            .findById(voteDTO.getId())
            .map(existingVote -> {
                voteMapper.partialUpdate(existingVote, voteDTO);

                return existingVote;
            })
            .map(voteRepository::save)
            .map(voteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Votes");
        return voteRepository.findAll(pageable).map(voteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoteDTO> findOne(Long id) {
        log.debug("Request to get Vote : {}", id);
        return voteRepository.findById(id).map(voteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vote : {}", id);
        voteRepository.deleteById(id);
    }
}
