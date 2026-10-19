package com.newasteralnx.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.newasteralnx.entity.*;
import com.newasteralnx.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final WarehouseRepository warehouseRepository;
    private final InvoiceRepository invoiceRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Map<String, Object> summary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("customers", customerRepository.count());
        summary.put("orders", orderRepository.count());
        summary.put("shipments", shipmentRepository.count());
        summary.put("warehouses", warehouseRepository.count());
        summary.put("invoices", invoiceRepository.count());
        summary.put("drivers", driverRepository.count());
        summary.put("trips", tripRepository.count());
        summary.put("delayedShipments", shipmentRepository.countByStatus(com.newasteralnx.constant.ShipmentStatus.DELAYED));
        summary.put("deliveredShipments", shipmentRepository.countByStatus(com.newasteralnx.constant.ShipmentStatus.DELIVERED));
        summary.put("invoicesOverdue", invoiceRepository.countByStatus(com.newasteralnx.constant.InvoiceStatus.OVERDUE));
        summary.put("invoicesPaid", invoiceRepository.countByStatus(com.newasteralnx.constant.InvoiceStatus.PAID));
        summary.put("ordersCancelled", orderRepository.countByStatus(com.newasteralnx.constant.OrderStatus.CANCELLED));
        return summary;
    }

    public byte[] exportCsv(String type) {
        String csv = switch (type.toLowerCase()) {
            case "shipments" -> shipmentsCsv();
            case "orders" -> ordersCsv();
            case "invoices", "revenue" -> invoicesCsv();
            case "warehouses" -> warehousesCsv();
            case "drivers", "trips" -> driversTripsCsv();
            case "customers" -> customersCsv();
            default -> throw new IllegalArgumentException("Unknown report type: " + type);
        };
        return csv.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportPdf(String type) {
        return switch (type.toLowerCase()) {
            case "shipments" -> buildPdf("Shipment Report",
                    new String[]{"Shipment #", "Origin", "Destination", "Status", "Expected"},
                    shipmentRepository.findAll().stream().map(s -> new String[]{
                            nullSafe(s.getShipmentNumber()), nullSafe(s.getOrigin()), nullSafe(s.getDestination()),
                            s.getStatus() != null ? s.getStatus().name() : "",
                            s.getExpectedDelivery() != null ? s.getExpectedDelivery().toString() : ""
                    }).collect(Collectors.toList()));
            case "orders" -> buildPdf("Order Report",
                    new String[]{"Order #", "Origin", "Destination", "Status", "Date"},
                    orderRepository.findAll().stream().map(o -> new String[]{
                            nullSafe(o.getOrderNumber()), nullSafe(o.getOrigin()), nullSafe(o.getDestination()),
                            o.getStatus() != null ? o.getStatus().name() : "",
                            o.getOrderDate() != null ? o.getOrderDate().toString() : ""
                    }).collect(Collectors.toList()));
            case "invoices", "revenue" -> buildPdf("Revenue / Invoice Report",
                    new String[]{"Invoice #", "Total", "Status", "Due Date"},
                    invoiceRepository.findAll().stream().map(i -> new String[]{
                            nullSafe(i.getInvoiceNumber()),
                            i.getTotal() != null ? i.getTotal().toPlainString() : "0",
                            i.getStatus() != null ? i.getStatus().name() : "",
                            i.getDueDate() != null ? i.getDueDate().toString() : ""
                    }).collect(Collectors.toList()));
            case "warehouses" -> buildPdf("Warehouse Report",
                    new String[]{"Code", "Name", "City", "Capacity", "Available", "Status"},
                    warehouseRepository.findAll().stream().map(w -> new String[]{
                            nullSafe(w.getWarehouseCode()), nullSafe(w.getName()), nullSafe(w.getCity()),
                            w.getCapacity() != null ? w.getCapacity().toString() : "",
                            w.getAvailableCapacity() != null ? w.getAvailableCapacity().toString() : "",
                            w.getStatus() != null ? w.getStatus().name() : ""
                    }).collect(Collectors.toList()));
            case "drivers", "trips" -> buildPdf("Driver / Trip Report",
                    new String[]{"Driver Code", "Name", "Phone", "Status", "License"},
                    driverRepository.findAll().stream().map(d -> new String[]{
                            nullSafe(d.getDriverCode()), nullSafe(d.getName()), nullSafe(d.getPhone()),
                            d.getStatus() != null ? d.getStatus().name() : "",
                            nullSafe(d.getLicenseNumber())
                    }).collect(Collectors.toList()));
            case "customers" -> buildPdf("Customer Report",
                    new String[]{"Code", "Company", "Contact", "Email", "City", "Status"},
                    customerRepository.findAll().stream().map(c -> new String[]{
                            nullSafe(c.getCustomerCode()), nullSafe(c.getCompanyName()), nullSafe(c.getContactPerson()),
                            nullSafe(c.getEmail()), nullSafe(c.getCity()),
                            c.getStatus() != null ? c.getStatus().name() : ""
                    }).collect(Collectors.toList()));
            default -> throw new IllegalArgumentException("Unknown report type: " + type);
        };
    }

    private String shipmentsCsv() {
        StringBuilder sb = new StringBuilder("Shipment Number,Origin,Destination,Status,Expected Delivery\n");
        shipmentRepository.findAll().forEach(s -> sb.append(csv(
                s.getShipmentNumber(), s.getOrigin(), s.getDestination(),
                s.getStatus() != null ? s.getStatus().name() : "",
                s.getExpectedDelivery() != null ? s.getExpectedDelivery().toString() : ""
        )));
        return sb.toString();
    }

    private String ordersCsv() {
        StringBuilder sb = new StringBuilder("Order Number,Origin,Destination,Status,Order Date\n");
        orderRepository.findAll().forEach(o -> sb.append(csv(
                o.getOrderNumber(), o.getOrigin(), o.getDestination(),
                o.getStatus() != null ? o.getStatus().name() : "",
                o.getOrderDate() != null ? o.getOrderDate().toString() : ""
        )));
        return sb.toString();
    }

    private String invoicesCsv() {
        StringBuilder sb = new StringBuilder("Invoice Number,Total,Status,Due Date,Paid Date\n");
        invoiceRepository.findAll().forEach(i -> sb.append(csv(
                i.getInvoiceNumber(),
                i.getTotal() != null ? i.getTotal().toPlainString() : "0",
                i.getStatus() != null ? i.getStatus().name() : "",
                i.getDueDate() != null ? i.getDueDate().toString() : "",
                i.getPaidDate() != null ? i.getPaidDate().toString() : ""
        )));
        return sb.toString();
    }

    private String warehousesCsv() {
        StringBuilder sb = new StringBuilder("Warehouse Code,Name,City,Capacity,Available,Status\n");
        warehouseRepository.findAll().forEach(w -> sb.append(csv(
                w.getWarehouseCode(), w.getName(), w.getCity(),
                w.getCapacity() != null ? w.getCapacity().toString() : "",
                w.getAvailableCapacity() != null ? w.getAvailableCapacity().toString() : "",
                w.getStatus() != null ? w.getStatus().name() : ""
        )));
        return sb.toString();
    }

    private String driversTripsCsv() {
        StringBuilder sb = new StringBuilder("Driver Code,Name,Phone,Status,License Number\n");
        driverRepository.findAll().forEach(d -> sb.append(csv(
                d.getDriverCode(), d.getName(), d.getPhone(),
                d.getStatus() != null ? d.getStatus().name() : "",
                d.getLicenseNumber()
        )));
        return sb.toString();
    }

    private String customersCsv() {
        StringBuilder sb = new StringBuilder("Customer Code,Company,Contact,Email,City,Status\n");
        customerRepository.findAll().forEach(c -> sb.append(csv(
                c.getCustomerCode(), c.getCompanyName(), c.getContactPerson(),
                c.getEmail(), c.getCity(),
                c.getStatus() != null ? c.getStatus().name() : ""
        )));
        return sb.toString();
    }

    private String csv(String... vals) {
        return java.util.Arrays.stream(vals)
                .map(v -> {
                    String s = v == null ? "" : v.replace("\"", "\"\"");
                    return "\"" + s + "\"";
                })
                .collect(Collectors.joining(",")) + "\n";
    }

    private String nullSafe(String s) { return s == null ? "" : s; }

    private byte[] buildPdf(String title, String[] headers, List<String[]> rows) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);

            document.add(new Paragraph(title, titleFont));
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(FMT),
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            Color headerBg = new Color(13, 110, 253);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(headerBg);
                cell.setPadding(5);
                table.addCell(cell);
            }
            for (String[] row : rows) {
                for (String col : row) {
                    PdfPCell cell = new PdfPCell(new Phrase(col != null ? col : "", cellFont));
                    cell.setPadding(4);
                    table.addCell(cell);
                }
            }
            if (rows.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No data available", cellFont));
                empty.setColspan(headers.length);
                empty.setPadding(8);
                table.addCell(empty);
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }
}
