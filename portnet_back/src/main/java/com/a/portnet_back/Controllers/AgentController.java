package com.a.portnet_back.Controllers;

import com.a.portnet_back.DTO.AgentActivationRequest;
import com.a.portnet_back.DTO.AgentDTO;
import com.a.portnet_back.DTO.AgentLogDTO;
import com.a.portnet_back.DTO.AgentRequest;
import com.a.portnet_back.Services.AgentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "*")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }


    @PostMapping
    public ResponseEntity<?> createAgent(@RequestBody AgentRequest request, Principal principal) {
        try {
            Long superviseurId = getSuperviseurIdFromPrincipal(principal); // À adapter à ton système de login
            agentService.createAgent(request, superviseurId);
            return ResponseEntity.ok().body("Agent créé avec succès. Email d’activation envoyé.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de la création : " + e.getMessage());
        }
    }


    @PostMapping("/activation")
    public ResponseEntity<?> activateAgent(@RequestBody AgentActivationRequest request) {
        try {
            boolean success = agentService.activateAgent(request);
            if (success) {
                return ResponseEntity.ok("Compte activé avec succès. Vous pouvez vous connecter.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalide ou expiré.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur lors de l’activation.");
        }
    }

    private Long getSuperviseurIdFromPrincipal(Principal principal) {

        return 1L;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SUPERVISEUR') or hasAuthority('AGENT')")
    public ResponseEntity<List<AgentDTO>> getAllAgents() {
        List<AgentDTO> agents = agentService.getAllAgents();
        return ResponseEntity.ok(agents);
    }


    @PostMapping("/{id}/toggle-activation")
    @PreAuthorize("hasAuthority('SUPERVISEUR')")
    public ResponseEntity<Boolean> toggleActivation(@PathVariable Long id) {
        boolean newState = agentService.toggleAgentActivation(id);
        return ResponseEntity.ok(newState);
    }



}
