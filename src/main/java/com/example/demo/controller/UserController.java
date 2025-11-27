package com.example.demo.controller;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserFilterDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO user = userService.getUserById(Long.parseLong(userId));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        checkAdminRole();
        UserResponseDTO userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")  // ← Лучше вынести регистрацию отдельно
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserCreateDTO createDto) {
        UserResponseDTO createdUser = userService.createUser(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable, UserFilterDTO filter) {
        checkAdminRole();
        Page<UserResponseDTO> page = userService.getAllUsers(pageable, filter);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(@Valid @RequestBody UserUpdateDTO updateDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO updatedUser = userService.updateUser(Long.parseLong(userId), updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDto) {
        checkAdminRole();
        UserResponseDTO updatedUser = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> setUserActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active
    ) {
        checkAdminRole();
        userService.setActiveStatus(id, active);
        return ResponseEntity.noContent().build(); // 204
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        checkAdminRole();
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private void checkAdminRole() {
        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Требуется роль ADMIN");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsersForAdmin();
        return ResponseEntity.ok(users);
    }
}