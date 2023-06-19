package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Departement;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.DepartementDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Departement} and its DTO {@link DepartementDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepartementMapper extends EntityMapper<DepartementDTO, Departement> {
    //@Mapping(target = "users", source = "users", qualifiedByName = "userIdSet")
    DepartementDTO toDto(Departement s);

    @Mapping(target = "removeUsers", ignore = true)
    Departement toEntity(DepartementDTO departementDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("userIdSet")
    default Set<UserDTO> toDtoUserIdSet(Set<User> user) {
        return user.stream().map(this::toDtoUserId).collect(Collectors.toSet());
    }
}
