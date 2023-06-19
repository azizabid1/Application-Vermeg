package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.service.dto.DevisDTO;
import com.mycompany.myapp.service.dto.ProjetDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Devis} and its DTO {@link DevisDTO}.
 */
@Mapper(componentModel = "spring")
public interface DevisMapper extends EntityMapper<DevisDTO, Devis> {
    //   @Mapping(target = "projet", source = "projet", qualifiedByName = "projetId")
    DevisDTO toDto(Devis s);

    @Named("projetId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjetDTO toDtoProjetId(Projet projet);
}
