package com.example.agrilinkback.module.consultation.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.consultation.dto.AnswerRequest;
import com.example.agrilinkback.module.consultation.dto.QuestionRequest;
import com.example.agrilinkback.module.consultation.entity.Question;
import com.example.agrilinkback.module.consultation.service.QuestionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consultation/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ApiResponse<List<Question>> listQuestions() {
        return ApiResponse.success(questionService.listQuestions());
    }

    @GetMapping("/{id}")
    public ApiResponse<Question> getQuestion(@PathVariable Integer id) {
        return ApiResponse.success(questionService.getQuestion(id));
    }

    @PostMapping
    public ApiResponse<Question> createQuestion(@Valid @RequestBody QuestionRequest request) {
        return ApiResponse.success(questionService.createQuestion(request));
    }

    @PatchMapping("/{id}/answer")
    public ApiResponse<Question> answerQuestion(
            @PathVariable Integer id,
            @Valid @RequestBody AnswerRequest request
    ) {
        return ApiResponse.success(questionService.answerQuestion(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        return ApiResponse.ok();
    }
}
