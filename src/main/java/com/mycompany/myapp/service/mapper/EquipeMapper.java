package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.Vote;
import com.mycompany.myapp.service.dto.EquipeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import com.mycompany.myapp.service.dto.VoteDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipe} and its DTO {@link EquipeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipeMapper extends EntityMapper<EquipeDTO, Equipe> {
    // @Mapping(target = "userId", source = "userId", qualifiedByName = "userId")
    //  @Mapping(target = "votes", source = "votes", qualifiedByName = "voteIdSet")
    EquipeDTO toDto(Equipe s);

    @Mapping(target = "removeVote", ignore = true)
    Equipe toEntity(EquipeDTO equipeDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("voteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    VoteDTO toDtoVoteId(Vote vote);

    @Named("voteIdSet")
    default Set<VoteDTO> toDtoVoteIdSet(Set<Vote> vote) {
        return vote.stream().map(this::toDtoVoteId).collect(Collectors.toSet());
    }
}
