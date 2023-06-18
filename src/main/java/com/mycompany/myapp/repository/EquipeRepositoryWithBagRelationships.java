package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Equipe;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface EquipeRepositoryWithBagRelationships {
    Optional<Equipe> fetchBagRelationships(Optional<Equipe> equipe);

    List<Equipe> fetchBagRelationships(List<Equipe> equipes);

    Page<Equipe> fetchBagRelationships(Page<Equipe> equipes);
}
