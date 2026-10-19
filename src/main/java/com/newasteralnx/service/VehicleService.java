package com.newasteralnx.service;

import com.newasteralnx.constant.VehicleStatus;
import com.newasteralnx.dto.request.VehicleRequest;
import com.newasteralnx.dto.response.VehicleResponse;
import com.newasteralnx.entity.Vehicle;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByVehicleNumberIgnoreCase(request.getVehicleNumber().trim())) {
            throw new BusinessException("Vehicle number must be unique");
        }
        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(request.getVehicleNumber().trim().toUpperCase())
                .vehicleType(request.getVehicleType().trim())
                .capacity(request.getCapacity())
                .status(VehicleStatus.ACTIVE)
                .registrationNumber(request.getRegistrationNumber())
                .insuranceExpiry(request.getInsuranceExpiry())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public Page<VehicleResponse> list(Pageable pageable) {
        return vehicleRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public VehicleResponse get(Long id) {
        return toResponse(require(id));
    }

    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = require(id);
        String number = request.getVehicleNumber().trim().toUpperCase();
        if (vehicleRepository.existsByVehicleNumberIgnoreCaseAndIdNot(number, id)) {
            throw new BusinessException("Vehicle number must be unique");
        }
        vehicle.setVehicleNumber(number);
        vehicle.setVehicleType(request.getVehicleType().trim());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setInsuranceExpiry(request.getInsuranceExpiry());
        vehicle.setUpdatedAt(LocalDateTime.now());
        return toResponse(vehicleRepository.save(vehicle));
    }

    public VehicleResponse updateStatus(Long id, VehicleStatus status) {
        Vehicle vehicle = require(id);
        vehicle.setStatus(status);
        vehicle.setUpdatedAt(LocalDateTime.now());
        return toResponse(vehicleRepository.save(vehicle));
    }

    public Vehicle requireAssignable(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        if (vehicle.getStatus() != VehicleStatus.ACTIVE) {
            throw new BusinessException("Inactive vehicle cannot be assigned");
        }
        if (vehicle.getInsuranceExpiry() != null && vehicle.getInsuranceExpiry().isBefore(LocalDate.now())) {
            throw new BusinessException("Expired insurance should prevent assignment");
        }
        return vehicle;
    }

    public Vehicle require(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleType(vehicle.getVehicleType())
                .capacity(vehicle.getCapacity())
                .status(vehicle.getStatus())
                .registrationNumber(vehicle.getRegistrationNumber())
                .insuranceExpiry(vehicle.getInsuranceExpiry())
                .build();
    }
}
