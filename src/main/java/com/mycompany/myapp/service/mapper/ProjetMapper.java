package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Devis;
import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.service.dto.DevisDTO;
import com.mycompany.myapp.service.dto.EquipeDTO;
import com.mycompany.myapp.service.dto.ProjetDTO;
import com.mycompany.myapp.service.dto.TacheDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Projet} and its DTO {@link ProjetDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjetMapper extends EntityMapper<ProjetDTO, Projet> {
    @Mapping(target = "devis", source = "devis", qualifiedByName = "devisId")
    @Mapping(target = "equipe", source = "equipe", qualifiedByName = "equipeId")
    @Mapping(target = "tache", source = "tache", qualifiedByName = "tacheId")
    ProjetDTO toDto(Projet s);

    @Named("devisId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DevisDTO toDtoDevisId(Devis devis);

    @Named("equipeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EquipeDTO toDtoEquipeId(Equipe equipe);

    @Named("tacheId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TacheDTO toDtoTacheId(Tache tache);
}
