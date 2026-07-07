package com.example.agrilinkback.module.finance.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.finance.dto.BankRequest;
import com.example.agrilinkback.module.finance.entity.Bank;
import com.example.agrilinkback.module.finance.service.BankService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 银行贷款产品接口，支持产品维护和农户侧智能匹配。
 */
@RestController
@RequestMapping("/api/finance/banks")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ApiResponse<List<Bank>> listBanks() {
        return ApiResponse.success(bankService.listBanks());
    }

    @GetMapping("/matches")
    public ApiResponse<List<Bank>> matchBanks(@RequestParam java.math.BigDecimal amount) {
        return ApiResponse.success(bankService.matchBanks(amount));
    }

    @GetMapping("/{bankId}")
    public ApiResponse<Bank> getBank(@PathVariable Integer bankId) {
        return ApiResponse.success(bankService.getBank(bankId));
    }

    @PostMapping
    public ApiResponse<Bank> createBank(@Valid @RequestBody BankRequest request) {
        return ApiResponse.success(bankService.createBank(request));
    }

    @PutMapping("/{bankId}")
    public ApiResponse<Bank> updateBank(@PathVariable Integer bankId, @Valid @RequestBody BankRequest request) {
        return ApiResponse.success(bankService.updateBank(bankId, request));
    }

    @DeleteMapping("/{bankId}")
    public ApiResponse<Void> deleteBank(@PathVariable Integer bankId) {
        bankService.deleteBank(bankId);
        return ApiResponse.ok();
    }
}
