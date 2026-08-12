package com.iment.app_mobile_tcc.progress.repository;

import com.iment.app_mobile_tcc.progress.entity.QuestionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, Long> {
    boolean existsByUserIdAndQuestionIdAndCorrectTrue(Long userId, Long questionId);

    long countByUserIdAndQuestionIdAndCorrectFalse(Long userId, Long questionId);

    @Query("""
        SELECT DISTINCT qa.question.id FROM QuestionAttempt qa
        WHERE qa.user.id = :userId
        AND qa.question.topic.id = :topicId
        AND qa.correct = true
        """)
    Set<Long> findConcludedQuestionIds(@Param("userId") Long userId, @Param("topicId") Long topicId);
}
