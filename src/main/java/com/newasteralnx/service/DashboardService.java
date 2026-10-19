package com.newasteralnx.service;

import com.newasteralnx.constant.InvoiceStatus;
import com.newasteralnx.constant.OrderStatus;
import com.newasteralnx.constant.Role;
import com.newasteralnx.constant.ShipmentStatus;
import com.newasteralnx.dto.response.UserResponse;
import com.newasteralnx.entity.Commodity;
import com.newasteralnx.entity.Driver;
import com.newasteralnx.entity.LogisticsOrder;
import com.newasteralnx.entity.Shipment;
import com.newasteralnx.repository.CommodityRepository;
import com.newasteralnx.repository.CustomerRepository;
import com.newasteralnx.repository.InvoiceRepository;
import com.newasteralnx.repository.OrderRepository;
import com.newasteralnx.repository.ShipmentRepository;
import com.newasteralnx.repository.TripRepository;
import com.newasteralnx.repository.WarehouseRepository;
import com.newasteralnx.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final BigDecimal LOW_STOCK_THRESHOLD = new BigDecimal("10");

    private final UserService userService;
    private final DriverService driverService;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final WarehouseRepository warehouseRepository;
    private final InvoiceRepository invoiceRepository;
    private final TripRepository tripRepository;
    private final CommodityRepository commodityRepository;

    public Map<String, Object> current() {
        UserResponse user = userService.getUser(SecurityUtils.requireCurrentUserId());
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("user", user);
        dashboard.put("role", user.getRole());

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.OPERATIONS_MANAGER) {
            dashboard.put("customers", customerRepository.count());
            dashboard.put("orders", orderRepository.count());
            dashboard.put("ordersDraft", orderRepository.countByStatus(OrderStatus.DRAFT));
            dashboard.put("ordersConfirmed", orderRepository.countByStatus(OrderStatus.CONFIRMED));
            dashboard.put("ordersProcessing", orderRepository.countByStatus(OrderStatus.PROCESSING));
            dashboard.put("ordersReadyForShipment", orderRepository.countByStatus(OrderStatus.READY_FOR_SHIPMENT));
            dashboard.put("ordersShipped", orderRepository.countByStatus(OrderStatus.SHIPPED));
            dashboard.put("ordersDelivered", orderRepository.countByStatus(OrderStatus.DELIVERED));
            dashboard.put("ordersByStatus", orderStatusBreakdown());
            dashboard.put("shipments", shipmentRepository.count());
            dashboard.put("shipmentsByStatus", shipmentStatusBreakdown());
            dashboard.put("delayedShipments", shipmentRepository.countByStatus(ShipmentStatus.DELAYED));
            dashboard.put("shipmentsInTransit", shipmentRepository.countByStatus(ShipmentStatus.IN_TRANSIT));
            dashboard.put("invoices", invoiceRepository.count());
            dashboard.put("invoicesIssued", invoiceRepository.countByStatus(InvoiceStatus.ISSUED));
            dashboard.put("invoicesOverdue", invoiceRepository.countByStatus(InvoiceStatus.OVERDUE));
            dashboard.put("invoicesPaid", invoiceRepository.countByStatus(InvoiceStatus.PAID));
            dashboard.put("lowStockItems", commodityRepository.countByQuantityLessThan(LOW_STOCK_THRESHOLD));
            dashboard.put("recentOrders", summarizeOrders(orderRepository.findTop10ByOrderByCreatedAtDesc()));
            dashboard.put("readyForShipment", summarizeOrders(
                    orderRepository.findTop10ByStatusOrderByCreatedAtDesc(OrderStatus.READY_FOR_SHIPMENT)));
            dashboard.put("delayedShipmentList", summarizeShipments(
                    shipmentRepository.findTop10ByStatusOrderByShipmentDateDesc(ShipmentStatus.DELAYED)));
            dashboard.put("inTransitShipments", summarizeShipments(
                    shipmentRepository.findTop10ByStatusOrderByShipmentDateDesc(ShipmentStatus.IN_TRANSIT)));
            dashboard.put("lowStockList", summarizeCommodities(
                    commodityRepository.findTop10ByQuantityLessThanOrderByQuantityAsc(LOW_STOCK_THRESHOLD)));
            if (user.getRole() == Role.ADMIN) {
                dashboard.put("warehouses", warehouseRepository.count());
                dashboard.put("trips", tripRepository.count());
            }
        } else if (user.getRole() == Role.WAREHOUSE_MANAGER) {
            dashboard.put("warehouses", warehouseRepository.count());
            dashboard.put("orders", orderRepository.count());
            dashboard.put("ordersReadyForShipment", orderRepository.countByStatus(OrderStatus.READY_FOR_SHIPMENT));
            dashboard.put("shipments", shipmentRepository.count());
            dashboard.put("shipmentsCreated", shipmentRepository.countByStatus(ShipmentStatus.CREATED));
            dashboard.put("lowStockItems", commodityRepository.countByQuantityLessThan(LOW_STOCK_THRESHOLD));
            dashboard.put("readyForShipment", summarizeOrders(
                    orderRepository.findTop10ByStatusOrderByCreatedAtDesc(OrderStatus.READY_FOR_SHIPMENT)));
            dashboard.put("lowStockList", summarizeCommodities(
                    commodityRepository.findTop10ByQuantityLessThanOrderByQuantityAsc(LOW_STOCK_THRESHOLD)));
        } else if (user.getRole() == Role.DRIVER) {
            try {
                Driver driver = driverService.requireCurrentDriverProfile();
                dashboard.put("assignedShipments", shipmentRepository.countByDriverId(driver.getId()));
                dashboard.put("myTrips", tripRepository.countByDriverId(driver.getId()));
            } catch (Exception ex) {
                dashboard.put("assignedShipments", 0);
                dashboard.put("myTrips", 0);
                dashboard.put("note", "No driver profile linked to this user");
            }
        }
        return dashboard;
    }

    private Map<String, Long> orderStatusBreakdown() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            map.put(s.name(), orderRepository.countByStatus(s));
        }
        return map;
    }

    private Map<String, Long> shipmentStatusBreakdown() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (ShipmentStatus s : ShipmentStatus.values()) {
            map.put(s.name(), shipmentRepository.countByStatus(s));
        }
        return map;
    }

    private List<Map<String, Object>> summarizeOrders(List<LogisticsOrder> orders) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LogisticsOrder o : orders) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", o.getId());
            m.put("orderNumber", o.getOrderNumber());
            m.put("status", o.getStatus() == null ? null : o.getStatus().name());
            m.put("origin", o.getOrigin());
            m.put("destination", o.getDestination());
            m.put("orderDate", o.getOrderDate());
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> summarizeShipments(List<Shipment> shipments) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Shipment s : shipments) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", s.getId());
            m.put("shipmentNumber", s.getShipmentNumber());
            m.put("status", s.getStatus() == null ? null : s.getStatus().name());
            m.put("origin", s.getOrigin());
            m.put("destination", s.getDestination());
            m.put("expectedDelivery", s.getExpectedDelivery());
            list.add(m);
        }
        return list;
    }

    private List<Map<String, Object>> summarizeCommodities(List<Commodity> items) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Commodity c : items) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("commodityCode", c.getCommodityCode());
            m.put("name", c.getName());
            m.put("quantity", c.getQuantity());
            m.put("unit", c.getUnit());
            list.add(m);
        }
        return list;
    }
}
