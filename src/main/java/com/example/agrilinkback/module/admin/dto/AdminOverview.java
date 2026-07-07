package com.example.agrilinkback.module.admin.dto;

import java.util.Map;

public record AdminOverview(
        int userCount,
        int orderCount,
        int purchaseCount,
        int financeApplicationCount,
        int financingIntentionCount,
        int knowledgeCount,
        Map<String, Long> usersByRole
) {
}
