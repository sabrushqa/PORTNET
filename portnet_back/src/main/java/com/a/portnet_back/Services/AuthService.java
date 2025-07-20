package com.a.portnet_back.Services;

import com.a.portnet_back.Models.Agent;
import com.a.portnet_back.Models.Operateur;
import com.a.portnet_back.Models.User;
import com.a.portnet_back.Models.Superviseur;
import com.a.portnet_back.Repositories.AgentRepository;
import com.a.portnet_back.Repositories.OperateurRepository;
import com.a.portnet_back.Repositories.UserRepository;
import com.a.portnet_back.Repositories.SuperviseurRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SuperviseurRepository superviseurRepository;
    private final CustomUserDetailsService userDetailsService;
    private final OperateurRepository operateurRepository;
    private final AgentRepository agentRepository;
    public AuthService(UserRepository userRepository,
                       SuperviseurRepository superviseurRepository,
                       OperateurRepository operateurRepository,
                       AgentRepository agentRepository,
                       CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.superviseurRepository = superviseurRepository;
        this.operateurRepository = operateurRepository;
        this.agentRepository= agentRepository;
        this.userDetailsService = userDetailsService;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }

    public Superviseur findSuperviseurByUserId(Long userId) {
        return superviseurRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Superviseur non trouvé"));
    }
    public Operateur findOperateurByUserId(Long userId) {
        return operateurRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Opérateur non trouvé"));
    }
    public Agent findAgentByUserId(Long userId) {
        return agentRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Agent non trouvé"));
    }


    public UserDetails loadUserByUsername(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUserEnabled(String email) {
        User user = findByEmail(email);
        return user.isEnabled();
    }
}