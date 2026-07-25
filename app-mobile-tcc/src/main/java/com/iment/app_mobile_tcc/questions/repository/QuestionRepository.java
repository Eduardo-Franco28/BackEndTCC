package com.iment.app_mobile_tcc.questions.repository;

import com.iment.app_mobile_tcc.questions.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
