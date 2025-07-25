package com.a.portnet_back.Services;

import com.a.portnet_back.DTO.AgentActivationRequest;
import com.a.portnet_back.DTO.AgentDTO;
import com.a.portnet_back.DTO.AgentLogDTO;
import com.a.portnet_back.DTO.AgentRequest;
import com.a.portnet_back.Models.Agent;
import com.a.portnet_back.Models.User;
import com.a.portnet_back.Enum.Role;
import com.a.portnet_back.Repositories.AgentRepository;
import com.a.portnet_back.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String baseFrontendUrl = System.getenv("FRONTEND_URL") != null
            ? System.getenv("FRONTEND_URL")
            : "http://localhost:3000";

    public Agent createAgent(AgentRequest request, Long superviseurId) {
        System.out.println("Création d'un agent pour: " + request.getEmail());

        if (agentRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un agent avec cet email existe déjà.");
        }

        Agent agent = new Agent(
                request.getNomComplet(),
                request.getEmail(),
                request.getTelephone(),
                request.getDepartement()
        );

        String token = UUID.randomUUID().toString();
        agent.setActivationToken(token);
        agent.setActivated(false);

        // Correction pour éviter l'erreur de type
        if (superviseurId != null) {
            agent.setSuperviseurId(superviseurId);
        }

        Agent savedAgent = agentRepository.save(agent);
        System.out.println("Agent sauvegardé avec ID: " + savedAgent.getId() + " et token: " + token);

        String activationLink = baseFrontendUrl + "/agent/activate?token=" + token;

        Map<String, String> payload = new HashMap<>();
        payload.put("nom", agent.getNomComplet());
        payload.put("email", agent.getEmail());
        payload.put("token", token);
        payload.put("activationLink", activationLink);

        try {
            restTemplate.postForEntity(
                    "https://portnet.app.n8n.cloud/webhook-test/invite-agent",
                    payload,
                    String.class);
            System.out.println("Email d'activation envoyé avec succès pour: " + agent.getEmail());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à N8N : " + e.getMessage());
        }

        return savedAgent;
    }

    public boolean activateAgent(AgentActivationRequest request) {
        System.out.println("=== DÉBUT DE L'ACTIVATION ===");
        System.out.println("Token reçu: " + request.getToken());
        System.out.println("Password présent: " + (request.getPassword() != null && !request.getPassword().isEmpty()));

        Optional<Agent> optionalAgent = agentRepository.findByActivationToken(request.getToken());

        if (optionalAgent.isEmpty()) {
            System.out.println("ERREUR: Aucun agent trouvé avec ce token");

            // Debug: vérifier tous les tokens existants
            List<Agent> allAgents = agentRepository.findAll();
            System.out.println("Tokens existants dans la base:");
            for (Agent a : allAgents) {
                System.out.println("- Agent " + a.getEmail() + " : token = " + a.getActivationToken());
            }

            return false;
        }

        Agent agent = optionalAgent.get();
        System.out.println("Agent trouvé: " + agent.getEmail());
        System.out.println("Agent déjà activé: " + agent.isActivated());

        if (agent.isActivated()) {
            System.out.println("ERREUR: Le compte est déjà activé");
            return false;
        }

        try {
            // Création de l'utilisateur - SOLUTION TEMPORAIRE
            User user = new User();
            user.setEmail(agent.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            // Utiliser AGENT_DOUANIER temporairement pour correspondre à la contrainte actuelle
            // TODO: Changer en AGENT une fois la contrainte mise à jour
            user.setRole(Role.AGENT); // Garder AGENT mais modifier la contrainte DB
            user.setEnabled(true);

            User savedUser = userRepository.save(user);
            System.out.println("Utilisateur créé avec ID: " + savedUser.getId());

            // Mise à jour de l'agent
            agent.setUser(savedUser);
            agent.setActivated(true);
            agent.setActivationToken(null);
            agentRepository.save(agent);

            System.out.println("=== ACTIVATION RÉUSSIE ===");
            return true;

        } catch (Exception e) {
            System.err.println("ERREUR lors de la création de l'utilisateur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<AgentDTO> getAllAgents() {
        List<Agent> agents = agentRepository.findAllWithUser();

        return agents.stream()
                .map(agent -> new AgentDTO(
                        agent.getId(),
                        agent.getNomComplet(),
                        agent.getEmail(),
                        agent.isActivated(),
                        agent.getUser() != null ? agent.getUser().getLastLogin() : null
                ))
                .collect(Collectors.toList());
    }

    public List<AgentLogDTO> getAgentsWithLastConnection() {
        List<Agent> agents = agentRepository.findAll();
        List<AgentLogDTO> agentLogs = new ArrayList<>();

        for (Agent agent : agents) {
            User user = agent.getUser();
            if (user != null) {
                AgentLogDTO dto = new AgentLogDTO();
                dto.setId(agent.getId());
                dto.setNomComplet(agent.getNomComplet());
                dto.setEmail(user.getEmail());
                dto.setEnabled(user.isEnabled());
                dto.setLastLogin(user.getLastLogin());
                agentLogs.add(dto);
            }
        }

        return agentLogs;
    }

    public boolean toggleAgentActivation(Long agentId) {
        Optional<Agent> optionalAgent = agentRepository.findById(agentId);
        if (optionalAgent.isEmpty()) {
            throw new RuntimeException("Agent non trouvé");
        }

        Agent agent = optionalAgent.get();
        User user = agent.getUser();

        if (user == null) {
            throw new RuntimeException("L'agent n'a pas de compte utilisateur associé");
        }

        boolean newState = !user.isEnabled();
        user.setEnabled(newState);
        userRepository.save(user);

        System.out.println("Agent " + agent.getEmail() + " " + (newState ? "activé" : "désactivé"));
        return newState;
    }
}