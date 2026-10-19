package com.newasteralnx.repository;

import com.newasteralnx.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByVehicleNumberIgnoreCase(String vehicleNumber);

    boolean existsByVehicleNumberIgnoreCaseAndIdNot(String vehicleNumber, Long id);
}
