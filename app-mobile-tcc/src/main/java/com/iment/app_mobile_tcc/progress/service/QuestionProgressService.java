package com.iment.app_mobile_tcc.progress.service;

import com.iment.app_mobile_tcc.progress.repository.QuestionAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionProgressService {
    @Autowired
    private QuestionAttemptRepository questionAttemptRepository;

    public boolean isCompleted(Long userId, Long questionId){
        return this.questionAttemptRepository
                .existsByUserIdAndQuestionIdAndCorrectTrue(userId, questionId);
    }
}
