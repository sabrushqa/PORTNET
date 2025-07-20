package com.a.portnet_back.Services;

import com.a.portnet_back.DTO.OperateurRequestDTO;
import com.a.portnet_back.Enum.Role;
import com.a.portnet_back.Models.Operateur;
import com.a.portnet_back.Models.User;
import com.a.portnet_back.Models.VerificationToken;
import com.a.portnet_back.Repositories.OperateurRepository;
import com.a.portnet_back.Repositories.UserRepository;
import com.a.portnet_back.Repositories.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OperateurService {

    private final OperateurRepository operateurRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public OperateurService(OperateurRepository operateurRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            VerificationTokenRepository verificationTokenRepository,
                            EmailService emailService) {
        this.operateurRepository = operateurRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    public Operateur enregistrerOperateur(OperateurRequestDTO dto) {

        if (userRepository.existsByEmail(dto.email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }

        User user = new User();
        user.setEmail(dto.email);
        user.setPassword(passwordEncoder.encode(dto.password));
        user.setRole(Role.OPERATEUR);
        user.setEnabled(false);
        userRepository.save(user);

        Operateur operateur = new Operateur();
        operateur.setNomComplet(dto.nomComplet);
        operateur.setSociete(dto.societe);
        operateur.setTelephone(dto.telephone);
        operateur.setAdresse(dto.adresse);
        operateur.setVille(dto.ville);
        operateur.setPays(dto.pays);
        operateur.setRc(dto.rc);
        operateur.setIce(dto.ice);
        operateur.setIfiscale(dto.ifiscale);
        operateur.setPatente(dto.patente);
        operateur.setEmailProfessionnel(dto.emailProfessionnel);
        operateur.setDomaineActivite(dto.domaineActivite);
        operateur.setTypeOperation(dto.typeOperation);
        operateur.setCertifieISO(dto.certifieISO);
        operateur.setStatutDouanier(dto.statutDouanier);
        operateur.setUser(user);

        operateurRepository.save(operateur);


        String token = UUID.randomUUID().toString();
        VerificationToken vToken = new VerificationToken(token, user, LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(vToken);


        String activationLink = "http://localhost:3000/activation?token=" + token;
        emailService.sendEmail(user.getEmail(), "Activation de votre compte",
                "Bonjour,\n\nCliquez sur le lien suivant pour activer votre compte :\n" + activationLink +
                        "\n\nCe lien expire dans 24h.\n\nMerci.");

        return operateur;
    }


}
