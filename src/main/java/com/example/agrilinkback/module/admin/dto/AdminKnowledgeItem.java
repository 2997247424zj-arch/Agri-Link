package com.example.agrilinkback.module.admin.dto;

import com.example.agrilinkback.module.knowledge.entity.Knowledge;
import java.time.LocalDateTime;

/**
 * ????/??????
 *
 * <p>????????????????????? id?category?summary ???
 */
public record AdminKnowledgeItem(
        Integer id,
        String title,
        String category,
        String summary,
        Integer status,
        LocalDateTime createTime
) {
    /**
     * ??????????????????
     */
    public static AdminKnowledgeItem fromKnowledge(Knowledge knowledge) {
        return new AdminKnowledgeItem(
                knowledge.knowledgeId(),
                knowledge.title(),
                knowledge.ownName(),
                knowledge.content(),
                knowledge.status() == null ? 1 : knowledge.status(),
                knowledge.createTime()
        );
    }
}
