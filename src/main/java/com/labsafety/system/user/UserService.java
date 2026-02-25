package com.labsafety.system.user;

import com.labsafety.system.user.dto.CreateUserRequest;
import com.labsafety.system.user.dto.UpdateMyProfileRequest;
import com.labsafety.system.user.dto.UpdateUserRequest;
import com.labsafety.system.user.dto.UserResponse;
import com.labsafety.system.user.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===============================
    // ADMIN: create user
    // ===============================
    public UserResponse createByAdmin(CreateUserRequest request) {

        validateIdentity(request.getUsername(), request.getPhone());

        if (request.getUsername() != null && userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already exists");
        }

        Role role = parseRoleOrDefault(request.getRole());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus("ACTIVE");

        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    // ===============================
    // ADMIN: list users
    // ===============================
    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toResponse);
    }

    // ===============================
    // ADMIN: get user by id
    // ===============================
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toResponse(user);
    }

    // ===============================
    // ADMIN: update user
    // ===============================
    public UserResponse updateByAdmin(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            Optional<User> owner = userRepository.findByUsername(request.getUsername());
            if (owner.isPresent() && !owner.get().getId().equals(user.getId())) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            Optional<User> owner = userRepository.findByPhone(request.getPhone());
            if (owner.isPresent() && !owner.get().getId().equals(user.getId())) {
                throw new RuntimeException("Phone already exists");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(parseRoleOrDefault(request.getRole()));
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    // ===============================
    // ADMIN: delete user
    // ===============================
    public void deleteByAdmin(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // ===============================
    // ALL: get my profile
    // ===============================
    @Transactional(readOnly = true)
    public UserResponse getMe(String identifier) {
        User me = findByIdentifier(identifier);
        return UserMapper.toResponse(me);
    }

    // ===============================
    // ALL: update my profile
    // ===============================
    public UserResponse updateMe(String identifier, UpdateMyProfileRequest request) {
        User me = findByIdentifier(identifier);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            Optional<User> owner = userRepository.findByUsername(request.getUsername());
            if (owner.isPresent() && !owner.get().getId().equals(me.getId())) {
                throw new RuntimeException("Username already exists");
            }
            me.setUsername(request.getUsername());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            Optional<User> owner = userRepository.findByPhone(request.getPhone());
            if (owner.isPresent() && !owner.get().getId().equals(me.getId())) {
                throw new RuntimeException("Phone already exists");
            }
            me.setPhone(request.getPhone());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            me.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        User saved = userRepository.save(me);
        return UserMapper.toResponse(saved);
    }

    // ===============================
    // helper methods
    // ===============================

    private void validateIdentity(String username, String phone) {
        if ((username == null || username.isBlank())
                && (phone == null || phone.isBlank())) {
            throw new RuntimeException("Username or phone is required");
        }
    }

    private User findByIdentifier(String identifier) {
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Role parseRoleOrDefault(String raw) {
        if (raw == null || raw.isBlank()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid role");
        }
    }
}