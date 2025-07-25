package com.a.portnet_back.Services;

import com.a.portnet_back.Models.Agent;
import com.a.portnet_back.Models.Importateur;
import com.a.portnet_back.Models.User;
import com.a.portnet_back.Repositories.AgentRepository;
import com.a.portnet_back.Repositories.ImportateurRepository;
import com.a.portnet_back.Repositories.UserRepository;
import com.a.portnet_back.Enum.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final ImportateurRepository importateurRepository;

    public AuthService(UserRepository userRepository,
                       AgentRepository agentRepository,
                       ImportateurRepository importateurRepository) {
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
        this.importateurRepository = importateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .accountExpired(false)
                .accountLocked(!user.isEnabled())
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    public Importateur findImportateurByUserId(Long userId) {
        return importateurRepository.findByUserId(userId).orElse(null);
    }

    public Agent findAgentByUserId(Long userId) {
        return agentRepository.findByUserId(userId).orElse(null);
    }


    public User findSuperviseurByUserId(Long userId) {
        return userRepository.findById(userId)
                .filter(user -> user.getRole() == Role.SUPERVISEUR)
                .orElse(null);
    }


    public User createSuperviseur(String email, String password, String nomComplet) {
        User superviseur = new User(email, password, Role.SUPERVISEUR, nomComplet);
        return userRepository.save(superviseur);
    }


    public java.util.List<User> findAllSuperviseurs() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.SUPERVISEUR)
                .toList();
    }


    public java.util.List<Importateur> findAllImportateurs() {
        return importateurRepository.findAllWithUser();
    }

    public java.util.List<User> findAllImportateurUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.IMPORTATEUR)
                .toList();
    }


    public boolean isImportateur(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == Role.IMPORTATEUR)
                .orElse(false);
    }


    public boolean isSuperviseur(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == Role.SUPERVISEUR)
                .orElse(false);
    }


    public boolean isAgent(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == Role.AGENT)
                .orElse(false);
    }
}