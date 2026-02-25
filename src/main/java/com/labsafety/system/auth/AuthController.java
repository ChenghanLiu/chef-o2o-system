package com.labsafety.system.auth;

import com.labsafety.system.chef.ChefProfile;
import com.labsafety.system.security.JwtService;
import com.labsafety.system.user.Role;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import com.labsafety.system.user.dto.UserResponse;
import com.labsafety.system.user.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import java.util.Optional;
import com.labsafety.system.chef.ChefProfile;
import com.labsafety.system.chef.ChefProfileRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ChefProfileRepository chefProfileRepository;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ChefProfileRepository chefProfileRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.chefProfileRepository = chefProfileRepository;
    }

    // ===============================
    // REGISTER
    // ===============================
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        // username and phone cannot both be empty
        if ((request.username() == null || request.username().isBlank())
                && (request.phone() == null || request.phone().isBlank())) {
            return ResponseEntity.badRequest().body("Username or phone is required");
        }

        if (request.username() != null && userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (request.phone() != null && userRepository.existsByPhone(request.phone())) {
            return ResponseEntity.badRequest().body("Phone already exists");
        }

        Role role = parseRoleOrDefault(request.role());

        User user = new User();
        user.setUsername(request.username());
        user.setPhone(request.phone());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setStatus("ACTIVE");

        userRepository.save(user);
        // after userRepository.save(user);

        if (user.getRole() == Role.CHEF) {
            ChefProfile profile = new ChefProfile();
            profile.setAccount(user);
            profile.setStatus("PENDING");
            chefProfileRepository.save(profile);
        }

        return ResponseEntity.ok().build();
    }

    // ===============================
    // LOGIN
    // ===============================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        Optional<User> userOptional =
                userRepository.findByUsername(request.identifier())
                        .or(() -> userRepository.findByPhone(request.identifier()));

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("Account disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().build();
        }

        String role = user.getRole().name();

        // use username if exists, otherwise phone
        String subject = user.getUsername() != null
                ? user.getUsername()
                : user.getPhone();

        String token = jwtService.generateToken(subject, role);

        return ResponseEntity.ok(new AuthResponse(token, role));
    }

    private Role parseRoleOrDefault(String raw) {
        if (raw == null || raw.isBlank()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Role.USER;
        }
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String identifier = authentication.getName();

        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok(UserMapper.toResponse(user));
    }
}