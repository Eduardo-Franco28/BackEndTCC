package com.iment.app_mobile_tcc.progress.service;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.progress.dto.response.AnsweredAlternativeResponse;
import com.iment.app_mobile_tcc.progress.entity.AttemptAlternative;
import com.iment.app_mobile_tcc.progress.repository.AttemptAlternativeRepository;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AttemptAlternativeService {
    @Autowired
    private AttemptAlternativeRepository attemptAlternativeRepository;

    @Autowired
    private AlternativeService alternativeService;

    @Autowired
    private QuestionProgressService questionProgressService;

    public AnsweredAlternativeResponse save(User user, Long alternativeId){
        Alternative alternative = this.alternativeService.getAlternative(alternativeId);

        boolean correct = alternative.isCorrect();

        AttemptAlternative newAttemptAlternative = new AttemptAlternative(
                null,
                user,
                alternative.getQuestion(),
                alternative,
                correct,
                Instant.now()
        );

        this.attemptAlternativeRepository.save(newAttemptAlternative);

        boolean questionConcluded = this.questionProgressService
                .isCompleted(user.getId(), alternative.getQuestion().getId());

        return new AnsweredAlternativeResponse(correct, questionConcluded);
    }
}
