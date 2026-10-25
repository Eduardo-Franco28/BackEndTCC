package com.iment.app_mobile_tcc.drafts.dto.response;

import com.iment.app_mobile_tcc.questions.enums.QuestionTypeEnum;
import com.iment.app_mobile_tcc.topics.entity.Topic;

public record DraftResponse(Long id, String title, String subTitle, String subject, boolean draft, boolean published, boolean copied, QuestionTypeEnum type) {
    public static DraftResponse from(Topic topic, boolean published, QuestionTypeEnum type){
        return new DraftResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getSubTitle(),
                topic.getSubject().getName(),
                topic.isDraft(),
                published,
                topic.getForkedFrom() != null,
                type
        );
    }
}
