package com.example.agrilinkback.module.admin.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeItem;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeRequest;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeStatusRequest;
import com.example.agrilinkback.module.admin.dto.AdminOverview;
import com.example.agrilinkback.module.admin.dto.AdminRoleRequest;
import com.example.agrilinkback.module.admin.service.AdminService;
import com.example.agrilinkback.module.finance.entity.Finance;
import com.example.agrilinkback.module.trade.dto.PurchaseStatusRequest;
import com.example.agrilinkback.module.trade.dto.TradeOrderStatusRequest;
import com.example.agrilinkback.module.trade.entity.Purchase;
import com.example.agrilinkback.module.trade.entity.TradeOrder;
import com.example.agrilinkback.module.user.entity.User;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理员后台接口，聚合用户、交易、融资监管和内容维护能力。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/overview")
    public ApiResponse<AdminOverview> overview() {
        return ApiResponse.success(adminService.getOverview());
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.success(adminService.listUsers());
    }

    @PatchMapping("/users/{userName}/role")
    public ApiResponse<User> updateUserRole(
            @PathVariable String userName,
            @Valid @RequestBody AdminRoleRequest request
    ) {
        return ApiResponse.success(adminService.updateBusinessRole(userName, request.role()));
    }

    @GetMapping("/trade/orders")
    public ApiResponse<List<TradeOrder>> listOrders() {
        return ApiResponse.success(adminService.listOrders());
    }

    @PatchMapping("/trade/orders/{orderId}/status")
    public ApiResponse<TradeOrder> updateOrderStatus(
            @PathVariable Integer orderId,
            @Valid @RequestBody TradeOrderStatusRequest request
    ) {
        return ApiResponse.success(adminService.updateOrderStatus(orderId, request));
    }

    @GetMapping("/trade/purchases")
    public ApiResponse<List<Purchase>> listPurchases() {
        return ApiResponse.success(adminService.listPurchases());
    }

    @PatchMapping("/trade/purchases/{purchaseId}/status")
    public ApiResponse<Purchase> updatePurchaseStatus(
            @PathVariable Integer purchaseId,
            @Valid @RequestBody PurchaseStatusRequest request
    ) {
        return ApiResponse.success(adminService.updatePurchaseStatus(purchaseId, request));
    }

    @GetMapping("/finance/applications")
    public ApiResponse<List<Finance>> listFinanceApplications() {
        return ApiResponse.success(adminService.listFinanceApplications());
    }

    @GetMapping("/knowledge")
    public ApiResponse<List<AdminKnowledgeItem>> listKnowledge() {
        return ApiResponse.success(adminService.listKnowledge());
    }

    @PostMapping("/knowledge")
    public ApiResponse<AdminKnowledgeItem> createKnowledge(
            @Valid @RequestBody AdminKnowledgeRequest request
    ) {
        return ApiResponse.success(adminService.createKnowledge(request));
    }

    @PatchMapping("/knowledge/{knowledgeId}/status")
    public ApiResponse<AdminKnowledgeItem> updateKnowledgeStatus(
            @PathVariable Integer knowledgeId,
            @Valid @RequestBody AdminKnowledgeStatusRequest request
    ) {
        return ApiResponse.success(adminService.updateKnowledgeStatus(knowledgeId, request));
    }

    @PutMapping("/knowledge/{knowledgeId}")
    public ApiResponse<AdminKnowledgeItem> updateKnowledge(
            @PathVariable Integer knowledgeId,
            @Valid @RequestBody AdminKnowledgeRequest request
    ) {
        return ApiResponse.success(adminService.updateKnowledge(knowledgeId, request));
    }

    @DeleteMapping("/knowledge/{knowledgeId}")
    public ApiResponse<Void> deleteKnowledge(@PathVariable Integer knowledgeId) {
        adminService.deleteKnowledge(knowledgeId);
        return ApiResponse.ok();
    }
}
