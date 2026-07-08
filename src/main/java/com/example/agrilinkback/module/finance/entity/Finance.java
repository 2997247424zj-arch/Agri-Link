package com.example.agrilinkback.module.finance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
        String fileInfo,
        @JsonIgnore String materialText
) {
    @JsonProperty("materials")
    public List<String> materials() {
        if (materialText == null || materialText.isBlank()) {
            return List.of();
        }
        return Arrays.stream(materialText.split("\\R"))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }
}
