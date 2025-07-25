package com.a.portnet_back.Controllers;

import com.a.portnet_back.DTO.*;
import com.a.portnet_back.Models.Agent;
import com.a.portnet_back.Models.Importateur;
import com.a.portnet_back.Models.User;
import com.a.portnet_back.Services.AuthService;
import com.a.portnet_back.Services.JwtService;
import com.a.portnet_back.Enum.Role;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, AuthService authService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            User user = authService.findByEmail(request.getEmail());
            if (user == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("Identifiants invalides"));
            }

            // Vérification spécifique pour les importateurs non activés
            if (user.getRole() == Role.IMPORTATEUR && !user.isEnabled()) {
                return ResponseEntity.status(401)
                        .body(new ErrorResponse("Compte importateur non activé. Veuillez vérifier votre email."));
            }

            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateAccessToken(userDetails);

            // Mise à jour du dernier login
            user.setLastLogin(java.time.LocalDateTime.now());

            return switch (user.getRole()) {
                case SUPERVISEUR -> {
                    SuperviseurResponse superviseurResponse = new SuperviseurResponse(
                            user.getId(), user.getNomComplet(), user.getEmail());
                    yield ResponseEntity.ok(new AuthResponse(token, "SUPERVISEUR", superviseurResponse, "Connexion réussie"));
                }

                case IMPORTATEUR -> {
                    Importateur importateur = authService.findImportateurByUserId(user.getId());
                    if (importateur == null) {
                        yield ResponseEntity.status(404).body(new ErrorResponse("Profil importateur non trouvé"));
                    }
                    ImportateurResponse importateurResponse = new ImportateurResponse(
                            importateur.getId(), importateur.getNomComplet(), user.getEmail());
                    yield ResponseEntity.ok(new AuthResponse(token, "IMPORTATEUR", importateurResponse, "Connexion réussie"));
                }

                case AGENT -> {
                    Agent agent = authService.findAgentByUserId(user.getId());
                    if (agent == null) {
                        yield ResponseEntity.status(404).body(new ErrorResponse("Profil agent non trouvé"));
                    }
                    AgentResponse agentResponse = new AgentResponse(
                            agent.getId(), agent.getNomComplet(), user.getEmail());
                    yield ResponseEntity.ok(new AuthResponse(token, "AGENT", agentResponse, "Connexion réussie"));
                }

                default -> ResponseEntity.status(403).body(new ErrorResponse("Rôle non reconnu: " + user.getRole()));
            };

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Email ou mot de passe incorrect"));
        } catch (org.springframework.security.authentication.DisabledException e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Compte désactivé"));
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            return ResponseEntity.status(401).body(new ErrorResponse("Erreur lors de la connexion"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(new ErrorResponse("Token manquant ou invalide"));
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("Token invalide"));
            }

            UserDetails userDetails = authService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                String newToken = jwtService.generateAccessToken(userDetails);
                User user = authService.findByEmail(username);

                if (user == null) {
                    return ResponseEntity.status(401).body(new ErrorResponse("Utilisateur non trouvé"));
                }

                return ResponseEntity.ok(new AuthResponse(
                        newToken,
                        user.getRole().name(),
                        null, // On ne retourne pas les détails complets lors du refresh
                        "Token rafraîchi avec succès"
                ));
            }

            return ResponseEntity.status(401).body(new ErrorResponse("Token invalide ou expiré"));
        } catch (Exception e) {
            System.err.println("Erreur lors du rafraîchissement: " + e.getMessage());
            return ResponseEntity.status(401).body(new ErrorResponse("Erreur lors du rafraîchissement du token"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(401).body(new ErrorResponse("Non authentifié"));
            }

            String email = auth.getName();
            User user = authService.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(404).body(new ErrorResponse("Utilisateur non trouvé"));
            }

            return switch (user.getRole()) {
                case SUPERVISEUR -> {
                    SuperviseurResponse response = new SuperviseurResponse(
                            user.getId(), user.getNomComplet(), user.getEmail());
                    yield ResponseEntity.ok(response);
                }

                case IMPORTATEUR -> {
                    Importateur importateur = authService.findImportateurByUserId(user.getId());
                    if (importateur == null) {
                        yield ResponseEntity.status(404).body(new ErrorResponse("Profil importateur non trouvé"));
                    }
                    ImportateurResponse response = new ImportateurResponse(
                            importateur.getId(), importateur.getNomComplet(), user.getEmail());

                    response.setSociete(importateur.getSociete());
                    response.setTelephone(importateur.getTelephone());
                    response.setVille(importateur.getVille());
                    response.setPays(importateur.getPays());
                    response.setDomaineActivite(importateur.getDomaineActivite());
                    response.setTypeOperation(importateur.getTypeOperation());
                    yield ResponseEntity.ok(response);
                }

                case AGENT -> {
                    Agent agent = authService.findAgentByUserId(user.getId());
                    if (agent == null) {
                        yield ResponseEntity.status(404).body(new ErrorResponse("Profil agent non trouvé"));
                    }
                    AgentResponse response = new AgentResponse(
                            agent.getId(), agent.getNomComplet(), user.getEmail());
                    yield ResponseEntity.ok(response);
                }

                default -> ResponseEntity.status(404).body(new ErrorResponse("Rôle non reconnu: " + user.getRole()));
            };

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du profil: " + e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Erreur interne du serveur"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Dans un système JWT, le logout côté serveur est optionnel
        // Le token sera simplement supprimé côté client
        return ResponseEntity.ok().body("Déconnexion réussie");
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(new ErrorResponse("Token manquant"));
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username != null) {
                UserDetails userDetails = authService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails)) {
                    User user = authService.findByEmail(username);
                    return ResponseEntity.ok().body("Token valide pour: " + user.getRole());
                }
            }

            return ResponseEntity.status(401).body(new ErrorResponse("Token invalide"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Erreur de validation"));
        }
    }
}