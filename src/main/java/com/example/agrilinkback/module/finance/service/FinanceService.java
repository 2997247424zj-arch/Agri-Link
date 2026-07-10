package com.example.agrilinkback.module.finance.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.finance.dto.FinanceApplicationRequest;
import com.example.agrilinkback.module.finance.dto.FinanceMaterialsRequest;
import com.example.agrilinkback.module.finance.dto.FinanceStatusRequest;
import com.example.agrilinkback.module.finance.dto.FinancingIntentionRequest;
import com.example.agrilinkback.module.finance.entity.Finance;
import com.example.agrilinkback.module.finance.entity.FinancingIntention;
import com.example.agrilinkback.module.finance.mapper.FinanceMapper;
import com.example.agrilinkback.module.finance.mapper.FinancingIntentionMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 融资申请与融资意向服务。
 *
 * <p>融资申请表示农户向银行提交的正式申请，融资意向用于银行侧智能匹配潜在农户。
 */
@Service
public class FinanceService {

    private final FinanceMapper financeMapper;
    private final FinancingIntentionMapper financingIntentionMapper;
    private final BankService bankService;

    public FinanceService(
            FinanceMapper financeMapper,
            FinancingIntentionMapper financingIntentionMapper,
            BankService bankService
    ) {
        this.financeMapper = financeMapper;
        this.financingIntentionMapper = financingIntentionMapper;
        this.bankService = bankService;
    }

    public List<Finance> listFinances() {
        return financeMapper.findAll();
    }

    public Finance getFinance(Integer financeId) {
        Finance finance = financeMapper.findByFinanceId(financeId);
        if (finance == null) {
            throw new BusinessException(404, "Finance not found");
        }
        return finance;
    }

    public List<FinancingIntention> listIntentions() {
        return financingIntentionMapper.findAll();
    }

    public List<FinancingIntention> matchFarmers(Integer bankId) {
        // 银行侧匹配以银行产品额度为输入，筛选符合额度范围的农户融资意向。
        return financingIntentionMapper.findMatchedIntentions(bankService.getBank(bankId).money());
    }

    public FinancingIntention getIntention(Integer id) {
        FinancingIntention intention = financingIntentionMapper.findById(id);
        if (intention == null) {
            throw new BusinessException(404, "Financing intention not found");
        }
        return intention;
    }

    public FinancingIntention createIntention(FinancingIntentionRequest request) {
        LocalDateTime now = LocalDateTime.now();
        FinancingIntention intention = new FinancingIntention(
                financingIntentionMapper.nextId(),
                request.userName(),
                request.realName(),
                request.address(),
                request.amount(),
                request.application(),
                request.item(),
                request.repaymentPeriod(),
                request.area(),
                request.phone(),
                now,
                now
        );
        financingIntentionMapper.insert(intention);
        return getIntention(intention.id());
    }

    public FinancingIntention updateIntention(Integer id, FinancingIntentionRequest request) {
        FinancingIntention existing = getIntention(id);
        FinancingIntention intention = new FinancingIntention(
                id,
                existing.userName(),
                request.realName(),
                request.address(),
                request.amount(),
                request.application(),
                request.item(),
                request.repaymentPeriod(),
                request.area(),
                request.phone(),
                existing.createTime(),
                LocalDateTime.now()
        );
        financingIntentionMapper.update(intention);
        return getIntention(id);
    }

    public void deleteIntention(Integer id) {
        getIntention(id);
        financingIntentionMapper.deleteById(id);
    }

    public Finance createFinance(FinanceApplicationRequest request) {
        LocalDateTime now = LocalDateTime.now();
        // 新融资申请默认待审核，后续由银行更新状态与备注。
        Finance finance = new Finance(
                financeMapper.nextId(),
                request.bankId(),
                request.ownName(),
                request.realName(),
                request.phone(),
                request.idNum(),
                0,
                "pending review",
                request.money(),
                request.rate(),
                request.repayment(),
                now,
                now,
                request.combinationName1(),
                request.combinationPhone1(),
                request.combinationIdnum1(),
                request.combinationName2(),
                request.combinationPhone2(),
                request.combinationIdnum2(),
                request.fileInfo(),
                null
        );
        financeMapper.insert(finance);
        return getFinance(finance.financeId());
    }

    public Finance updateFinanceStatus(Integer financeId, FinanceStatusRequest request) {
        getFinance(financeId);
        financeMapper.updateStatus(financeId, request.status(), request.remark());
        return getFinance(financeId);
    }

    public Finance updateFinanceMaterials(Integer financeId, FinanceMaterialsRequest request) {
        getFinance(financeId);
        String materials = request.materials() == null
                ? null
                : request.materials().stream()
                .filter(item -> item != null && !item.isBlank())
                .map(String::trim)
                .collect(Collectors.joining("\n"));
        financeMapper.updateMaterials(financeId, materials);
        return getFinance(financeId);
    }

    public void deleteFinance(Integer financeId) {
        getFinance(financeId);
        financeMapper.deleteByFinanceId(financeId);
    }
}
