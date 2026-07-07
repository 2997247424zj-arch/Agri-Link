package com.example.agrilinkback.module.finance.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.finance.dto.BankRequest;
import com.example.agrilinkback.module.finance.entity.Bank;
import com.example.agrilinkback.module.finance.mapper.BankMapper;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    private final BankMapper bankMapper;

    public BankService(BankMapper bankMapper) {
        this.bankMapper = bankMapper;
    }

    public List<Bank> listBanks() {
        return bankMapper.findAll();
    }

    public List<Bank> matchBanks(BigDecimal amount) {
        return bankMapper.findMatchedBanks(amount);
    }

    public Bank getBank(Integer bankId) {
        Bank bank = bankMapper.findByBankId(bankId);
        if (bank == null) {
            throw new BusinessException(404, "Bank not found");
        }
        return bank;
    }

    public Bank createBank(BankRequest request) {
        Bank bank = new Bank(
                bankMapper.nextId(),
                request.bankName(),
                request.introduce(),
                request.bankPhone(),
                request.money(),
                request.rate(),
                request.repayment()
        );
        bankMapper.insert(bank);
        return getBank(bank.bankId());
    }

    public Bank updateBank(Integer bankId, BankRequest request) {
        getBank(bankId);
        Bank bank = new Bank(
                bankId,
                request.bankName(),
                request.introduce(),
                request.bankPhone(),
                request.money(),
                request.rate(),
                request.repayment()
        );
        bankMapper.update(bank);
        return getBank(bankId);
    }

    public void deleteBank(Integer bankId) {
        getBank(bankId);
        bankMapper.deleteByBankId(bankId);
    }
}
