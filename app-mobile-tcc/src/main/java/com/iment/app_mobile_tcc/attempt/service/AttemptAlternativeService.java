package com.iment.app_mobile_tcc.attempt.service;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.attempt.dto.request.AnsweredAlternativeRequest;
import com.iment.app_mobile_tcc.attempt.dto.response.AnsweredAlternativeResponse;
import com.iment.app_mobile_tcc.attempt.repository.AttemptAlternativeRepository;
import com.iment.app_mobile_tcc.questions.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttemptAlternativeService {
    @Autowired
    private AttemptAlternativeRepository attemptAlternativeRepository;

    @Autowired
    private AlternativeService alternativeService;

    @Autowired
    private QuestionService questionService;

    public AnsweredAlternativeResponse save(AnsweredAlternativeRequest obj){
        Alternative alternative = this.alternativeService.getAlternative(obj.alternativeId());

        boolean correct = alternative.isCorrect();

        
    }
}
