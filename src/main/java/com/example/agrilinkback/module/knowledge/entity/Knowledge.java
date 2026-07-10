package com.example.agrilinkback.module.knowledge.entity;

import java.time.LocalDateTime;

public record Knowledge(
        Integer knowledgeId,
        String title,
        String content,
        String picPath,
        String ownName,
        Integer status,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}
