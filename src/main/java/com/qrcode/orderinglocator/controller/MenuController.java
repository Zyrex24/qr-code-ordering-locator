package com.qrcode.orderinglocator.controller;

import com.qrcode.orderinglocator.dto.menu.MenuResponse;
import com.qrcode.orderinglocator.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "Menu and catalog endpoints")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/menu")
    @Operation(summary = "Get menu", description = "Get menu with categories and products, optionally filtered by table")
    public ResponseEntity<MenuResponse> getMenu(
            @Parameter(description = "Table ID to get menu for specific table")
            @RequestParam(required = false) Long table_id) {
        MenuResponse menu = menuService.getMenu(table_id);
        return ResponseEntity.ok(menu);
    }
}