package com.nilgirikitchen.billing.dto;

import com.nilgirikitchen.billing.enums.FoodType;
import java.util.List;

public record CreateMenuItemRequest(String itemCode, String name, FoodType type, List<MenuItemVariantRequest> variants) {}
