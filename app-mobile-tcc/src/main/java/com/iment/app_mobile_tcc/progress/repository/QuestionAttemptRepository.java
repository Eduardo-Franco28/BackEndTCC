package com.iment.app_mobile_tcc.progress.repository;

import com.iment.app_mobile_tcc.progress.entity.QuestionAttempt;
import com.iment.app_mobile_tcc.questions.entity.Question;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, Long> {
    boolean existsByUserIdAndQuestionIdAndCorrectTrue(Long userId, Long questionId);

    @Query("""
        SELECT DISTINCT qa.question.id FROM QuestionAttempt qa
        WHERE qa.user.id = :userId
        AND qa.question.topic.id = :topicId
        AND qa.correct = true
        """)
    Set<Long> findConcludedQuestionIds(@Param("userId") Long userId, @Param("topicId") Long topicId);

    @Query("""
        SELECT DISTINCT qa.question.id FROM QuestionAttempt qa
        WHERE qa.user.id = :userId
        AND qa.correct = true
        """)
    Set<Long> findAllConcludedQuestionIds(@Param("userId") Long userId);

    @Query("""
        SELECT q FROM QuestionAttempt qa
        JOIN qa.question q
        WHERE qa.user.id = :userId
        AND q.id NOT IN (
            SELECT done.question.id FROM QuestionAttempt done
            WHERE done.user.id = :userId
            AND done.correct = true
        )
        GROUP BY q
        ORDER BY MAX(qa.answeredAt) DESC
    """)
    List<Question> findLastUnfinishedQuestions(@Param("userId") Long userId, Limit limit);

    @Query("""
        SELECT qa.answeredAt FROM QuestionAttempt qa
        WHERE qa.user.id = :userId
        ORDER BY qa.answeredAt DESC
    """)
    List<Instant> findAllAnsweredAtAttempts(@Param("userId") Long userId);
}
