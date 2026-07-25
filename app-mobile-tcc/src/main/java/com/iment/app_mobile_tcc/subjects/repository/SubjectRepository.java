package com.iment.app_mobile_tcc.subjects.repository;

import com.iment.app_mobile_tcc.subjects.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
