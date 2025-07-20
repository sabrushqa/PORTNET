package com.a.portnet_back.Controllers;

import com.a.portnet_back.Models.User;
import com.a.portnet_back.Models.VerificationToken;
import com.a.portnet_back.Repositories.UserRepository;
import com.a.portnet_back.Repositories.VerificationTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.a.portnet_back.DTO.ActivationResponse;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class ActivationController {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public ActivationController(VerificationTokenRepository tokenRepository,
                                UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }



    @GetMapping("/activation")
    public ResponseEntity<?> activerCompte(@RequestParam("token") String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ActivationResponse("Token invalide"));
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ActivationResponse("Token expiré"));
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return ResponseEntity.ok(new ActivationResponse("Compte activé avec succès"));
    }


}
