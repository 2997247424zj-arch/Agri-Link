package com.example.agrilinkback.module.finance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Finance(
        Integer financeId,
        Integer bankId,
        String ownName,
        String realName,
        String phone,
        String idNum,
        Integer status,
        String remark,
        BigDecimal money,
        BigDecimal rate,
        String repayment,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        String combinationName1,
        String combinationPhone1,
        String combinationIdnum1,
        String combinationName2,
        String combinationPhone2,
        String combinationIdnum2,
        String fileInfo
) {
}
