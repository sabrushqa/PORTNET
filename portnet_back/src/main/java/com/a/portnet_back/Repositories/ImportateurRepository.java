package com.a.portnet_back.Repositories;

import com.a.portnet_back.Models.Importateur;
import com.a.portnet_back.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportateurRepository extends JpaRepository<Importateur, Long> {

    Optional<Importateur> findByUserId(Long userId);

    Optional<Importateur> findByUser(User user);

    @Query("SELECT i FROM Importateur i WHERE i.user.id = :userId")
    Optional<Importateur> findByUserIdWithQuery(@Param("userId") Long userId);

    @Query("SELECT i FROM Importateur i LEFT JOIN FETCH i.user")
    List<Importateur> findAllWithUser();


    Optional<Importateur> findBySociete(String societe);

    Optional<Importateur> findByIce(String ice);

    Optional<Importateur> findByRc(String rc);

    List<Importateur> findByVille(String ville);

    List<Importateur> findByPays(String pays);

    List<Importateur> findByDomaineActivite(String domaineActivite);
}