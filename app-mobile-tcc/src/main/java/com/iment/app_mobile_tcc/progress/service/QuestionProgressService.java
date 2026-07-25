package com.iment.app_mobile_tcc.progress.service;

import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.progress.repository.AttemptAlternativeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class QuestionProgressService {
    @Autowired
    private AttemptAlternativeRepository attemptAlternativeRepository;

    @Autowired
    private AlternativeService alternativeService;

    public boolean isCompleted(Long userId, Long questionId){
        Long total = this.alternativeService.countAlternativesByQuestion(questionId);
        Long totalConclued = this.attemptAlternativeRepository.countDistinctCorrectAlternativesByQuestionId(userId, questionId);

        return (Objects.equals(total, totalConclued));
    }
}
