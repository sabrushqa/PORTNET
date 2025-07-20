package com.a.portnet_back.Repositories;

import com.a.portnet_back.Models.Superviseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperviseurRepository extends JpaRepository<Superviseur, Long> {
    Optional<Superviseur> findByUserId(Long userId);
}