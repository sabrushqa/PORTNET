package com.a.portnet_back.Controllers;

import com.a.portnet_back.DTO.AgentActivationRequest;
import com.a.portnet_back.DTO.AgentDTO;
import com.a.portnet_back.DTO.AgentRequest;
import com.a.portnet_back.Models.Agent;
import com.a.portnet_back.Services.AgentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "*")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPERVISEUR')")
    public ResponseEntity<?> createAgent(@RequestBody AgentRequest request, Principal principal) {
        try {
            System.out.println("=== DÉBUT CRÉATION AGENT ===");
            System.out.println("Request reçue: " + request);
            System.out.println("Principal: " + (principal != null ? principal.getName() : "null"));

            // Validation des données d'entrée
            if (request == null) {
                System.out.println("ERREUR: Request null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Données de l'agent manquantes");
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                System.out.println("ERREUR: Email manquant");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("L'email est obligatoire");
            }

            if (request.getNomComplet() == null || request.getNomComplet().trim().isEmpty()) {
                System.out.println("ERREUR: Nom complet manquant");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Le nom complet est obligatoire");
            }

            System.out.println("Données validées - Email: " + request.getEmail());
            System.out.println("Nom: " + request.getNomComplet());

            Long superviseurId = getSuperviseurIdFromPrincipal(principal);
            if (superviseurId == null) {
                System.out.println("ERREUR: SuperviseurId null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Impossible d'identifier le superviseur");
            }

            System.out.println("SuperviseurId trouvé: " + superviseurId);

            Agent createdAgent = agentService.createAgent(request, superviseurId);
            System.out.println("Agent créé avec succès - ID: " + createdAgent.getId());

            return ResponseEntity.ok().body("Agent créé avec succès. Email d'activation envoyé.");

        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de validation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la création: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur lors de la création : " + e.getMessage());
        }
    }


    @PostMapping("/activation")
    public ResponseEntity<?> activateAgent(@RequestBody AgentActivationRequest request) {
        try {
            System.out.println("Tentative d'activation avec le token: " + request.getToken());

            boolean success = agentService.activateAgent(request);
            if (success) {
                return ResponseEntity.ok("Compte activé avec succès. Vous pouvez vous connecter.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Token invalide ou expiré.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'activation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur serveur lors de l'activation.");
        }
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPERVISEUR') or hasAuthority('ROLE_AGENT')")
    public ResponseEntity<List<AgentDTO>> getAllAgents() {
        List<AgentDTO> agents = agentService.getAllAgents();
        return ResponseEntity.ok(agents);
    }


    @PostMapping("/{id}/toggle-activation")
    @PreAuthorize("hasAuthority('ROLE_SUPERVISEUR')")
    public ResponseEntity<Boolean> toggleActivation(@PathVariable Long id) {
        try {
            boolean newState = agentService.toggleAgentActivation(id);
            return ResponseEntity.ok(newState);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }


    private Long getSuperviseurIdFromPrincipal(Principal principal) {
        if (principal == null) {
            System.err.println("Principal est null - utilisateur non authentifié");
            return null;
        }

        try {
            String email = principal.getName();
            System.out.println("Email du principal: " + email);



            return Long.valueOf(1);

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du superviseur: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}