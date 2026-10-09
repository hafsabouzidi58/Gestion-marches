package com.stage.gestion_marches.controller;
import com.stage.gestion_marches.dto.AuthResponse;
import com.stage.gestion_marches.dto.LoginRequest;
import com.stage.gestion_marches.dto.RegisterRequest;
import com.stage.gestion_marches.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // <-- Autorise Angular
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            String response = authService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Affiche la stacktrace complète dans le terminal IntelliJ
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}