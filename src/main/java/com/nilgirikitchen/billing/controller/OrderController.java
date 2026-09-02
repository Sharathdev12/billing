package com.nilgirikitchen.billing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nilgirikitchen.billing.service.OrderService;
import com.nilgirikitchen.billing.dto.CreateOrderRequest;
import com.nilgirikitchen.billing.dto.OrderResponse;
import com.nilgirikitchen.billing.dto.CollectPaymentRequest;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PatchMapping("/{billNo}/deliver")
    public OrderResponse markDelivered(@PathVariable String billNo) {
        return orderService.markDelivered(billNo);
    }

    @PatchMapping("/{billNo}/collect-payment")
    public OrderResponse collectPayment(@PathVariable String billNo, @RequestBody CollectPaymentRequest request) {
        return orderService.collectPayment(billNo, request);
    }
}