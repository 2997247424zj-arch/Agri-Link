package com.example.agrilinkback.module.trade.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.trade.dto.TradeOrderRequest;
import com.example.agrilinkback.module.trade.dto.TradeOrderStatusRequest;
import com.example.agrilinkback.module.trade.entity.TradeOrder;
import com.example.agrilinkback.module.trade.service.TradeOrderService;
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
 * 货源/销售订单接口，农户发布货源，买家和后台查询处理。
 */
@RestController
@RequestMapping("/api/trade/orders")
public class TradeOrderController {

    private final TradeOrderService tradeOrderService;

    public TradeOrderController(TradeOrderService tradeOrderService) {
        this.tradeOrderService = tradeOrderService;
    }

    @GetMapping
    public ApiResponse<List<TradeOrder>> listOrders() {
        return ApiResponse.success(tradeOrderService.listOrders());
    }

    @GetMapping("/owners/{ownName}")
    public ApiResponse<List<TradeOrder>> listOrdersByOwner(@PathVariable String ownName) {
        return ApiResponse.success(tradeOrderService.listOrdersByOwner(ownName));
    }

    @GetMapping("/cooperators/{cooperationName}")
    public ApiResponse<List<TradeOrder>> listOrdersByCooperationName(@PathVariable String cooperationName) {
        return ApiResponse.success(tradeOrderService.listOrdersByCooperationName(cooperationName));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<TradeOrder> getOrder(@PathVariable Integer orderId) {
        return ApiResponse.success(tradeOrderService.getOrder(orderId));
    }

    @PostMapping
    public ApiResponse<TradeOrder> createOrder(@Valid @RequestBody TradeOrderRequest request) {
        return ApiResponse.success(tradeOrderService.createOrder(request));
    }

    @PutMapping("/{orderId}")
    public ApiResponse<TradeOrder> updateOrder(
            @PathVariable Integer orderId,
            @Valid @RequestBody TradeOrderRequest request
    ) {
        return ApiResponse.success(tradeOrderService.updateOrder(orderId, request));
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<TradeOrder> updateOrderStatus(
            @PathVariable Integer orderId,
            @Valid @RequestBody TradeOrderStatusRequest request
    ) {
        return ApiResponse.success(tradeOrderService.updateOrderStatus(orderId, request));
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> deleteOrder(@PathVariable Integer orderId) {
        tradeOrderService.deleteOrder(orderId);
        return ApiResponse.ok();
    }
}
