package com.iment.app_mobile_tcc.boards.repository;

import com.iment.app_mobile_tcc.boards.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
