package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Departement;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Departement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long>, JpaSpecificationExecutor<Departement> {
    @Query("select departement from Departement departement where departement.userId.login = ?#{principal.username}")
    List<Departement> findByUserIdIsCurrentUser();
}
