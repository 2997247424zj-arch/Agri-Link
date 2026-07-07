package com.example.agrilinkback.module.user.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.user.dto.ExpertRequest;
import com.example.agrilinkback.module.user.entity.Expert;
import com.example.agrilinkback.module.user.service.ExpertService;
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
@RequestMapping("/api/experts")
public class ExpertController {

    private final ExpertService expertService;

    public ExpertController(ExpertService expertService) {
        this.expertService = expertService;
    }

    @GetMapping
    public ApiResponse<List<Expert>> listExperts() {
        return ApiResponse.success(expertService.listExperts());
    }

    @GetMapping("/{userName}")
    public ApiResponse<Expert> getExpert(@PathVariable String userName) {
        return ApiResponse.success(expertService.getExpert(userName));
    }

    @PostMapping
    public ApiResponse<Expert> createExpert(@Valid @RequestBody ExpertRequest request) {
        return ApiResponse.success(expertService.createExpert(request));
    }

    @PutMapping("/{userName}")
    public ApiResponse<Expert> updateExpert(@PathVariable String userName, @Valid @RequestBody ExpertRequest request) {
        return ApiResponse.success(expertService.updateExpert(userName, request));
    }

    @DeleteMapping("/{userName}")
    public ApiResponse<Void> deleteExpert(@PathVariable String userName) {
        expertService.deleteExpert(userName);
        return ApiResponse.ok();
    }
}
