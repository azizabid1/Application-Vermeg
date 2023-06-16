package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.StatusEmploye;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.StatusEmployeDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StatusEmploye} and its DTO {@link StatusEmployeDTO}.
 */
@Mapper(componentModel = "spring")
public interface StatusEmployeMapper extends EntityMapper<StatusEmployeDTO, StatusEmploye> {
    @Mapping(target = "userId", source = "userId", qualifiedByName = "userId")
    StatusEmployeDTO toDto(StatusEmploye s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
