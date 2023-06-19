package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Poste;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.PosteDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Poste} and its DTO {@link PosteDTO}.
 */
@Mapper(componentModel = "spring")
public interface PosteMapper extends EntityMapper<PosteDTO, Poste> {
    // @Mapping(target = "userId", source = "userId", qualifiedByName = "userId")
    PosteDTO toDto(Poste s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
