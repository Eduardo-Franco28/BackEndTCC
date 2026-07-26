package com.iment.app_mobile_tcc.progress.repository;

import com.iment.app_mobile_tcc.progress.entity.QuestionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, Long> {
    boolean existsByUserIdAndQuestionIdAndCorrectTrue(Long userId, Long questionId);

    long countByUserIdAndQuestionIdAndCorrectFalse(Long userId, Long questionId);
}
