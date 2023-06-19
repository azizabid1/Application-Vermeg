package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.StatusEmploye;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StatusEmploye} and its DTO {@link StatusEmployeDTO}.
 */
@Mapper(componentModel = "spring")
public interface StatusEmployeMapper extends EntityMapper<StatusEmployeDTO, StatusEmploye> {
    // @Mapping(target = "users", source = "users", qualifiedByName = "userIdSet")
    StatusEmployeDTO toDto(StatusEmploye s);

    @Mapping(target = "removeUsers", ignore = true)
    StatusEmploye toEntity(StatusEmployeDTO statusEmployeDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("userIdSet")
    default Set<UserDTO> toDtoUserIdSet(Set<User> user) {
        return user.stream().map(this::toDtoUserId).collect(Collectors.toSet());
    }
}
