package me.diegxherrera.estrafebackend.controller.v1;

import me.diegxherrera.estrafebackend.model.UserEntity;
import me.diegxherrera.estrafebackend.repository.UserRepository;
import me.diegxherrera.estrafebackend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          UserDetailsService userDetailsService, UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        if (!userData.containsKey("email") || userData.get("email").isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        if (!userData.containsKey("username") || userData.get("username").isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
        }
        if (!userData.containsKey("password") || userData.get("password").isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
        }

        if (userRepository.findByUsername(userData.get("username")).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        UserEntity user = new UserEntity();
        user.setUsername(userData.get("username"));
        user.setPassword(passwordEncoder.encode(userData.get("password")));
        user.setEmail(userData.get("email"));
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.get("username"), credentials.get("password"))
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.get("username"));
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
        }

        token = token.substring(7); // Remove "Bearer " prefix

        String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.validateToken(token, userDetails)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
        }

        String newToken = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", newToken));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        Optional<UserEntity> userOptional = userRepository.findByUsername(principal.getName());

        if (userOptional.isEmpty() ) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        UserEntity user = userOptional.get();
        Map<String, Serializable> userProfile = new HashMap<>();
        userProfile.put("id", user.getId().toString());
        userProfile.put("username", user.getUsername());
        userProfile.put("email", user.getEmail());
        userProfile.put("role", user.getRole());

        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token format"));
        }

        token = token.substring(7);

        // Here you can implement token blacklisting if needed.
        // Example: store invalidated tokens in Redis or DB

        return ResponseEntity.ok(Map.of("message", "User logged out successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Principal principal) {
        Optional<UserEntity> userOptional = userRepository.findByUsername(principal.getName());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        UserEntity user = userOptional.get();
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("error", "Incorrect current password"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Serializable>>> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();

        List<Map<String, Serializable>> userList = users.stream().map(user -> {
            Map<String, Serializable> userMap = new HashMap<>();
            userMap.put("id", user.getId().toString());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole());
            return userMap;
        }).toList();

        return ResponseEntity.ok(userList);
    }
}