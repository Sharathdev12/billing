package com.nilgirikitchen.billing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.nilgirikitchen.billing.dto.CreateMenuItemRequest;
import com.nilgirikitchen.billing.dto.MenuItemResponse;
import com.nilgirikitchen.billing.service.MenuItemService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemService menuItemService;

    @GetMapping
    public List<MenuItemResponse> getMenu() {
        return menuItemService.getActiveMenu();
    }

    @PostMapping
    public MenuItemResponse addItem(@RequestBody CreateMenuItemRequest request) {
        return menuItemService.createMenuItem(request);
    }
}