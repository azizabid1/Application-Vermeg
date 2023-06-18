package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Poste;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Poste entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PosteRepository extends JpaRepository<Poste, Long>, JpaSpecificationExecutor<Poste> {
    @Query("select poste from Poste poste where poste.userId.login = ?#{principal.username}")
    List<Poste> findByUserIdIsCurrentUser();
}
