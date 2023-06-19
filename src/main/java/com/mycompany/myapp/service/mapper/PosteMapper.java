package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Poste;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.PosteDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Poste} and its DTO {@link PosteDTO}.
 */
@Mapper(componentModel = "spring")
public interface PosteMapper extends EntityMapper<PosteDTO, Poste> {
    // @Mapping(target = "users", source = "users", qualifiedByName = "userIdSet")
    PosteDTO toDto(Poste s);

    @Mapping(target = "removeUsers", ignore = true)
    Poste toEntity(PosteDTO posteDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("userIdSet")
    default Set<UserDTO> toDtoUserIdSet(Set<User> user) {
        return user.stream().map(this::toDtoUserId).collect(Collectors.toSet());
    }
}
