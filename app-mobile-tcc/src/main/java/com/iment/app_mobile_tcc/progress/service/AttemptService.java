package com.iment.app_mobile_tcc.progress.service;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.alternatives.service.AlternativeService;
import com.iment.app_mobile_tcc.progress.dto.request.AttemptAlternativeRequest;
import com.iment.app_mobile_tcc.progress.dto.response.AnsweredAlternativeResponse;
import com.iment.app_mobile_tcc.progress.entity.QuestionAttempt;
import com.iment.app_mobile_tcc.progress.repository.QuestionAttemptRepository;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AttemptService {
    @Autowired
    private QuestionAttemptRepository questionAttemptRepository;

    @Autowired
    private AlternativeService alternativeService;

    @Autowired
    private QuestionProgressService questionProgressService;

    public AnsweredAlternativeResponse save(User user, AttemptAlternativeRequest obj){
        List<Alternative> lstAlternative = this.alternativeService.getAlternatives(obj.alternativesId());

        Question question = lstAlternative.get(0).getQuestion();

        int allCorrects = 0;
        boolean missQuestion = false;

        for (Alternative alternative : lstAlternative){
            if(alternative.isCorrect())
                allCorrects++;
            else
                missQuestion = true;
        }

        Long totalCorrects = this.alternativeService.countCorrectAlternativesByQuestion(obj.questionId());

        boolean correctQuestion = !missQuestion && allCorrects == totalCorrects;

        this.questionAttemptRepository.save(new QuestionAttempt(
                null,
                user,
                question,
                correctQuestion,
                Instant.now()
        ));

        boolean questionConcluded = correctQuestion
                || this.questionProgressService.isCompleted(user.getId(), obj.questionId());

        return new AnsweredAlternativeResponse(correctQuestion, questionConcluded);
    }
}
