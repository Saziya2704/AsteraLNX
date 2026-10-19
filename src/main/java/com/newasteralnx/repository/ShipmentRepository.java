package com.newasteralnx.repository;

import com.newasteralnx.constant.ShipmentStatus;
import com.newasteralnx.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long>, JpaSpecificationExecutor<Shipment> {
    boolean existsByShipmentNumber(String shipmentNumber);
    boolean existsByOrderIdAndDriverIdAndStatusNotIn(Long orderId, Long driverId, Collection<ShipmentStatus> statuses);
    Page<Shipment> findByDriverId(Long driverId, Pageable pageable);
    Page<Shipment> findByDriverIdAndStatus(Long driverId, ShipmentStatus status, Pageable pageable);
    long countByDriverId(Long driverId);
    long countByStatus(ShipmentStatus status);

    boolean existsByCommodityIdAndStatusNotIn(Long commodityId, Collection<ShipmentStatus> statuses);

    boolean existsByWarehouseIdAndStatusNotIn(Long warehouseId, Collection<ShipmentStatus> statuses);

    long countByCommodityIdAndStatusNotIn(Long commodityId, Collection<ShipmentStatus> statuses);

    List<Shipment> findTop10ByOrderByShipmentDateDesc();

    List<Shipment> findTop10ByStatusOrderByShipmentDateDesc(ShipmentStatus status);
}
