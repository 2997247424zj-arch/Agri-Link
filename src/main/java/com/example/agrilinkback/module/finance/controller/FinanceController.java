package com.example.agrilinkback.module.finance.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.finance.dto.FinanceApplicationRequest;
import com.example.agrilinkback.module.finance.dto.FinanceMaterialsRequest;
import com.example.agrilinkback.module.finance.dto.FinanceStatusRequest;
import com.example.agrilinkback.module.finance.dto.FinancingIntentionRequest;
import com.example.agrilinkback.module.finance.entity.Finance;
import com.example.agrilinkback.module.finance.entity.FinancingIntention;
import com.example.agrilinkback.module.finance.service.FinanceService;
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
 * 融资接口，覆盖融资申请、融资意向和银行侧农户匹配。
 */
@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/applications")
    public ApiResponse<List<Finance>> listFinances() {
        return ApiResponse.success(financeService.listFinances());
    }

    @GetMapping("/applications/{financeId}")
    public ApiResponse<Finance> getFinance(@PathVariable Integer financeId) {
        return ApiResponse.success(financeService.getFinance(financeId));
    }

    @PostMapping("/applications")
    public ApiResponse<Finance> createFinance(@Valid @RequestBody FinanceApplicationRequest request) {
        return ApiResponse.success(financeService.createFinance(request));
    }

    @PatchMapping("/applications/{financeId}/status")
    public ApiResponse<Finance> updateFinanceStatus(
            @PathVariable Integer financeId,
            @Valid @RequestBody FinanceStatusRequest request
    ) {
        return ApiResponse.success(financeService.updateFinanceStatus(financeId, request));
    }

    @PatchMapping("/applications/{financeId}/materials")
    public ApiResponse<Finance> updateFinanceMaterials(
            @PathVariable Integer financeId,
            @RequestBody FinanceMaterialsRequest request
    ) {
        return ApiResponse.success(financeService.updateFinanceMaterials(financeId, request));
    }

    @DeleteMapping("/applications/{financeId}")
    public ApiResponse<Void> deleteFinance(@PathVariable Integer financeId) {
        financeService.deleteFinance(financeId);
        return ApiResponse.ok();
    }

    @GetMapping("/intentions")
    public ApiResponse<List<FinancingIntention>> listIntentions() {
        return ApiResponse.success(financeService.listIntentions());
    }

    @GetMapping("/matches/farmers/{bankId}")
    public ApiResponse<List<FinancingIntention>> matchFarmers(@PathVariable Integer bankId) {
        return ApiResponse.success(financeService.matchFarmers(bankId));
    }

    @GetMapping("/intentions/{id}")
    public ApiResponse<FinancingIntention> getIntention(@PathVariable Integer id) {
        return ApiResponse.success(financeService.getIntention(id));
    }

    @PostMapping("/intentions")
    public ApiResponse<FinancingIntention> createIntention(@Valid @RequestBody FinancingIntentionRequest request) {
        return ApiResponse.success(financeService.createIntention(request));
    }

    @PutMapping("/intentions/{id}")
    public ApiResponse<FinancingIntention> updateIntention(
            @PathVariable Integer id,
            @Valid @RequestBody FinancingIntentionRequest request
    ) {
        return ApiResponse.success(financeService.updateIntention(id, request));
    }

    @DeleteMapping("/intentions/{id}")
    public ApiResponse<Void> deleteIntention(@PathVariable Integer id) {
        financeService.deleteIntention(id);
        return ApiResponse.ok();
    }
}
