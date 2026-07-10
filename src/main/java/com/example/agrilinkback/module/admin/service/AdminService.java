package com.example.agrilinkback.module.admin.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.common.security.UserRole;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeItem;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeRequest;
import com.example.agrilinkback.module.admin.dto.AdminKnowledgeStatusRequest;
import com.example.agrilinkback.module.admin.dto.AdminOverview;
import com.example.agrilinkback.module.finance.entity.Finance;
import com.example.agrilinkback.module.finance.service.FinanceService;
import com.example.agrilinkback.module.knowledge.dto.KnowledgeRequest;
import com.example.agrilinkback.module.knowledge.service.KnowledgeService;
import com.example.agrilinkback.module.trade.dto.PurchaseStatusRequest;
import com.example.agrilinkback.module.trade.dto.TradeOrderStatusRequest;
import com.example.agrilinkback.module.trade.entity.Purchase;
import com.example.agrilinkback.module.trade.entity.TradeOrder;
import com.example.agrilinkback.module.trade.service.PurchaseService;
import com.example.agrilinkback.module.trade.service.TradeOrderService;
import com.example.agrilinkback.module.user.entity.User;
import com.example.agrilinkback.module.user.mapper.UserMapper;
import com.example.agrilinkback.module.user.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 系统管理员聚合服务。
 *
 * <p>后台接口不直接绕过业务服务，而是复用各模块服务，保证状态变更规则一致。
 */
@Service
public class AdminService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final TradeOrderService tradeOrderService;
    private final PurchaseService purchaseService;
    private final FinanceService financeService;
    private final KnowledgeService knowledgeService;

    public AdminService(
            UserService userService,
            UserMapper userMapper,
            TradeOrderService tradeOrderService,
            PurchaseService purchaseService,
            FinanceService financeService,
            KnowledgeService knowledgeService
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.tradeOrderService = tradeOrderService;
        this.purchaseService = purchaseService;
        this.financeService = financeService;
        this.knowledgeService = knowledgeService;
    }

    public AdminOverview getOverview() {
        List<User> users = userService.listUsers();
        // 概览页需要按角色统计用户分布，用于后台首页数据看板。
        Map<String, Long> usersByRole = users.stream()
                .collect(Collectors.groupingBy(User::role, Collectors.counting()));

        return new AdminOverview(
                users.size(),
                tradeOrderService.listOrders().size(),
                purchaseService.listPurchases().size(),
                financeService.listFinances().size(),
                financeService.listIntentions().size(),
                knowledgeService.listKnowledge().size(),
                usersByRole
        );
    }

    public List<User> listUsers() {
        return userService.listUsers();
    }

    public User updateBusinessRole(String userName, String role) {
        User existing = userService.getUser(userName);
        UserRole nextRole = requireBusinessRole(role);
        if (UserRole.SYSTEM_ADMIN.code().equals(existing.role())) {
            // 系统管理员账号不参与业务角色切换，避免误降权导致后台无法管理。
            throw new BusinessException("System admin role cannot be managed as a business role");
        }
        userMapper.updateRole(userName, nextRole.code());
        return userService.getUser(userName);
    }

    public List<TradeOrder> listOrders() {
        return tradeOrderService.listOrders();
    }

    public TradeOrder updateOrderStatus(Integer orderId, TradeOrderStatusRequest request) {
        return tradeOrderService.updateOrderStatus(orderId, request);
    }

    public List<Purchase> listPurchases() {
        return purchaseService.listPurchases();
    }

    public Purchase updatePurchaseStatus(Integer purchaseId, PurchaseStatusRequest request) {
        return purchaseService.updatePurchaseStatus(purchaseId, request);
    }

    public List<Finance> listFinanceApplications() {
        return financeService.listFinances();
    }

    /**
     * ???????????????????????????????????
     */
    public List<AdminKnowledgeItem> listKnowledge() {
        return knowledgeService.listKnowledge().stream()
                .map(AdminKnowledgeItem::fromKnowledge)
                .toList();
    }

    public AdminKnowledgeItem createKnowledge(AdminKnowledgeRequest request) {
        return AdminKnowledgeItem.fromKnowledge(knowledgeService.createKnowledge(toKnowledgeRequest(request)));
    }

    public AdminKnowledgeItem updateKnowledge(Integer knowledgeId, AdminKnowledgeRequest request) {
        return AdminKnowledgeItem.fromKnowledge(knowledgeService.updateKnowledge(knowledgeId, toKnowledgeRequest(request)));
    }

    public AdminKnowledgeItem updateKnowledgeStatus(
            Integer knowledgeId,
            AdminKnowledgeStatusRequest request
    ) {
        return AdminKnowledgeItem.fromKnowledge(
                knowledgeService.updateKnowledgeStatus(knowledgeId, request.status())
        );
    }

    public void deleteKnowledge(Integer knowledgeId) {
        knowledgeService.deleteKnowledge(knowledgeId);
    }

    /**
     * ????????????????????????????????
     */
    private UserRole requireBusinessRole(String role) {
        UserRole userRole = UserRole.fromCode(role)
                .orElseThrow(() -> new BusinessException("Role must be BUYER, FARMER, EXPERT or BANK"));
        if (!userRole.isBusinessRole()) {
            throw new BusinessException("Role must be BUYER, FARMER, EXPERT or BANK");
        }
        return userRole;
    }

    /**
     * ??????????????category ??? Knowledge.ownName?summary ??????
     */
    private KnowledgeRequest toKnowledgeRequest(AdminKnowledgeRequest request) {
        String category = request.category() == null || request.category().isBlank()
                ? "平台资讯"
                : request.category().trim();
        return new KnowledgeRequest(request.title(), request.summary(), null, category);
    }
}
