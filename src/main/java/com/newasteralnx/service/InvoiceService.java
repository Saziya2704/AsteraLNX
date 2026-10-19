package com.newasteralnx.service;

import com.newasteralnx.constant.InvoiceStatus;
import com.newasteralnx.constant.ShipmentStatus;
import com.newasteralnx.dto.request.InvoiceRequest;
import com.newasteralnx.dto.response.InvoiceResponse;
import com.newasteralnx.entity.Customer;
import com.newasteralnx.entity.Invoice;
import com.newasteralnx.entity.LogisticsOrder;
import com.newasteralnx.entity.Shipment;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.InvoiceRepository;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final ShipmentService shipmentService;
    private final MailService mailService;

    public InvoiceResponse create(InvoiceRequest request) {
        Customer customer = customerService.requireActiveForTransaction(request.getCustomerId());
        LogisticsOrder order = orderService.require(request.getOrderId());
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Order does not belong to the selected customer");
        }
        if (order.getStatus() == com.newasteralnx.constant.OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot invoice a cancelled order");
        }
        Shipment shipment = request.getShipmentId() == null ? null : shipmentService.require(request.getShipmentId());
        if (shipment != null) {
            if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
                throw new BusinessException("Cancelled shipment cannot be invoiced");
            }
            if (!shipment.getOrder().getId().equals(order.getId())) {
                throw new BusinessException("Shipment does not belong to the selected order");
            }
        }

        BigDecimal subtotal = nullToZero(request.getSubtotal());
        BigDecimal tax = nullToZero(request.getTax());
        BigDecimal discount = nullToZero(request.getDiscount());
        BigDecimal total = subtotal.add(tax).subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Invoice amount cannot be negative");
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber(SequentialCodes.next("INV", invoiceRepository.count() + 1, invoiceRepository::existsByInvoiceNumber))
                .customer(customer)
                .order(order)
                .shipment(shipment)
                .invoiceDate(LocalDate.now())
                .subtotal(subtotal)
                .tax(tax)
                .discount(discount)
                .total(total)
                .status(InvoiceStatus.ISSUED)
                .dueDate(request.getDueDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice generated invoiceNumber={}", saved.getInvoiceNumber());
        return toResponse(refreshOverdue(saved));
    }

    public InvoiceResponse send(Long id) {
        Invoice invoice = require(id);
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) throw new BusinessException("Cancelled invoice cannot be sent");
        Customer customer = invoice.getCustomer();
        String html = "<div style=\"font-family:Arial,sans-serif;max-width:680px;margin:auto;color:#172033\">"
                + "<div style=\"padding:20px;background:#12366b;color:#fff\"><h2 style=\"margin:0\">AsteraLNX Logistics</h2><div>INVOICE</div></div>"
                + "<h3>Invoice " + invoice.getInvoiceNumber() + "</h3><p>Dear " + customer.getContactPerson() + ",</p>"
                + "<table style=\"border-collapse:collapse;width:100%\"><tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Invoice date</th><td style=\"padding:8px;border:1px solid #ddd\">" + invoice.getInvoiceDate() + "</td></tr>"
                + "<tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Order</th><td style=\"padding:8px;border:1px solid #ddd\">" + invoice.getOrder().getOrderNumber() + "</td></tr>"
                + "<tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Subtotal</th><td style=\"padding:8px;border:1px solid #ddd\">₹" + invoice.getSubtotal() + "</td></tr>"
                + "<tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Tax</th><td style=\"padding:8px;border:1px solid #ddd\">₹" + invoice.getTax() + "</td></tr>"
                + "<tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Discount</th><td style=\"padding:8px;border:1px solid #ddd\">₹" + invoice.getDiscount() + "</td></tr>"
                + "<tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Grand total</th><td style=\"padding:8px;border:1px solid #ddd;font-weight:bold\">₹" + invoice.getTotal() + "</td></tr></table>"
                + "<p>Regards,<br><strong>AsteraLNX Logistics</strong></p></div>";
        mailService.sendBusinessEmail(customer.getEmail(), "Invoice " + invoice.getInvoiceNumber() + " — AsteraLNX", html);
        // New invoices are created as ISSUED; DRAFT is not a supported InvoiceStatus.
        invoice.setUpdatedAt(LocalDateTime.now());
        return toResponse(invoiceRepository.save(invoice));
    }

    public InvoiceResponse markPaid(Long id) {
        Invoice invoice = refreshOverdue(require(id));
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Invoice is already paid");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException("Cancelled invoice cannot be marked paid");
        }
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDate.now());
        invoice.setUpdatedAt(LocalDateTime.now());
        return toResponse(invoiceRepository.save(invoice));
    }

    public InvoiceResponse cancel(Long id) {
        Invoice invoice = require(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Paid invoice cannot be cancelled");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException("Invoice is already cancelled");
        }
        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setUpdatedAt(LocalDateTime.now());
        return toResponse(invoiceRepository.save(invoice));
    }

    public InvoiceResponse update(Long id, InvoiceRequest request) {
        Invoice invoice = require(id);
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Paid invoice cannot be edited");
        }
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException("Cancelled invoice cannot be edited");
        }
        BigDecimal subtotal = nullToZero(request.getSubtotal());
        BigDecimal tax = nullToZero(request.getTax());
        BigDecimal discount = nullToZero(request.getDiscount());
        BigDecimal total = subtotal.add(tax).subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Invoice amount cannot be negative");
        }
        invoice.setSubtotal(subtotal);
        invoice.setTax(tax);
        invoice.setDiscount(discount);
        invoice.setTotal(total);
        invoice.setDueDate(request.getDueDate());
        invoice.setUpdatedAt(LocalDateTime.now());
        return toResponse(invoiceRepository.save(invoice));
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> list(Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(this::refreshOverdue).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse get(Long id) {
        return toResponse(refreshOverdue(require(id)));
    }

    private Invoice require(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private Invoice refreshOverdue(Invoice invoice) {
        if (invoice.getStatus() == InvoiceStatus.ISSUED
                && invoice.getDueDate() != null
                && invoice.getDueDate().isBefore(LocalDate.now())) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoice.setUpdatedAt(LocalDateTime.now());
            return invoiceRepository.save(invoice);
        }
        return invoice;
    }

    private static BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerId(invoice.getCustomer().getId())
                .orderId(invoice.getOrder().getId())
                .shipmentId(invoice.getShipment() == null ? null : invoice.getShipment().getId())
                .invoiceDate(invoice.getInvoiceDate())
                .subtotal(invoice.getSubtotal())
                .tax(invoice.getTax())
                .discount(invoice.getDiscount())
                .total(invoice.getTotal())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .paidDate(invoice.getPaidDate())
                .build();
    }
}
