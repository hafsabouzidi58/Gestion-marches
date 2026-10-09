package com.stage.gestion_marches.service;
import com.stage.gestion_marches.entity.Role; // Adaptez le package selon l'emplacement exact de votre classe Role
import com.stage.gestion_marches.dto.AuthResponse;
import com.stage.gestion_marches.dto.LoginRequest;
import com.stage.gestion_marches.dto.RegisterRequest;
import com.stage.gestion_marches.entity.Utilisateur;
import com.stage.gestion_marches.repository.UtilisateurRepository;
import com.stage.gestion_marches.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtUtils jwtUtils;

    // Injection propre de toutes les dépendances via le constructeur
    public AuthService(PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UtilisateurRepository utilisateurRepository,
                       JwtUtils jwtUtils) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.utilisateurRepository = utilisateurRepository;
        this.jwtUtils = jwtUtils;
    }

    public String register(RegisterRequest request) {
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.getMotDePasse()));

        // Utilisation de la chaîne de caractères "USER" entre guillemets
        String roleAttribue = (request.getRole() != null && !request.getRole().isBlank())
                ? request.getRole()
                : "USER";
        utilisateur.setRole(Role.valueOf(roleAttribue));
        utilisateur.setActif(true);

        utilisateurRepository.save(utilisateur);
        return "Utilisateur créé avec succès !";
    }
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.motDePasse())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return new AuthResponse(
                jwt,
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getRole()
        );
    }
}