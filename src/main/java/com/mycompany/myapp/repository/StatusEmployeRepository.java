package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.StatusEmploye;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the StatusEmploye entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StatusEmployeRepository extends JpaRepository<StatusEmploye, Long>, JpaSpecificationExecutor<StatusEmploye> {
    @Query("select statusEmploye from StatusEmploye statusEmploye where statusEmploye.userId.login = ?#{principal.username}")
    List<StatusEmploye> findByUserIdIsCurrentUser();
}
