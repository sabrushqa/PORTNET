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
        agent.setSuperviseurId(superviseurId);

        Agent savedAgent = agentRepository.save(agent);


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
        } catch (Exception e) {
            System.out.println("Erreur lors de l'appel à N8N : " + e.getMessage());
        }

        return savedAgent;
    }

    public boolean activateAgent(AgentActivationRequest request) {
        Optional<Agent> optionalAgent = agentRepository.findByActivationToken(request.getToken());
        if (optionalAgent.isEmpty()) return false;

        Agent agent = optionalAgent.get();

        User user = new User();
        user.setEmail(agent.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.AGENT);
        user.setEnabled(true);

        userRepository.save(user);

        agent.setUser(user);
        agent.setActivated(true);
        agent.setActivationToken(null);
        agentRepository.save(agent);

        return true;
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
            AgentLogDTO dto = new AgentLogDTO();
            dto.setId(agent.getId());
            dto.setNomComplet(agent.getNomComplet());
            dto.setEmail(user.getEmail());
            dto.setEnabled(user.isEnabled());
            dto.setLastLogin(user.getLastLogin());
            agentLogs.add(dto);
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

        boolean newState = !user.isEnabled();
        user.setEnabled(newState);
        userRepository.save(user);

        return newState;
    }
}
