package com.iment.app_mobile_tcc.questions.dto.response;

import com.iment.app_mobile_tcc.alternatives.dto.response.AlternativeResponse;
import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import com.iment.app_mobile_tcc.topics.entity.Topic;
import tools.jackson.databind.JsonNode;

import java.util.List;

public record QuestionResponse(Long id, String title, Topic topic, Integer level, QuestionTypeEnum type, String content, List<AlternativeResponse> lstAlternative , boolean conclued) {
    public static QuestionResponse from(Question question, boolean conclued){
        return new QuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getTopic(),
                question.getLevel(),
                question.getType(),
                question.getContent(),
                question.getLstAlternative().stream().map(AlternativeResponse::from).toList(),
                conclued
        );
    }
}
