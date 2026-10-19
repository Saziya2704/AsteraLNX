package com.newasteralnx.controller;

import com.newasteralnx.constant.Role;
import com.newasteralnx.dto.request.CreateUserRequest;
import com.newasteralnx.dto.request.UpdateUserRequest;
import com.newasteralnx.dto.response.UserResponse;
import com.newasteralnx.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                size < 1 || size > 100 ? 20 : size,
                Sort.by("createdAt").descending()
        );
        return ResponseEntity.ok(userService.getUsers(role, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        userService.updateActive(id, active);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
