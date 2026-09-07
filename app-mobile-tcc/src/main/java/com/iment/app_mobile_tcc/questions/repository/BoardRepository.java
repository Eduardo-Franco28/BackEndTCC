package com.iment.app_mobile_tcc.questions.repository;

import com.iment.app_mobile_tcc.questions.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
