package com.iment.app_mobile_tcc.progress.service;

import com.iment.app_mobile_tcc.progress.entity.QuestionAttempt;
import com.iment.app_mobile_tcc.progress.repository.QuestionAttemptRepository;
import com.iment.app_mobile_tcc.questions.dto.response.QuestionResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.service.QuestionService;
import com.iment.app_mobile_tcc.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProgressService {
    @Autowired
    private QuestionAttemptRepository questionAttemptRepository;

    @Autowired
    private QuestionService questionService;

    public boolean isCompleted(Long userId, Long questionId){
        return this.questionAttemptRepository
                .existsByUserIdAndQuestionIdAndCorrectTrue(userId, questionId);
    }

    public Set<Long> getConcludedQuestionIds(Long userId, Long topicId){
        return this.questionAttemptRepository.findConcludedQuestionIds(userId, topicId);
    }

    public Set<Long> getAllConcludedQuestionIds(Long userId){
        return this.questionAttemptRepository.findAllConcludedQuestionIds(userId);
    }

    public List<QuestionResponse> findUnfinishedQuestions(User user, Integer limit){
        return this.questionAttemptRepository.findLastUnfinishedQuestions(user.getId(), Limit.of(limit))
                .stream().map(question -> QuestionResponse.from(question, false, null))
                .toList();
    }

    public int calcPercentConclued(Long userId, Long topicId){
        long total = this.questionService.countAllByTopicId(topicId);

        if (total == 0) return 0;

        long concluededs = this.getConcludedQuestionIds(userId, topicId).size();

        return (int) (concluededs * 100 / total);
    }
}
