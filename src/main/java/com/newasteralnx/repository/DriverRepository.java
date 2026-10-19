package com.newasteralnx.repository;

import com.newasteralnx.constant.DriverStatus;
import com.newasteralnx.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByDriverCode(String driverCode);
    boolean existsByLicenseNumberIgnoreCase(String licenseNumber);

    boolean existsByLicenseNumberIgnoreCaseAndIdNot(String licenseNumber, Long id);
    Page<Driver> findByDeletedFalse(Pageable pageable);
    Page<Driver> findByDeletedFalseAndStatus(DriverStatus status, Pageable pageable);
    Optional<Driver> findByUserIdAndDeletedFalse(Long userId);
}
