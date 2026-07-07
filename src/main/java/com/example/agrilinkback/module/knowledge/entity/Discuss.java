package com.example.agrilinkback.module.knowledge.entity;

import java.time.LocalDateTime;

public record Discuss(
        Integer discussId,
        Integer knowledgeId,
        String ownName,
        String content,
        LocalDateTime createTime
) {
}
