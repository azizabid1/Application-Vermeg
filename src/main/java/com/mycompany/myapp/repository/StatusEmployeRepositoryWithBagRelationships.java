package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.StatusEmploye;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface StatusEmployeRepositoryWithBagRelationships {
    Optional<StatusEmploye> fetchBagRelationships(Optional<StatusEmploye> statusEmploye);

    List<StatusEmploye> fetchBagRelationships(List<StatusEmploye> statusEmployes);

    Page<StatusEmploye> fetchBagRelationships(Page<StatusEmploye> statusEmployes);
}
