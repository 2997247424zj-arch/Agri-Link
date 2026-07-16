package com.example.agrilinkback.module.admin.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.admin.dto.AdminEnabledRequest;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeItem;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeRequest;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeStatusRequest;
import com.example.agrilinkback.module.admin.dto.AdminOverview;
import com.example.agrilinkback.module.admin.dto.AdminRoleRequest;
import com.example.agrilinkback.module.admin.dto.AdminUserUpdateRequest;
import com.example.agrilinkback.module.admin.service.AdminService;
import com.example.agrilinkback.module.admin.util.ExcelExportUtil;
import com.example.agrilinkback.module.finance.entity.Finance;
import com.example.agrilinkback.module.trade.dto.PurchaseStatusRequest;
import com.example.agrilinkback.module.trade.dto.TradeOrderStatusRequest;
import com.example.agrilinkback.module.trade.entity.Purchase;
import com.example.agrilinkback.module.trade.entity.TradeOrder;
import com.example.agrilinkback.module.user.entity.User;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // ---- 概览 ----

    @GetMapping("/overview")
    public ApiResponse<AdminOverview> overview() {
        return ApiResponse.success(adminService.getOverview());
    }

    // ---- 用户管理 ----

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

    @PatchMapping("/users/{userName}/enabled")
    public ApiResponse<User> toggleUserEnabled(
            @PathVariable String userName,
            @Valid @RequestBody AdminEnabledRequest request
    ) {
        return ApiResponse.success(adminService.toggleUserEnabled(userName, request.enabled()));
    }

    @PutMapping("/users/{userName}")
    public ApiResponse<User> updateUserProfile(
            @PathVariable String userName,
            @Valid @RequestBody AdminUserUpdateRequest request
    ) {
        return ApiResponse.success(adminService.updateUserProfile(userName, request));
    }

    @DeleteMapping("/users/{userName}")
    public ApiResponse<Void> deleteUser(@PathVariable String userName) {
        adminService.deleteUser(userName);
        return ApiResponse.ok();
    }

    // ---- 交易监管 ----

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

    // ---- 融资监管 ----

    @GetMapping("/finance/applications")
    public ApiResponse<List<Finance>> listFinanceApplications() {
        return ApiResponse.success(adminService.listFinanceApplications());
    }

    // ---- 内容管理 ----

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

    // ---- 数据导出 ----

    @GetMapping("/users/export")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false, defaultValue = "") String keyword
    ) throws IOException {
        List<User> data = adminService.listUsers();
        if (!keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            data = data.stream().filter(u ->
                    contains(u.userName(), kw) || contains(u.nickName(), kw) ||
                    contains(u.phone(), kw) || contains(u.realName(), kw)
            ).toList();
        }
        return ExcelExportUtil.export("用户列表",
                new String[]{"账号", "昵称", "真实姓名", "身份证号", "手机号", "角色", "状态"},
                data, "用户导出.xlsx",
                User::userName,
                u -> u.nickName() != null ? u.nickName() : "",
                u -> u.realName() != null ? u.realName() : "",
                u -> u.identityNum() != null ? u.identityNum() : "",
                u -> u.phone() != null ? u.phone() : "",
                User::role,
                u -> Boolean.FALSE.equals(u.enabled()) ? "禁用" : "启用"
        );
    }

    @GetMapping("/trade/orders/export")
    public ResponseEntity<byte[]> exportOrders(
            @RequestParam(required = false) Integer status
    ) throws IOException {
        List<TradeOrder> data = adminService.listOrders();
        if (status != null) {
            data = data.stream().filter(o -> status.equals(o.orderStatus())).toList();
        }
        return ExcelExportUtil.export("货源列表",
                new String[]{"编号", "标题", "价格", "发布者", "状态", "创建时间"},
                data, "货源导出.xlsx",
                TradeOrder::orderId,
                TradeOrder::title,
                o -> o.price() != null ? o.price() : "",
                o -> o.ownName() != null ? o.ownName() : "",
                o -> statusLabel(o.orderStatus()),
                o -> o.createTime() != null ? o.createTime().toString() : ""
        );
    }

    @GetMapping("/trade/purchases/export")
    public ResponseEntity<byte[]> exportPurchases(
            @RequestParam(required = false) Integer status
    ) throws IOException {
        List<Purchase> data = adminService.listPurchases();
        if (status != null) {
            data = data.stream().filter(p -> status.equals(p.purchaseStatus())).toList();
        }
        return ExcelExportUtil.export("采购列表",
                new String[]{"编号", "买家", "总价", "地址", "状态", "创建时间"},
                data, "采购导出.xlsx",
                Purchase::purchaseId,
                Purchase::ownName,
                p -> p.totalPrice() != null ? p.totalPrice() : "",
                p -> p.address() != null ? p.address() : "",
                p -> purchaseLabel(p.purchaseStatus()),
                p -> p.createTime() != null ? p.createTime().toString() : ""
        );
    }

    @GetMapping("/finance/applications/export")
    public ResponseEntity<byte[]> exportFinanceApplications(
            @RequestParam(required = false) Integer status
    ) throws IOException {
        List<Finance> data = adminService.listFinanceApplications();
        if (status != null) {
            data = data.stream().filter(f -> status.equals(f.status())).toList();
        }
        return ExcelExportUtil.export("融资申请列表",
                new String[]{"编号", "申请人", "金额", "利率", "还款方式", "状态"},
                data, "融资导出.xlsx",
                Finance::financeId,
                f -> f.realName() != null ? f.realName() : f.ownName(),
                f -> f.money() != null ? f.money() : "",
                f -> f.rate() != null ? f.rate() : "",
                f -> f.repayment() != null ? f.repayment() : "",
                f -> statusLabel(f.status())
        );
    }

    // ---- 工具方法 ----

    private static boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private static String statusLabel(Integer status) {
        if (status == null || status == 0) return "待审批";
        if (status == 1) return "已通过";
        if (status == 2) return "已拒绝";
        return String.valueOf(status);
    }

    private static String purchaseLabel(Integer status) {
        if (status == null || status == 0) return "待确认";
        if (status == 1) return "已确认";
        if (status == 2) return "已取消";
        return String.valueOf(status);
    }
}
