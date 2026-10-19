package com.newasteralnx.config;

import com.newasteralnx.constant.Role;
import com.newasteralnx.dto.request.CreateUserRequest;
import com.newasteralnx.entity.User;
import com.newasteralnx.repository.UserRepository;
import com.newasteralnx.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds bootstrap users for local demo.
 * Admin is always ensured. Demo OPS / WH / DRIVER accounts are created only if missing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    public void run(String... args) {
        ensureAdmin();
        ensureDemoUser("ops@asteralnx.com", "Ops Manager", Role.OPERATIONS_MANAGER, "Ops@12345");
        ensureDemoUser("warehouse@asteralnx.com", "Warehouse Manager", Role.WAREHOUSE_MANAGER, "Wh@12345");
        ensureDemoUser("driver@asteralnx.com", "Demo Driver", Role.DRIVER, "Driver@123");
    }

    private void ensureAdmin() {
        if (userRepository.existsByRoleAndDeletedFalse(Role.ADMIN)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        User admin = User.builder()
                .fullName("System Admin")
                .email("admin@asteralnx.com")
                .password(passwordEncoder.encode("Astera@123"))
                .role(Role.ADMIN)
                .phoneNumber(null)
                .active(true)
                .deleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
        userRepository.save(admin);
        log.info("Bootstrap admin created email=admin@asteralnx.com");
    }

    private void ensureDemoUser(String email, String fullName, Role role, String password) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        try {
            CreateUserRequest req = new CreateUserRequest();
            req.setFullName(fullName);
            req.setEmail(email);
            req.setPassword(password);
            req.setRole(role);
            req.setPhoneNumber("9000000000");
            userService.createUser(req);
            log.info("Demo user created email={} role={}", email, role);
        } catch (Exception ex) {
            log.warn("Could not seed demo user {}: {}", email, ex.getMessage());
        }
    }
}
