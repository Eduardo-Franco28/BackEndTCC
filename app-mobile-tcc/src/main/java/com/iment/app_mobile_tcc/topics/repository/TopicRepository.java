package com.iment.app_mobile_tcc.topics.repository;

import com.iment.app_mobile_tcc.topics.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findAllBySubjectId(Long id);
}
