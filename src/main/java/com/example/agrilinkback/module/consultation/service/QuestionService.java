package com.example.agrilinkback.module.consultation.service;

import com.example.agrilinkback.common.exception.BusinessException;
import com.example.agrilinkback.module.consultation.dto.AnswerRequest;
import com.example.agrilinkback.module.consultation.dto.QuestionRequest;
import com.example.agrilinkback.module.consultation.entity.Question;
import com.example.agrilinkback.module.consultation.mapper.QuestionMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionMapper questionMapper;

    public QuestionService(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    public List<Question> listQuestions() {
        return questionMapper.findAll();
    }

    public Question getQuestion(Integer id) {
        Question question = questionMapper.findById(id);
        if (question == null) {
            throw new BusinessException(404, "Question not found");
        }
        return question;
    }

    public Question createQuestion(QuestionRequest request) {
        Question question = new Question(
                questionMapper.nextId(),
                request.expertName(),
                request.questioner(),
                request.phone(),
                request.plantName(),
                request.title(),
                request.question(),
                null,
                0,
                joinLines(request.attachments())
        );
        questionMapper.insert(question);
        return getQuestion(question.id());
    }

    public Question answerQuestion(Integer id, AnswerRequest request) {
        getQuestion(id);
        questionMapper.updateAnswer(id, request.answer(), request.status());
        return getQuestion(id);
    }

    public void deleteQuestion(Integer id) {
        getQuestion(id);
        questionMapper.deleteById(id);
    }

    private String joinLines(List<String> values) {
        if (values == null) {
            return null;
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }
}
