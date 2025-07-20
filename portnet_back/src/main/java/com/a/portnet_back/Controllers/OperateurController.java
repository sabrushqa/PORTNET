package com.a.portnet_back.Controllers;

import com.a.portnet_back.DTO.OperateurRequestDTO;
import com.a.portnet_back.Models.Operateur;
import com.a.portnet_back.Services.OperateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operateur")
public class OperateurController {

    private final OperateurService operateurService;

    public OperateurController(OperateurService operateurService) {
        this.operateurService = operateurService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerOperateur(@RequestBody OperateurRequestDTO dto) {
        try {
            Operateur operateur = operateurService.enregistrerOperateur(dto);
            return ResponseEntity.ok(operateur);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
