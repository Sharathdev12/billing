package com.nilgirikitchen.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.nilgirikitchen.billing.repository.MenuItemRepository;
import com.nilgirikitchen.billing.entity.MenuItem;
import com.nilgirikitchen.billing.entity.MenuItemVariant;
import com.nilgirikitchen.billing.dto.CreateMenuItemRequest;
import com.nilgirikitchen.billing.dto.MenuItemResponse;
import com.nilgirikitchen.billing.dto.MenuItemVariantResponse;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public List<MenuItemResponse> getActiveMenu() {
        return menuItemRepository.findByIsActiveTrue()
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public MenuItemResponse createMenuItem(CreateMenuItemRequest request) {
        if (request.itemCode() == null || request.itemCode().isBlank()) {
            throw new IllegalArgumentException("Item code is required");
        }
        if (menuItemRepository.existsByItemCode(request.itemCode().trim())) {
            throw new IllegalArgumentException("Item code '" + request.itemCode() + "' is already in use");
        }
        if (request.category() == null) {
            throw new IllegalArgumentException("Category is required");
        }

        MenuItem item = new MenuItem();
        item.setItemCode(request.itemCode().trim());
        item.setName(request.name());
        item.setType(request.type());
        item.setCategory(request.category());
        item.setIsActive(true);

        List<MenuItemVariant> variants = request.variants().stream().map(v -> {
            MenuItemVariant variant = new MenuItemVariant();
            variant.setVariantName(v.variantName());
            variant.setPrice(v.price());
            variant.setMenuItem(item);
            return variant;
        }).toList();

        item.setVariants(variants);
        return toResponse(menuItemRepository.save(item));
    }

    private MenuItemResponse toResponse(MenuItem item) {
        List<MenuItemVariantResponse> variantResponses = item.getVariants().stream()
            .map(v -> new MenuItemVariantResponse(v.getId(), v.getVariantName(), v.getPrice()))
            .toList();
        return new MenuItemResponse(item.getId(), item.getItemCode(), item.getName(),
                 item.getType(), item.getCategory(), variantResponses);
    }
}
