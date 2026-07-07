package com.example.agrilinkback.module.trade.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.trade.dto.TradeOrderRequest;
import com.example.agrilinkback.module.trade.dto.TradeOrderStatusRequest;
import com.example.agrilinkback.module.trade.entity.TradeOrder;
import com.example.agrilinkback.module.trade.mapper.TradeOrderMapper;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * 农产品货源/销售订单服务。
 *
 * <p>订单状态用于表达货源审核、成交或下架等业务阶段。
 */
@Service
public class TradeOrderService {

    private final TradeOrderMapper tradeOrderMapper;

    public TradeOrderService(TradeOrderMapper tradeOrderMapper) {
        this.tradeOrderMapper = tradeOrderMapper;
    }

    public List<TradeOrder> listOrders() {
        return tradeOrderMapper.findAll();
    }

    public List<TradeOrder> listOrdersByOwner(String ownName) {
        return tradeOrderMapper.findByOwner(ownName);
    }

    public List<TradeOrder> listOrdersByCooperationName(String cooperationName) {
        return tradeOrderMapper.findByCooperationName(cooperationName);
    }

    public TradeOrder getOrder(Integer orderId) {
        TradeOrder order = tradeOrderMapper.findByOrderId(orderId);
        if (order == null) {
            throw new BusinessException(404, "Order not found");
        }
        return order;
    }

    public TradeOrder createOrder(TradeOrderRequest request) {
        LocalDateTime now = LocalDateTime.now();
        // 新发布货源默认处于待处理状态，合作方在成交/状态变更时再写入。
        TradeOrder order = new TradeOrder(
                tradeOrderMapper.nextId(),
                request.title(),
                request.price(),
                request.content(),
                0,
                request.type(),
                request.picture(),
                request.ownName(),
                null,
                now,
                now,
                request.address()
        );
        tradeOrderMapper.insert(order);
        return getOrder(order.orderId());
    }

    public TradeOrder updateOrder(Integer orderId, TradeOrderRequest request) {
        TradeOrder existing = getOrder(orderId);
        TradeOrder order = new TradeOrder(
                orderId,
                request.title(),
                request.price(),
                request.content(),
                existing.orderStatus(),
                request.type(),
                request.picture(),
                request.ownName(),
                existing.cooperationName(),
                existing.createTime(),
                LocalDateTime.now(),
                request.address()
        );
        tradeOrderMapper.update(order);
        return getOrder(orderId);
    }

    public TradeOrder updateOrderStatus(Integer orderId, TradeOrderStatusRequest request) {
        getOrder(orderId);
        tradeOrderMapper.updateStatus(orderId, request.orderStatus(), request.cooperationName());
        return getOrder(orderId);
    }

    public void deleteOrder(Integer orderId) {
        getOrder(orderId);
        tradeOrderMapper.deleteByOrderId(orderId);
    }
}
