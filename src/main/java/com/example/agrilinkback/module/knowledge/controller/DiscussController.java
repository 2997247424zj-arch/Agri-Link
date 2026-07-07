package com.example.agrilinkback.module.knowledge.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.knowledge.dto.DiscussRequest;
import com.example.agrilinkback.module.knowledge.entity.Discuss;
import com.example.agrilinkback.module.knowledge.service.DiscussService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 农业知识讨论接口，按知识文章维护评论。
 */
@RestController
@RequestMapping("/api/knowledge/{knowledgeId}/discusses")
public class DiscussController {

    private final DiscussService discussService;

    public DiscussController(DiscussService discussService) {
        this.discussService = discussService;
    }

    @GetMapping
    public ApiResponse<List<Discuss>> listDiscussByKnowledge(@PathVariable Integer knowledgeId) {
        return ApiResponse.success(discussService.listDiscussByKnowledge(knowledgeId));
    }

    @PostMapping
    public ApiResponse<Discuss> createDiscuss(
            @PathVariable Integer knowledgeId,
            @Valid @RequestBody DiscussRequest request
    ) {
        return ApiResponse.success(discussService.createDiscuss(knowledgeId, request));
    }

    @DeleteMapping("/{discussId}")
    public ApiResponse<Void> deleteDiscuss(@PathVariable Integer knowledgeId, @PathVariable Integer discussId) {
        discussService.deleteDiscuss(knowledgeId, discussId);
        return ApiResponse.ok();
    }
}
