package com.a.portnet_back.Repositories;

import com.a.portnet_back.Models.User;
import com.a.portnet_back.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Nouvelles méthodes pour remplacer SuperviseurRepository
    List<User> findByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.role = 'SUPERVISEUR'")
    List<User> findAllSuperviseurs();

    @Query("SELECT u FROM User u WHERE u.role = 'SUPERVISEUR' AND u.enabled = true")
    List<User> findActiveSuperviseurs();

    Optional<User> findByIdAndRole(Long id, Role role);
}