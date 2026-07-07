package com.example.agrilinkback.module.knowledge.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.knowledge.dto.KnowledgeRequest;
import com.example.agrilinkback.module.knowledge.entity.Knowledge;
import com.example.agrilinkback.module.knowledge.service.KnowledgeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping
    public ApiResponse<List<Knowledge>> listKnowledge() {
        return ApiResponse.success(knowledgeService.listKnowledge());
    }

    @GetMapping("/{knowledgeId}")
    public ApiResponse<Knowledge> getKnowledge(@PathVariable Integer knowledgeId) {
        return ApiResponse.success(knowledgeService.getKnowledge(knowledgeId));
    }

    @PostMapping
    public ApiResponse<Knowledge> createKnowledge(@Valid @RequestBody KnowledgeRequest request) {
        return ApiResponse.success(knowledgeService.createKnowledge(request));
    }

    @PutMapping("/{knowledgeId}")
    public ApiResponse<Knowledge> updateKnowledge(
            @PathVariable Integer knowledgeId,
            @Valid @RequestBody KnowledgeRequest request
    ) {
        return ApiResponse.success(knowledgeService.updateKnowledge(knowledgeId, request));
    }

    @DeleteMapping("/{knowledgeId}")
    public ApiResponse<Void> deleteKnowledge(@PathVariable Integer knowledgeId) {
        knowledgeService.deleteKnowledge(knowledgeId);
        return ApiResponse.ok();
    }
}
