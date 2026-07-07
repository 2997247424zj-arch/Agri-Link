package com.example.agrilinkback.module.trade.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.trade.dto.ShoppingCartRequest;
import com.example.agrilinkback.module.trade.entity.ShoppingCart;
import com.example.agrilinkback.module.trade.service.ShoppingCartService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public ApiResponse<List<ShoppingCart>> listShoppingCarts() {
        return ApiResponse.success(shoppingCartService.listShoppingCarts());
    }

    @GetMapping("/owners/{ownName}")
    public ApiResponse<List<ShoppingCart>> listShoppingCartsByOwner(@PathVariable String ownName) {
        return ApiResponse.success(shoppingCartService.listShoppingCartsByOwner(ownName));
    }

    @GetMapping("/{shoppingId}")
    public ApiResponse<ShoppingCart> getShoppingCart(@PathVariable Integer shoppingId) {
        return ApiResponse.success(shoppingCartService.getShoppingCart(shoppingId));
    }

    @PostMapping
    public ApiResponse<ShoppingCart> createShoppingCart(@Valid @RequestBody ShoppingCartRequest request) {
        return ApiResponse.success(shoppingCartService.createShoppingCart(request));
    }

    @PutMapping("/{shoppingId}")
    public ApiResponse<ShoppingCart> updateShoppingCart(
            @PathVariable Integer shoppingId,
            @Valid @RequestBody ShoppingCartRequest request
    ) {
        return ApiResponse.success(shoppingCartService.updateShoppingCart(shoppingId, request));
    }

    @DeleteMapping("/{shoppingId}")
    public ApiResponse<Void> deleteShoppingCart(@PathVariable Integer shoppingId) {
        shoppingCartService.deleteShoppingCart(shoppingId);
        return ApiResponse.ok();
    }
}
