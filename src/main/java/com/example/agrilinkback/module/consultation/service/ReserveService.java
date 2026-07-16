package com.example.agrilinkback.module.consultation.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.consultation.dto.AnswerRequest;
import com.example.agrilinkback.module.consultation.dto.ReserveRequest;
import com.example.agrilinkback.module.consultation.dto.ReserveStatusRequest;
import com.example.agrilinkback.module.consultation.entity.Reserve;
import com.example.agrilinkback.module.consultation.mapper.ReserveMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReserveService {

    private final ReserveMapper reserveMapper;

    public ReserveService(ReserveMapper reserveMapper) {
        this.reserveMapper = reserveMapper;
    }

    public List<Reserve> listReserves() {
        return reserveMapper.findAll();
    }

    public Reserve getReserve(Integer id) {
        Reserve reserve = reserveMapper.findById(id);
        if (reserve == null) {
            throw new BusinessException(404, "Reserve not found");
        }
        return reserve;
    }

    public Reserve createReserve(ReserveRequest request) {
        Reserve reserve = new Reserve(
                reserveMapper.nextId(),
                request.expertName(),
                request.questioner(),
                request.area(),
                request.address(),
                request.plantName(),
                request.soilCondition(),
                request.plantCondition(),
                request.plantDetail(),
                request.phone(),
                request.message(),
                null,
                0,
                request.appointmentTime(),
                request.serviceMode()
        );
        reserveMapper.insert(reserve);
        return getReserve(reserve.id());
    }

    public Reserve answerReserve(Integer id, AnswerRequest request) {
        getReserve(id);
        // 未传 status 时默认“已处理”，与问答答复接口保持一致。
        int status = request.status() == null ? 1 : request.status();
        reserveMapper.updateAnswer(id, request.answer(), status);
        return getReserve(id);
    }

    public Reserve updateReserveStatus(Integer id, ReserveStatusRequest request) {
        getReserve(id);
        reserveMapper.updateStatus(id, request.status());
        return getReserve(id);
    }

    public void deleteReserve(Integer id) {
        getReserve(id);
        reserveMapper.deleteById(id);
    }
}
