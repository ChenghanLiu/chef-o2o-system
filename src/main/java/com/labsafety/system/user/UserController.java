package com.labsafety.system.user;

import com.labsafety.system.user.dto.CreateUserRequest;
import com.labsafety.system.user.dto.UpdateMyProfileRequest;
import com.labsafety.system.user.dto.UpdateUserRequest;
import com.labsafety.system.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ===============================
    // ADMIN: create user
    // ===============================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.createByAdmin(request);
    }

    // ===============================
    // ADMIN: list users (paged)
    // ===============================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> list(@PageableDefault(size = 10) Pageable pageable) {
        return userService.list(pageable);
    }

    // ===============================
    // ADMIN: get user by id
    // ===============================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    // ===============================
    // ADMIN: update user by id
    // ===============================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateByAdmin(id, request);
    }

    // ===============================
    // ADMIN: delete user
    // ===============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        userService.deleteByAdmin(id);
    }

    // ===============================
    // ALL: get my profile
    // ===============================
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','USER','CHEF')")
    public UserResponse me(Authentication authentication) {
        return userService.getMe(authentication.getName());
    }

    // ===============================
    // ALL: update my profile (email/password only)
    // ===============================
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','USER','CHEF')")
    public UserResponse updateMe(Authentication authentication,
                                 @Valid @RequestBody UpdateMyProfileRequest request) {
        return userService.updateMe(authentication.getName(), request);
    }
}
