package com.a.portnet_back.Repositories;

import com.a.portnet_back.Models.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByEmail(String email);

    Optional<Agent> findByActivationToken(String activationToken);

    @Query("SELECT a FROM Agent a WHERE a.user.id = :userId")
    Optional<Agent> findByUserId(@Param("userId") Long userId);

    boolean existsByEmail(String email);

    @Query("SELECT a FROM Agent a LEFT JOIN FETCH a.user")
    List<Agent> findAllWithUser();

    List<Agent> findByIsActivatedFalse();

    List<Agent> findByIsActivatedTrue();
}