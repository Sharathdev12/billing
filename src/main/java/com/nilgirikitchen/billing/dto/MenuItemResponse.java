package com.nilgirikitchen.billing.dto;

import com.nilgirikitchen.billing.enums.FoodType;
import com.nilgirikitchen.billing.enums.MenuCategory;
import java.util.List;

public record MenuItemResponse(Long id, String itemCode, String name, FoodType type, MenuCategory category,
                                List<MenuItemVariantResponse> variants) {}
