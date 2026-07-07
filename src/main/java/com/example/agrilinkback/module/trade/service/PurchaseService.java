package com.example.agrilinkback.module.trade.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.trade.dto.PurchaseDetailRequest;
import com.example.agrilinkback.module.trade.dto.PurchaseRequest;
import com.example.agrilinkback.module.trade.dto.PurchaseStatusRequest;
import com.example.agrilinkback.module.trade.entity.Purchase;
import com.example.agrilinkback.module.trade.entity.PurchaseDetail;
import com.example.agrilinkback.module.trade.mapper.PurchaseMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseService {

    private final PurchaseMapper purchaseMapper;

    public PurchaseService(PurchaseMapper purchaseMapper) {
        this.purchaseMapper = purchaseMapper;
    }

    public List<Purchase> listPurchases() {
        return purchaseMapper.findAll();
    }

    public List<Purchase> listPurchasesByOwner(String ownName) {
        return purchaseMapper.findByOwner(ownName);
    }

    public Purchase getPurchase(Integer purchaseId) {
        Purchase purchase = purchaseMapper.findByPurchaseId(purchaseId);
        if (purchase == null) {
            throw new BusinessException(404, "Purchase not found");
        }
        return purchase;
    }

    public List<PurchaseDetail> listPurchaseDetails(Integer purchaseId) {
        getPurchase(purchaseId);
        return purchaseMapper.findDetailsByPurchaseId(purchaseId);
    }

    @Transactional
    public Purchase createPurchase(PurchaseRequest request) {
        Integer purchaseId = purchaseMapper.nextPurchaseId();
        BigDecimal totalPrice = request.details()
                .stream()
                .map(detail -> detail.unitPrice().multiply(BigDecimal.valueOf(detail.count())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDateTime now = LocalDateTime.now();
        Purchase purchase = new Purchase(
                purchaseId,
                request.ownName(),
                request.purchaseType(),
                totalPrice,
                request.address(),
                0,
                now,
                now
        );
        purchaseMapper.insertPurchase(purchase);

        int nextDetailId = purchaseMapper.nextDetailId();
        for (PurchaseDetailRequest detailRequest : request.details()) {
            BigDecimal sumPrice = detailRequest.unitPrice().multiply(BigDecimal.valueOf(detailRequest.count()));
            PurchaseDetail detail = new PurchaseDetail(
                    nextDetailId++,
                    purchaseId,
                    detailRequest.orderId(),
                    detailRequest.unitPrice(),
                    sumPrice,
                    detailRequest.count()
            );
            purchaseMapper.insertDetail(detail);
        }
        return getPurchase(purchaseId);
    }

    public Purchase updatePurchaseStatus(Integer purchaseId, PurchaseStatusRequest request) {
        getPurchase(purchaseId);
        purchaseMapper.updateStatus(purchaseId, request.purchaseStatus());
        return getPurchase(purchaseId);
    }

    @Transactional
    public void deletePurchase(Integer purchaseId) {
        getPurchase(purchaseId);
        purchaseMapper.deleteDetailsByPurchaseId(purchaseId);
        purchaseMapper.deleteByPurchaseId(purchaseId);
    }
}
