package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Departement;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.DepartementDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Departement} and its DTO {@link DepartementDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepartementMapper extends EntityMapper<DepartementDTO, Departement> {
    @Mapping(target = "userId", source = "userId", qualifiedByName = "userId")
    DepartementDTO toDto(Departement s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
