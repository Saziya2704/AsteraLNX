package com.newasteralnx.repository;

import com.newasteralnx.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBookingNumber(String bookingNumber);
    boolean existsByOrderId(Long orderId);
}
