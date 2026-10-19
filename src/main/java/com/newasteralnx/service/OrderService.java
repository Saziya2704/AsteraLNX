package com.newasteralnx.service;

import com.newasteralnx.constant.OrderPriority;
import com.newasteralnx.constant.OrderStatus;
import com.newasteralnx.dto.request.OrderRequest;
import com.newasteralnx.dto.response.OrderResponse;
import com.newasteralnx.entity.Customer;
import com.newasteralnx.entity.LogisticsOrder;
import com.newasteralnx.entity.Quote;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.OrderRepository;
import com.newasteralnx.util.SecurityUtils;
import com.newasteralnx.util.SequentialCodes;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final QuoteService quoteService;

    public OrderResponse create(OrderRequest request) {
        Customer customer = customerService.requireActiveForTransaction(request.getCustomerId());
        Quote quote = null;
        if (request.getQuoteId() != null) {
            quote = quoteService.requireAccepted(request.getQuoteId());
            if (!quote.getCustomer().getId().equals(customer.getId())) {
                throw new BusinessException("Quote does not belong to the selected customer");
            }
        }

        LogisticsOrder order = LogisticsOrder.builder()
                .orderNumber(SequentialCodes.next("ORD", orderRepository.count() + 1, orderRepository::existsByOrderNumber))
                .customer(customer)
                .quote(quote)
                .origin(request.getOrigin().trim())
                .destination(request.getDestination().trim())
                .orderDate(LocalDate.now())
                .commodityName(request.getCommodityName().trim())
                .quantity(request.getQuantity())
                .weight(request.getWeight())
                .volume(request.getVolume())
                .status(OrderStatus.DRAFT)
                .priority(request.getPriority() == null ? OrderPriority.NORMAL : request.getPriority())
                .createdBy(SecurityUtils.requireCurrentUserId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        LogisticsOrder saved = orderRepository.save(order);
        if (quote != null) {
            quoteService.markConverted(quote);
        }
        return toResponse(saved);
    }

    public OrderResponse convertFromQuote(Long quoteId) {
        Quote quote = quoteService.requireAccepted(quoteId);
        OrderRequest request = new OrderRequest();
        request.setCustomerId(quote.getCustomer().getId());
        request.setQuoteId(quote.getId());
        request.setOrigin(quote.getOrigin());
        request.setDestination(quote.getDestination());
        String commodityName = "Quoted cargo";
        java.math.BigDecimal quantity = java.math.BigDecimal.ONE;
        if (quote.getInquiry() != null) {
            if (quote.getInquiry().getCargoDescription() != null && !quote.getInquiry().getCargoDescription().isBlank()) {
                commodityName = quote.getInquiry().getCargoDescription();
            }
            if (quote.getInquiry().getQuantity() != null) {
                quantity = quote.getInquiry().getQuantity();
            }
        }
        request.setCommodityName(commodityName);
        request.setQuantity(quantity);
        request.setPriority(OrderPriority.NORMAL);
        return create(request);
    }

    public OrderResponse confirm(Long id) {
        LogisticsOrder order = require(id);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BusinessException("Only draft orders can be confirmed");
        }
        return updateStatus(id, OrderStatus.CONFIRMED);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> list(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long id) {
        return toResponse(require(id));
    }

    public OrderResponse update(Long id, OrderRequest request) {
        LogisticsOrder order = require(id);
        assertEditable(order);
        customerService.requireActiveForTransaction(request.getCustomerId());
        order.setOrigin(request.getOrigin().trim());
        order.setDestination(request.getDestination().trim());
        order.setCommodityName(request.getCommodityName().trim());
        order.setQuantity(request.getQuantity());
        order.setWeight(request.getWeight());
        order.setVolume(request.getVolume());
        if (request.getPriority() != null) {
            order.setPriority(request.getPriority());
        }
        order.setUpdatedAt(LocalDateTime.now());
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse updateStatus(Long id, OrderStatus status) {
        LogisticsOrder order = require(id);
        OrderStatus current = order.getStatus();
        assertAllowedTransition(current, status);
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return toResponse(orderRepository.save(order));
    }

    /**
     * Cancel an order. Allowed from DRAFT, CONFIRMED, PROCESSING, READY_FOR_SHIPMENT.
     * Not allowed once SHIPPED, DELIVERED, or already CANCELLED.
     */
    public OrderResponse cancel(Long id) {
        LogisticsOrder order = require(id);
        OrderStatus current = order.getStatus();
        if (current == OrderStatus.CANCELLED) {
            throw new BusinessException("Order is already cancelled");
        }
        if (current == OrderStatus.SHIPPED || current == OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel an order that is already shipped or delivered");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return toResponse(orderRepository.save(order));
    }

    /**
     * Allowed transitions:
     * DRAFT → CONFIRMED | CANCELLED
     * CONFIRMED → PROCESSING | READY_FOR_SHIPMENT | CANCELLED
     * PROCESSING → READY_FOR_SHIPMENT | CANCELLED
     * READY_FOR_SHIPMENT → SHIPPED | CANCELLED
     * SHIPPED → DELIVERED
     * DELIVERED / CANCELLED → (terminal)
     */
    private void assertAllowedTransition(OrderStatus current, OrderStatus target) {
        if (current == target) {
            return;
        }
        if (current == OrderStatus.DELIVERED) {
            throw new BusinessException("Delivered order cannot change status");
        }
        if (current == OrderStatus.CANCELLED) {
            throw new BusinessException("Cancelled order cannot change status");
        }
        boolean allowed = switch (current) {
            case DRAFT -> target == OrderStatus.CONFIRMED || target == OrderStatus.CANCELLED;
            case CONFIRMED -> target == OrderStatus.PROCESSING
                    || target == OrderStatus.READY_FOR_SHIPMENT
                    || target == OrderStatus.CANCELLED;
            case PROCESSING -> target == OrderStatus.READY_FOR_SHIPMENT || target == OrderStatus.CANCELLED;
            case READY_FOR_SHIPMENT -> target == OrderStatus.SHIPPED || target == OrderStatus.CANCELLED;
            case SHIPPED -> target == OrderStatus.DELIVERED;
            default -> false;
        };
        if (!allowed) {
            throw new BusinessException(
                    "Invalid order status transition from " + current + " to " + target);
        }
    }

    public LogisticsOrder require(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public LogisticsOrder requireShippable(Long id) {
        LogisticsOrder order = require(id);
        OrderStatus status = order.getStatus();
        if (status == OrderStatus.CANCELLED) {
            throw new BusinessException("Cancelled order cannot create a shipment");
        }
        if (status == OrderStatus.DRAFT) {
            throw new BusinessException("Draft order cannot create a shipment; confirm it first");
        }
        if (status == OrderStatus.DELIVERED) {
            throw new BusinessException("Delivered order cannot create a new shipment");
        }
        customerService.requireActiveForTransaction(order.getCustomer().getId());
        return order;
    }

    public void markShipped(LogisticsOrder order) {
        order.setStatus(OrderStatus.SHIPPED);
        order.setUpdatedAt(LocalDateTime.now());
    }

    public void markDelivered(LogisticsOrder order) {
        order.setStatus(OrderStatus.DELIVERED);
        order.setUpdatedAt(LocalDateTime.now());
    }

    public void assertEditable(LogisticsOrder order) {
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Delivered order cannot be edited");
        }
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.READY_FOR_SHIPMENT) {
            throw new BusinessException("Order cannot be modified after shipment begins");
        }
    }

    private OrderResponse toResponse(LogisticsOrder order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .quoteId(order.getQuote() == null ? null : order.getQuote().getId())
                .origin(order.getOrigin())
                .destination(order.getDestination())
                .orderDate(order.getOrderDate())
                .commodityName(order.getCommodityName())
                .quantity(order.getQuantity())
                .weight(order.getWeight())
                .volume(order.getVolume())
                .status(order.getStatus())
                .priority(order.getPriority())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
