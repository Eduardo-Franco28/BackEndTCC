package com.iment.app_mobile_tcc.questions.dto.response;

import com.iment.app_mobile_tcc.questions.entity.Question;
import com.iment.app_mobile_tcc.topics.entity.Topic;

public record QuestionResponse(Long id, String title, Topic topic) {
    public static QuestionResponse from(Question question){
        return new QuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getTopic()
        );
    }
}
