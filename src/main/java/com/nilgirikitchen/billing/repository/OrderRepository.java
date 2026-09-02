package com.nilgirikitchen.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

import com.nilgirikitchen.billing.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByBillNo(String billNo);
    List<Order> findAllByOrderByOrderDateDesc();
}