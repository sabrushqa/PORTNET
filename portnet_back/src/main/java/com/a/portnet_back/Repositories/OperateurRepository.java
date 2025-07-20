package com.a.portnet_back.Repositories;

import com.a.portnet_back.Models.Operateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperateurRepository extends JpaRepository<Operateur, Long> {
    Optional<Operateur> findByUserId(Long userId);
}
