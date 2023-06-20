package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Departement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface DepartementRepositoryWithBagRelationships {
    Optional<Departement> fetchBagRelationships(Optional<Departement> departement);

    List<Departement> fetchBagRelationships(List<Departement> departements);

    Page<Departement> fetchBagRelationships(Page<Departement> departements);
}
