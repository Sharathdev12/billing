package com.nilgirikitchen.billing.dto;

import com.nilgirikitchen.billing.enums.FoodType;
import java.util.List;

public record MenuItemResponse(Long id, String itemCode, String name, FoodType type, List<MenuItemVariantResponse> variants) {}
