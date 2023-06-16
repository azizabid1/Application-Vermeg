package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.service.dto.TacheDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tache} and its DTO {@link TacheDTO}.
 */
@Mapper(componentModel = "spring")
public interface TacheMapper extends EntityMapper<TacheDTO, Tache> {}
