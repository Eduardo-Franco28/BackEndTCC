package com.iment.app_mobile_tcc.questions.repository;

import com.iment.app_mobile_tcc.questions.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("""
        SELECT DISTINCT q FROM Question q
        LEFT JOIN FETCH q.lstAlternative
        WHERE q.topic.id = :topicId
        ORDER BY q.level
        """)
    List<Question> findAllByTopicIdWithAlternatives(@Param("topicId") Long topicId);
}
