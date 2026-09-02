package com.nilgirikitchen.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.nilgirikitchen.billing.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByIsActiveTrue();
    boolean existsByItemCode(String itemCode);
}

