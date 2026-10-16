package com.iment.app_mobile_tcc.topics.repository;

import com.iment.app_mobile_tcc.topics.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("""
        SELECT t FROM Topic t
        WHERE t.subject.id = :subjectId
        AND (t.owner IS NULL OR t.owner.id = :userId)
        AND t.draft = false
        AND t.catalog = false
        AND t.removedAt IS NULL
        """)
    List<Topic> findPlayableBySubject(@Param("subjectId") Long subjectId, @Param("userId") Long userId);

    @Query("""
        SELECT t FROM Topic t
        WHERE (t.owner IS NULL OR t.owner.id = :userId)
        AND t.draft = false
        AND t.catalog = false
        AND t.removedAt IS NULL
        """)
    List<Topic> findAllPlayable(@Param("userId") Long userId);

    List<Topic> findAllByOwnerIdAndCatalogFalseAndRemovedAtIsNull(Long ownerId);
}
