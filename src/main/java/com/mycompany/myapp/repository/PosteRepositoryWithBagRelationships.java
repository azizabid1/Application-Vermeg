package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Poste;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface PosteRepositoryWithBagRelationships {
    Optional<Poste> fetchBagRelationships(Optional<Poste> poste);

    List<Poste> fetchBagRelationships(List<Poste> postes);

    Page<Poste> fetchBagRelationships(Page<Poste> postes);
}
