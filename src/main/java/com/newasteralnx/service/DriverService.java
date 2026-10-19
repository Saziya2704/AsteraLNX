package com.newasteralnx.service;

import com.newasteralnx.constant.DriverStatus;
import com.newasteralnx.constant.Role;
import com.newasteralnx.constant.TripStatus;
import com.newasteralnx.dto.request.DriverRequest;
import com.newasteralnx.dto.response.DriverResponse;
import com.newasteralnx.entity.Driver;
import com.newasteralnx.entity.User;
import com.newasteralnx.entity.Vehicle;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.DriverRepository;
import com.newasteralnx.repository.TripRepository;
import com.newasteralnx.repository.UserRepository;
import com.newasteralnx.util.SecurityUtils;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final VehicleService vehicleService;
    private final TripRepository tripRepository;

    public DriverResponse create(DriverRequest request) {
        if (driverRepository.existsByLicenseNumberIgnoreCase(request.getLicenseNumber().trim())) {
            throw new BusinessException("License number must be unique");
        }
        if (request.getLicenseExpiry().isBefore(LocalDate.now())) {
            throw new BusinessException("Expired license cannot be assigned to a new trip");
        }

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
            if (user.getRole() != Role.DRIVER) {
                throw new BusinessException("Linked user must have DRIVER role");
            }
        }

        Vehicle vehicle = request.getAssignedVehicleId() == null
                ? null
                : vehicleService.requireAssignable(request.getAssignedVehicleId());

        Driver driver = Driver.builder()
                .driverCode(SequentialCodes.next("DRV", driverRepository.count() + 1, driverRepository::existsByDriverCode))
                .user(user)
                .name(request.getName().trim())
                .phone(request.getPhone().trim())
                .email(request.getEmail() == null ? null : request.getEmail().trim().toLowerCase())
                .licenseNumber(request.getLicenseNumber().trim().toUpperCase())
                .licenseExpiry(request.getLicenseExpiry())
                .status(DriverStatus.ACTIVE)
                .assignedVehicle(vehicle)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(driverRepository.save(driver));
    }

    @Transactional(readOnly = true)
    public Page<DriverResponse> list(DriverStatus status, Pageable pageable) {
        Page<Driver> page = status == null
                ? driverRepository.findByDeletedFalse(pageable)
                : driverRepository.findByDeletedFalseAndStatus(status, pageable);
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public DriverResponse get(Long id) {
        return toResponse(requireExisting(id));
    }

    public DriverResponse update(Long id, DriverRequest request) {
        Driver driver = requireExisting(id);
        String license = request.getLicenseNumber().trim().toUpperCase();
        if (driverRepository.existsByLicenseNumberIgnoreCaseAndIdNot(license, id)) {
            throw new BusinessException("License number must be unique");
        }
        if (request.getLicenseExpiry().isBefore(LocalDate.now())) {
            throw new BusinessException("Expired license cannot be assigned to a new trip");
        }

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
            if (user.getRole() != Role.DRIVER) {
                throw new BusinessException("Linked user must have DRIVER role");
            }
        }

        Vehicle vehicle = request.getAssignedVehicleId() == null
                ? null
                : vehicleService.requireAssignable(request.getAssignedVehicleId());

        driver.setUser(user);
        driver.setName(request.getName().trim());
        driver.setPhone(request.getPhone().trim());
        driver.setEmail(request.getEmail() == null ? null : request.getEmail().trim().toLowerCase());
        driver.setLicenseNumber(license);
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setAssignedVehicle(vehicle);
        driver.setUpdatedAt(LocalDateTime.now());
        return toResponse(driverRepository.save(driver));
    }

    public DriverResponse updateStatus(Long id, DriverStatus status) {
        Driver driver = requireExisting(id);
        driver.setStatus(status);
        driver.setUpdatedAt(LocalDateTime.now());
        return toResponse(driverRepository.save(driver));
    }

    public Driver requireAssignable(Long id) {
        Driver driver = requireExisting(id);
        if (driver.getStatus() != DriverStatus.ACTIVE) {
            throw new BusinessException("Inactive driver cannot be assigned");
        }
        if (driver.getLicenseExpiry().isBefore(LocalDate.now())) {
            throw new BusinessException("Expired license cannot be assigned to a new trip");
        }
        if (tripRepository.existsByDriverIdAndStatusIn(driver.getId(), List.of(TripStatus.ASSIGNED, TripStatus.STARTED))) {
            throw new BusinessException("Driver cannot have conflicting active trips");
        }
        return driver;
    }

    public Driver requireExisting(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        if (Boolean.TRUE.equals(driver.getDeleted())) {
            throw new ResourceNotFoundException("Driver not found with id: " + id);
        }
        return driver;
    }

    public Driver requireCurrentDriverProfile() {
        return driverRepository.findByUserIdAndDeletedFalse(SecurityUtils.requireCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile is not linked to this user"));
    }

    public void delete(Long id) {
        Driver driver = requireExisting(id);
        driver.setDeleted(true);
        driver.setStatus(DriverStatus.INACTIVE);
        driver.setDeletedAt(LocalDateTime.now());
        driver.setDeletedBy(SecurityUtils.requireCurrentUserId());
        driver.setUpdatedAt(LocalDateTime.now());
        driverRepository.save(driver);
    }

    private DriverResponse toResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .driverCode(driver.getDriverCode())
                .userId(driver.getUser() == null ? null : driver.getUser().getId())
                .name(driver.getName())
                .phone(driver.getPhone())
                .email(driver.getEmail())
                .licenseNumber(driver.getLicenseNumber())
                .licenseExpiry(driver.getLicenseExpiry())
                .status(driver.getStatus())
                .assignedVehicleId(driver.getAssignedVehicle() == null ? null : driver.getAssignedVehicle().getId())
                .build();
    }
}
