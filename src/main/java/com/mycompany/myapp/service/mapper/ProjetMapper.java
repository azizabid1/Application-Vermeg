package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Equipe;
import com.mycompany.myapp.domain.Projet;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.service.dto.EquipeDTO;
import com.mycompany.myapp.service.dto.ProjetDTO;
import com.mycompany.myapp.service.dto.TacheDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Projet} and its DTO {@link ProjetDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjetMapper extends EntityMapper<ProjetDTO, Projet> {
    //@Mapping(target = "equipe", source = "equipe", qualifiedByName = "equipeId")
    // @Mapping(target = "taches", source = "taches", qualifiedByName = "tacheIdSet")
    ProjetDTO toDto(Projet s);

    @Mapping(target = "removeTaches", ignore = true)
    Projet toEntity(ProjetDTO projetDTO);

    @Named("equipeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EquipeDTO toDtoEquipeId(Equipe equipe);

    @Named("tacheId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TacheDTO toDtoTacheId(Tache tache);

    @Named("tacheIdSet")
    default Set<TacheDTO> toDtoTacheIdSet(Set<Tache> tache) {
        return tache.stream().map(this::toDtoTacheId).collect(Collectors.toSet());
    }
}
