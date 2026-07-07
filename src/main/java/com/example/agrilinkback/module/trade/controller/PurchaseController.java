package com.example.agrilinkback.module.trade.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.trade.dto.PurchaseRequest;
import com.example.agrilinkback.module.trade.dto.PurchaseStatusRequest;
import com.example.agrilinkback.module.trade.entity.Purchase;
import com.example.agrilinkback.module.trade.entity.PurchaseDetail;
import com.example.agrilinkback.module.trade.service.PurchaseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 采购订单接口，处理买家下单、订单明细和采购状态。
 */
@RestController
@RequestMapping("/api/trade/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public ApiResponse<List<Purchase>> listPurchases() {
        return ApiResponse.success(purchaseService.listPurchases());
    }

    @GetMapping("/owners/{ownName}")
    public ApiResponse<List<Purchase>> listPurchasesByOwner(@PathVariable String ownName) {
        return ApiResponse.success(purchaseService.listPurchasesByOwner(ownName));
    }

    @GetMapping("/{purchaseId}")
    public ApiResponse<Purchase> getPurchase(@PathVariable Integer purchaseId) {
        return ApiResponse.success(purchaseService.getPurchase(purchaseId));
    }

    @GetMapping("/{purchaseId}/details")
    public ApiResponse<List<PurchaseDetail>> listPurchaseDetails(@PathVariable Integer purchaseId) {
        return ApiResponse.success(purchaseService.listPurchaseDetails(purchaseId));
    }

    @PostMapping
    public ApiResponse<Purchase> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        return ApiResponse.success(purchaseService.createPurchase(request));
    }

    @PatchMapping("/{purchaseId}/status")
    public ApiResponse<Purchase> updatePurchaseStatus(
            @PathVariable Integer purchaseId,
            @Valid @RequestBody PurchaseStatusRequest request
    ) {
        return ApiResponse.success(purchaseService.updatePurchaseStatus(purchaseId, request));
    }

    @DeleteMapping("/{purchaseId}")
    public ApiResponse<Void> deletePurchase(@PathVariable Integer purchaseId) {
        purchaseService.deletePurchase(purchaseId);
        return ApiResponse.ok();
    }
}
