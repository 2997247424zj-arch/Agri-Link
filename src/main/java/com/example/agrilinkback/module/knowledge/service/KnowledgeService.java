package com.example.agrilinkback.module.knowledge.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.knowledge.dto.KnowledgeRequest;
import com.example.agrilinkback.module.knowledge.entity.Knowledge;
import com.example.agrilinkback.module.knowledge.mapper.DiscussMapper;
import com.example.agrilinkback.module.knowledge.mapper.KnowledgeMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KnowledgeService {

    private final KnowledgeMapper knowledgeMapper;
    private final DiscussMapper discussMapper;

    public KnowledgeService(KnowledgeMapper knowledgeMapper, DiscussMapper discussMapper) {
        this.knowledgeMapper = knowledgeMapper;
        this.discussMapper = discussMapper;
    }

    public List<Knowledge> listKnowledge() {
        return knowledgeMapper.findAll();
    }

    public Knowledge getKnowledge(Integer knowledgeId) {
        Knowledge knowledge = knowledgeMapper.findByKnowledgeId(knowledgeId);
        if (knowledge == null) {
            throw new BusinessException(404, "Knowledge not found");
        }
        return knowledge;
    }

    public Knowledge createKnowledge(KnowledgeRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Knowledge knowledge = new Knowledge(
                knowledgeMapper.nextId(),
                request.title(),
                request.content(),
                request.picPath(),
                request.ownName(),
                1,
                now,
                now
        );
        knowledgeMapper.insert(knowledge);
        return getKnowledge(knowledge.knowledgeId());
    }

    public Knowledge updateKnowledge(Integer knowledgeId, KnowledgeRequest request) {
        Knowledge existing = getKnowledge(knowledgeId);
        Knowledge knowledge = new Knowledge(
                knowledgeId,
                request.title(),
                request.content(),
                request.picPath(),
                request.ownName(),
                existing.status(),
                existing.createTime(),
                LocalDateTime.now()
        );
        knowledgeMapper.update(knowledge);
        return getKnowledge(knowledgeId);
    }

    /** 上下架知识内容：status 1=已发布 2=已下架，写入 tb_knowledge.status。 */
    public Knowledge updateKnowledgeStatus(Integer knowledgeId, Integer status) {
        getKnowledge(knowledgeId);
        knowledgeMapper.updateStatus(knowledgeId, status, LocalDateTime.now());
        return getKnowledge(knowledgeId);
    }

    @Transactional
    public void deleteKnowledge(Integer knowledgeId) {
        getKnowledge(knowledgeId);
        discussMapper.deleteByKnowledgeId(knowledgeId);
        knowledgeMapper.deleteByKnowledgeId(knowledgeId);
    }
}
