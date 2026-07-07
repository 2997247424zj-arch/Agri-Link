package com.example.agrilinkback.module.consultation.controller;

import com.example.agrilinkback.common.api.ApiResponse;
import com.example.agrilinkback.module.consultation.dto.AnswerRequest;
import com.example.agrilinkback.module.consultation.dto.ReserveRequest;
import com.example.agrilinkback.module.consultation.entity.Reserve;
import com.example.agrilinkback.module.consultation.service.ReserveService;
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

/**
 * 专家预约接口，记录农户预约需求和专家处理结果。
 */
@RestController
@RequestMapping("/api/consultation/reserves")
public class ReserveController {

    private final ReserveService reserveService;

    public ReserveController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @GetMapping
    public ApiResponse<List<Reserve>> listReserves() {
        return ApiResponse.success(reserveService.listReserves());
    }

    @GetMapping("/{id}")
    public ApiResponse<Reserve> getReserve(@PathVariable Integer id) {
        return ApiResponse.success(reserveService.getReserve(id));
    }

    @PostMapping
    public ApiResponse<Reserve> createReserve(@Valid @RequestBody ReserveRequest request) {
        return ApiResponse.success(reserveService.createReserve(request));
    }

    @PatchMapping("/{id}/answer")
    public ApiResponse<Reserve> answerReserve(
            @PathVariable Integer id,
            @Valid @RequestBody AnswerRequest request
    ) {
        return ApiResponse.success(reserveService.answerReserve(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReserve(@PathVariable Integer id) {
        reserveService.deleteReserve(id);
        return ApiResponse.ok();
    }
}
