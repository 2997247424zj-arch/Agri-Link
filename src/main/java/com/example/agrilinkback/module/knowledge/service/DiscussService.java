package com.example.agrilinkback.module.knowledge.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.knowledge.dto.DiscussRequest;
import com.example.agrilinkback.module.knowledge.entity.Discuss;
import com.example.agrilinkback.module.knowledge.mapper.DiscussMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DiscussService {

    private final DiscussMapper discussMapper;
    private final KnowledgeService knowledgeService;

    public DiscussService(DiscussMapper discussMapper, KnowledgeService knowledgeService) {
        this.discussMapper = discussMapper;
        this.knowledgeService = knowledgeService;
    }

    public List<Discuss> listDiscussByKnowledge(Integer knowledgeId) {
        knowledgeService.getKnowledge(knowledgeId);
        return discussMapper.findByKnowledgeId(knowledgeId);
    }

    public Discuss createDiscuss(Integer knowledgeId, DiscussRequest request) {
        knowledgeService.getKnowledge(knowledgeId);
        Discuss discuss = new Discuss(
                discussMapper.nextId(),
                knowledgeId,
                request.ownName(),
                request.content(),
                LocalDateTime.now()
        );
        discussMapper.insert(discuss);
        return discussMapper.findByKnowledgeId(knowledgeId)
                .stream()
                .filter(item -> item.discussId().equals(discuss.discussId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(404, "Discuss not found"));
    }

    public void deleteDiscuss(Integer knowledgeId, Integer discussId) {
        knowledgeService.getKnowledge(knowledgeId);
        discussMapper.deleteByDiscussId(discussId);
    }
}
