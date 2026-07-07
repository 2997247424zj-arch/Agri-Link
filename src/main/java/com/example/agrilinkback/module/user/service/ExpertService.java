package com.example.agrilinkback.module.user.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.user.dto.ExpertRequest;
import com.example.agrilinkback.module.user.entity.Expert;
import com.example.agrilinkback.module.user.mapper.ExpertMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExpertService {

    private final ExpertMapper expertMapper;

    public ExpertService(ExpertMapper expertMapper) {
        this.expertMapper = expertMapper;
    }

    public List<Expert> listExperts() {
        return expertMapper.findAll();
    }

    public Expert getExpert(String userName) {
        Expert expert = expertMapper.findByUserName(userName);
        if (expert == null) {
            throw new BusinessException(404, "Expert not found");
        }
        return expert;
    }

    public Expert createExpert(ExpertRequest request) {
        if (expertMapper.findByUserName(request.userName()) != null) {
            throw new BusinessException(409, "Expert already exists");
        }
        Expert expert = toExpert(request);
        expertMapper.insert(expert);
        return getExpert(expert.userName());
    }

    public Expert updateExpert(String userName, ExpertRequest request) {
        getExpert(userName);
        Expert expert = new Expert(
                userName,
                request.realName(),
                request.phone(),
                request.profession(),
                request.position(),
                request.belong()
        );
        expertMapper.update(expert);
        return getExpert(userName);
    }

    public void deleteExpert(String userName) {
        getExpert(userName);
        expertMapper.deleteByUserName(userName);
    }

    private Expert toExpert(ExpertRequest request) {
        return new Expert(
                request.userName(),
                request.realName(),
                request.phone(),
                request.profession(),
                request.position(),
                request.belong()
        );
    }
}
