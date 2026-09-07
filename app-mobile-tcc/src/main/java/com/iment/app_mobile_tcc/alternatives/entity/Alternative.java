package com.iment.app_mobile_tcc.alternatives.entity;

import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alternatives")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alternative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean correct;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private String correctSlot;
}
