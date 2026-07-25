package com.iment.app_mobile_tcc.progress.repository;

import com.iment.app_mobile_tcc.progress.entity.AttemptAlternative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttemptAlternativeRepository extends JpaRepository<AttemptAlternative, Long> {
    @Query("""
            SELECT COUNT(DISTINCT(at.alternative.id))
            FROM AttemptAlternative AS at
            WHERE at.user.id = :userId
            AND at.question.id = :questionId
            AND at.correct = true
            """
    )
    Long countDistinctCorrectAlternativesByQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);
}
