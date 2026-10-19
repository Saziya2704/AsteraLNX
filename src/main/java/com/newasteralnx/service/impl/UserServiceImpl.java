package com.newasteralnx.service.impl;

import com.newasteralnx.constant.DriverStatus;
import com.newasteralnx.constant.Role;
import com.newasteralnx.dto.request.CreateUserRequest;
import com.newasteralnx.dto.request.UpdateUserRequest;
import com.newasteralnx.dto.response.UserResponse;
import com.newasteralnx.entity.Driver;
import com.newasteralnx.entity.User;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.DriverRepository;
import com.newasteralnx.repository.UserRepository;
import com.newasteralnx.service.UserService;
import com.newasteralnx.util.SecurityUtils;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Email already registered");
        }

        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phoneNumber(request.getPhoneNumber() == null ? null : request.getPhoneNumber().trim())
                .active(true)
                .deleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        user = userRepository.save(user);

        // Real company flow: DRIVER login user must have a Driver profile linked
        if (request.getRole() == Role.DRIVER) {
            createLinkedDriverProfile(user);
        }

        return toResponse(user);
    }

    private void createLinkedDriverProfile(User user) {
        if (driverRepository.findByUserIdAndDeletedFalse(user.getId()).isPresent()) {
            return;
        }
        String license = "TMP-" + user.getId() + "-" + System.currentTimeMillis() % 100000;
        Driver driver = Driver.builder()
                .driverCode(SequentialCodes.next("DRV", driverRepository.count() + 1, driverRepository::existsByDriverCode))
                .user(user)
                .name(user.getFullName())
                .phone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "0000000000")
                .email(user.getEmail())
                .licenseNumber(license)
                .licenseExpiry(LocalDate.now().plusYears(2))
                .status(DriverStatus.ACTIVE)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        driverRepository.save(driver);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = getActiveUser(id);
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim().isEmpty() ? null : request.getPhoneNumber().trim());
        }
        if (request.getRole() != null) {
            Role old = user.getRole();
            user.setRole(request.getRole());
            if (request.getRole() == Role.DRIVER && old != Role.DRIVER) {
                createLinkedDriverProfile(user);
            }
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Role role, Pageable pageable) {
        Page<User> users = role == null
                ? userRepository.findByDeletedFalse(pageable)
                : userRepository.findByDeletedFalseAndRole(role, pageable);
        return users.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return toResponse(getActiveUser(id));
    }

    @Override
    public void updateActive(Long id, boolean active) {
        User user = getActiveUser(id);
        if (!active && user.getRole() == Role.ADMIN) {
            ensureNotLastActiveAdmin(user);
        }
        user.setActive(active);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getActiveUser(id);
        Long currentUserId = SecurityUtils.requireCurrentUserId();
        if (user.getId().equals(currentUserId)) {
            throw new BusinessException("You cannot delete your own account");
        }
        if (user.getRole() == Role.ADMIN) {
            ensureNotLastActiveAdmin(user);
        }
        user.setDeleted(true);
        user.setActive(false);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(currentUserId);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private void ensureNotLastActiveAdmin(User user) {
        long activeAdmins = userRepository.countByRoleAndDeletedFalseAndActiveTrue(Role.ADMIN);
        if (activeAdmins <= 1 && Boolean.TRUE.equals(user.getActive())) {
            throw new BusinessException("Cannot deactivate or delete the last active ADMIN account");
        }
    }

    private User getActiveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (Boolean.TRUE.equals(user.getDeleted())) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
