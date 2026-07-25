package com.iment.app_mobile_tcc.alternatives.repository;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlternativeRepository extends JpaRepository<Alternative, Long> {
}
