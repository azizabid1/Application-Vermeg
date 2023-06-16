package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Vote;
import com.mycompany.myapp.service.dto.VoteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Vote} and its DTO {@link VoteDTO}.
 */
@Mapper(componentModel = "spring")
public interface VoteMapper extends EntityMapper<VoteDTO, Vote> {}
