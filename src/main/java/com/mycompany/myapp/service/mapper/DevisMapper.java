package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.service.dto.DevisDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Devis} and its DTO {@link DevisDTO}.
 */
@Mapper(componentModel = "spring")
public interface DevisMapper extends EntityMapper<DevisDTO, Devis> {}
