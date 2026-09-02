package com.nilgirikitchen.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nilgirikitchen.billing.entity.MenuItemVariant;

public interface MenuItemVariantRepository extends JpaRepository<MenuItemVariant, Long> {}

