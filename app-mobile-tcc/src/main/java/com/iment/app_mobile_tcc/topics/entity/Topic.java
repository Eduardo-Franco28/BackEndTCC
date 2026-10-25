package com.iment.app_mobile_tcc.topics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iment.app_mobile_tcc.subjects.entity.Subject;
import com.iment.app_mobile_tcc.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "topics")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subTitle;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean draft;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean catalog;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forked_from_id")
    private Topic forkedFrom;

    private Instant removedAt;
}
