package com.a.portnet_back.Controllers;

import com.a.portnet_back.DTO.*;
import com.a.portnet_back.Models.Agent;
import com.a.portnet_back.Models.Operateur;
import com.a.portnet_back.Models.Superviseur;
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

            if (user.getRole() == Role.OPERATEUR && !user.isEnabled()) {
                return ResponseEntity.status(401)
                        .body(new ErrorResponse("Compte opérateur non activé. Veuillez vérifier votre email."));
            }

            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateAccessToken(userDetails);

            return switch (user.getRole()) {
                case SUPERVISEUR -> {
                    Superviseur superviseur = authService.findSuperviseurByUserId(user.getId());
                    SuperviseurResponse superviseurResponse = new SuperviseurResponse(
                            superviseur.getId(), superviseur.getNomComplet(), user.getEmail());
                    yield ResponseEntity.ok(new AuthResponse(token, "SUPERVISEUR", superviseurResponse, "Connexion réussie"));
                }

                case OPERATEUR -> {
                    Operateur operateur = authService.findOperateurByUserId(user.getId());
                    OperateurResponse operateurResponse = new OperateurResponse(
                            operateur.getId(), operateur.getNomComplet(), user.getEmail());
                    yield ResponseEntity.ok(new AuthResponse(token, "OPERATEUR", operateurResponse, "Connexion réussie"));
                }

                case AGENT -> {
                    Agent agent = authService.findAgentByUserId(user.getId());
                    AgentResponse agentResponse = new AgentResponse(
                            agent.getId(), agent.getNomComplet(), user.getEmail());
                    yield ResponseEntity.ok(new AuthResponse(token, "AGENT", agentResponse, "Connexion réussie"));
                }

                default -> ResponseEntity.status(403).body(new ErrorResponse("Rôle non reconnu."));
            };

        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Identifiants invalides"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = authService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                String newToken = jwtService.generateAccessToken(userDetails);
                User user = authService.findByEmail(username);

                return ResponseEntity.ok(new AuthResponse(
                        newToken,
                        user.getRole().name(),
                        user,
                        "Token rafraîchi avec succès"
                ));
            }

            return ResponseEntity.status(401).body(new ErrorResponse("Token invalide"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ErrorResponse("Erreur lors du rafraîchissement"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        try {
            String email = auth.getName();
            User user = authService.findByEmail(email);

            return switch (user.getRole()) {
                case SUPERVISEUR -> {
                    Superviseur superviseur = authService.findSuperviseurByUserId(user.getId());
                    yield ResponseEntity.ok(new SuperviseurResponse(
                            superviseur.getId(), superviseur.getNomComplet(), user.getEmail()));
                }

                case OPERATEUR -> {
                    Operateur operateur = authService.findOperateurByUserId(user.getId());
                    yield ResponseEntity.ok(new OperateurResponse(
                            operateur.getId(), operateur.getNomComplet(), user.getEmail()));
                }

                case AGENT -> {
                    Agent agent = authService.findAgentByUserId(user.getId());
                    yield ResponseEntity.ok(new AgentResponse(
                            agent.getId(), agent.getNomComplet(), user.getEmail()));
                }

                default -> ResponseEntity.status(404).body(new ErrorResponse("Rôle non reconnu"));
            };

        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ErrorResponse("Utilisateur non trouvé"));
        }
    }
}
