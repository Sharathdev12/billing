package com.nilgirikitchen.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import com.nilgirikitchen.billing.repository.OrderRepository;
import com.nilgirikitchen.billing.repository.MenuItemRepository;
import com.nilgirikitchen.billing.repository.MenuItemVariantRepository;
import com.nilgirikitchen.billing.dto.CreateOrderRequest;
import com.nilgirikitchen.billing.dto.CollectPaymentRequest;
import com.nilgirikitchen.billing.dto.OrderItemRequest;
import com.nilgirikitchen.billing.dto.OrderItemResponse;
import com.nilgirikitchen.billing.dto.OrderResponse;
import com.nilgirikitchen.billing.entity.MenuItemVariant;
import com.nilgirikitchen.billing.entity.Order;
import com.nilgirikitchen.billing.enums.OrderMode;
import com.nilgirikitchen.billing.enums.OrderStatus;
import com.nilgirikitchen.billing.enums.PaymentStatus;
import com.nilgirikitchen.billing.entity.OrderItem;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemVariantRepository variantRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }

        if (request.orderMode() == null) {
            throw new IllegalArgumentException("Order mode (dine-in/takeaway) is required");
        }

        Order order = new Order();
        order.setBillNo(generateBillNo());
        order.setOrderMode(request.orderMode());
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setPaymentType(null);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.items()) {
            MenuItemVariant variant = variantRepository.findById(itemReq.variantId())
                .orElseThrow(() -> new EntityNotFoundException("Variant not found: " + itemReq.variantId()));

            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItemId(variant.getMenuItem().getId());
            orderItem.setVariantId(variant.getId());
            orderItem.setItemName(variant.getMenuItem().getName());
            orderItem.setVariantName(variant.getVariantName());
            orderItem.setCategory(variant.getMenuItem().getCategory());
            orderItem.setUnitPrice(variant.getPrice());
            orderItem.setQuantity(itemReq.quantity());
            orderItem.setLineTotal(lineTotal);

            orderItems.add(orderItem);
            total = total.add(lineTotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse collectPayment(String billNo, CollectPaymentRequest request) {
        if (request.paymentType() == null) {
            throw new IllegalArgumentException("Payment type (cash/online) is required");
        }
        Order order = orderRepository.findByBillNo(billNo)
            .orElseThrow(() -> new EntityNotFoundException("No order found with bill number: " + billNo));
        order.setPaymentType(request.paymentType());
        order.setPaymentStatus(PaymentStatus.PAID);
        return toResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc()
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponse markDelivered(String billNo) {
        Order order = orderRepository.findByBillNo(billNo)
                .orElseThrow(() -> new EntityNotFoundException("No order found with bill number: " + billNo));
        order.setIsDelivered(true);
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(String billNo) {
        Order order = orderRepository.findByBillNo(billNo)
            .orElseThrow(() -> new EntityNotFoundException("No order found with bill number: " + billNo));
        if (Boolean.TRUE.equals(order.getIsDelivered())) {
            throw new IllegalArgumentException("Cannot cancel an order that has already been delivered");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse addItemsToOrder(String billNo, List<OrderItemRequest> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            throw new IllegalArgumentException("No items provided to add");
        }
        Order order = orderRepository.findByBillNo(billNo)
            .orElseThrow(() -> new EntityNotFoundException("No order found with bill number: " + billNo));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot add items to a cancelled order");
        }
        if (Boolean.TRUE.equals(order.getIsDelivered())) {
            throw new IllegalArgumentException("Cannot add items to an order that has already been delivered");
        }

        BigDecimal additionalTotal = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : newItems) {
            MenuItemVariant variant = variantRepository.findById(itemReq.variantId())
                .orElseThrow(() -> new EntityNotFoundException("Variant not found: " + itemReq.variantId()));

            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItemId(variant.getMenuItem().getId());
            orderItem.setVariantId(variant.getId());
            orderItem.setItemName(variant.getMenuItem().getName());
            orderItem.setVariantName(variant.getVariantName());
            orderItem.setCategory(variant.getMenuItem().getCategory());
            orderItem.setUnitPrice(variant.getPrice());
            orderItem.setQuantity(itemReq.quantity());
            orderItem.setLineTotal(lineTotal);

            order.getItems().add(orderItem);
            additionalTotal = additionalTotal.add(lineTotal);
        }

        order.setTotalAmount(order.getTotalAmount().add(additionalTotal));

        // The total just changed underneath an already-collected payment, so flip
        // back to UNPAID and make staff re-collect the correct amount.
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.UNPAID);
            order.setPaymentType(null);
        }

        return toResponse(orderRepository.save(order));
    }

    private String generateBillNo() {
        return "NK" + System.currentTimeMillis() % 100000;
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(i -> new OrderItemResponse(i.getItemName(), i.getVariantName(), i.getCategory(),
                 i.getUnitPrice(), i.getQuantity(), i.getLineTotal()))
            .toList();
        return new OrderResponse(order.getBillNo(), order.getTotalAmount(),
                 order.getOrderMode(), order.getPaymentStatus(), order.getPaymentType(),
                 order.getOrderStatus(), order.getOrderDate(), order.getIsDelivered(), items);
    }
}