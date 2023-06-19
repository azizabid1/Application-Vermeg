package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Projet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface ProjetRepositoryWithBagRelationships {
    Optional<Projet> fetchBagRelationships(Optional<Projet> projet);

    List<Projet> fetchBagRelationships(List<Projet> projets);

    Page<Projet> fetchBagRelationships(Page<Projet> projets);
}
