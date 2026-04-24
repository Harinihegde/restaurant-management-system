package com.restaurant.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderModule orderModule;

    public OrderController(OrderModule orderModule) {
        this.orderModule = orderModule;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("orders", orderModule.searchOrders(keyword));
        model.addAttribute("payments", orderModule.getPayments());
        model.addAttribute("keyword", keyword);
        model.addAttribute("order", new OrderEntry());
        return "orders";
    }

    @GetMapping("/save")
    public String save(@RequestParam String itemSummary,
                       @RequestParam(defaultValue = "1") Integer quantity,
                       @RequestParam(defaultValue = "0") BigDecimal totalAmount,
                       @RequestParam(defaultValue = "none") String discountType,
                       RedirectAttributes redirectAttributes) {
        OrderEntry order = new OrderEntry();
        order.setItemSummary(itemSummary);
        order.setQuantity(quantity);
        order.setTotalAmount(totalAmount);
        orderModule.createOrder(order, discountType);
        redirectAttributes.addFlashAttribute("successMessage", "Order created using " + discountType + " discount type.");
        return "redirect:/orders";
    }

    @GetMapping("/payment")
    public String processPayment(@RequestParam Long orderId,
                                 @RequestParam String paymentType,
                                 @RequestParam(required = false) String lastFour,
                                 @RequestParam(required = false) String upiId,
                                 RedirectAttributes redirectAttributes) {
        orderModule.processPayment(orderId, paymentType, Map.of(
                "lastFour", lastFour == null ? "" : lastFour,
                "upiId", upiId == null ? "" : upiId));
        redirectAttributes.addFlashAttribute("successMessage", "Payment processed successfully.");
        return "redirect:/orders";
    }
}

@Component
class OrderModule {

    private final List<OrderEntry> orders = new ArrayList<>();
    private final List<PaymentEntry> payments = new ArrayList<>();
    private final AtomicLong orderIds = new AtomicLong(1);
    private final AtomicLong paymentIds = new AtomicLong(1);

    OrderModule() {
        OrderEntry sample = new OrderEntry();
        sample.setId(orderIds.getAndIncrement());
        sample.setItemSummary("Paneer Tikka x1");
        sample.setQuantity(1);
        sample.setTotalAmount(new BigDecimal("220.00"));
        sample.setStatus("PLACED");
        sample.setCreatedAt(LocalDateTime.now());
        orders.add(sample);
    }

    List<OrderEntry> searchOrders(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(orders);
        }
        String normalized = keyword.toLowerCase();
        return orders.stream()
                .filter(order -> order.getStatus().toLowerCase().contains(normalized))
                .toList();
    }

    List<PaymentEntry> getPayments() {
        return new ArrayList<>(payments);
    }

    OrderEntry createOrder(OrderEntry order, String discountType) {
        order.setId(orderIds.getAndIncrement());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PLACED");
        order.setTotalAmount(applyDiscount(order.getTotalAmount(), discountType));
        orders.add(order);
        return order;
    }

    PaymentEntry processPayment(Long orderId, String paymentType, Map<String, String> params) {
        OrderEntry order = orders.stream()
                .filter(existing -> existing.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
        PaymentMethod paymentMethod = PaymentFactory.createPayment(paymentType, params);
        PaymentEntry payment = new PaymentEntry();
        payment.setId(paymentIds.getAndIncrement());
        payment.setOrderId(orderId);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentType(paymentType.toUpperCase());
        payment.setPaymentStatus(paymentMethod.processPayment(order.getTotalAmount(), params));
        payments.add(payment);
        order.setStatus("PAID");
        return payment;
    }

    private BigDecimal applyDiscount(BigDecimal amount, String discountType) {
        BigDecimal safeAmount = amount == null ? BigDecimal.ZERO : amount;
        if (discountType == null) {
            return safeAmount;
        }
        return switch (discountType.toLowerCase()) {
            case "percentage" -> safeAmount.multiply(BigDecimal.valueOf(0.90));
            case "flat" -> safeAmount.subtract(BigDecimal.valueOf(100)).max(BigDecimal.ZERO);
            default -> safeAmount;
        };
    }
}

final class PaymentFactory {

    private PaymentFactory() {
    }

    static PaymentMethod createPayment(String paymentType, Map<String, String> params) {
        return switch (paymentType.toUpperCase()) {
            case "CASH" -> new CashPayment();
            case "CARD" -> new CardPayment();
            default -> throw new IllegalArgumentException("Unsupported payment type: " + paymentType);
        };
    }
}

interface PaymentMethod {
    String processPayment(BigDecimal amount, Map<String, String> params);
}

class CashPayment implements PaymentMethod {

    @Override
    public String processPayment(BigDecimal amount, Map<String, String> params) {
        return "CASH RECEIVED";
    }
}

class CardPayment implements PaymentMethod {

    @Override
    public String processPayment(BigDecimal amount, Map<String, String> params) {
        String lastFour = params.getOrDefault("lastFour", "0000");
        return "CARD APPROVED (" + lastFour + ")";
    }
}

class OrderEntry {

    private Long id;
    private String itemSummary = "";
    private Integer quantity = 1;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private String status = "";
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemSummary() {
        return itemSummary;
    }

    public void setItemSummary(String itemSummary) {
        this.itemSummary = itemSummary;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

class PaymentEntry {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String paymentType;
    private String paymentStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
