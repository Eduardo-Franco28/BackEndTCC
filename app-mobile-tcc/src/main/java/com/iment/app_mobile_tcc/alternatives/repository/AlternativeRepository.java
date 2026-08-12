package com.iment.app_mobile_tcc.alternatives.repository;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlternativeRepository extends JpaRepository<Alternative, Long> {
    Long countByQuestionIdAndCorrectTrue(Long questionId);

    List<Alternative> findAllByQuestionId(Long questionId);
}
