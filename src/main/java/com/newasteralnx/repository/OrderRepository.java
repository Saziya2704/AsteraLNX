package com.newasteralnx.repository;

import com.newasteralnx.constant.OrderStatus;
import com.newasteralnx.entity.LogisticsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderRepository extends JpaRepository<LogisticsOrder, Long>, JpaSpecificationExecutor<LogisticsOrder> {
    boolean existsByOrderNumber(String orderNumber);

    long countByStatus(OrderStatus status);

    List<LogisticsOrder> findTop10ByOrderByCreatedAtDesc();

    List<LogisticsOrder> findTop10ByStatusOrderByCreatedAtDesc(OrderStatus status);
}
