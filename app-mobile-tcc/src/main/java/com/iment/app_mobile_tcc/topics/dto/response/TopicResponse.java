package com.iment.app_mobile_tcc.topics.dto.response;

import com.iment.app_mobile_tcc.subjects.entity.Subject;
import com.iment.app_mobile_tcc.topics.enums.TopicStatusEnum;
import com.iment.app_mobile_tcc.topics.entity.Topic;

public record TopicResponse(Long id, String title, String subTitle, Subject subject, Integer percentConclued, TopicStatusEnum status) {
    public static TopicResponse from(Topic topic){
        return new TopicResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getSubTitle(),
                topic.getSubject(),
                0,
                TopicStatusEnum.NAO_INICIADO
        );
    }

    public static TopicResponse from(Topic topic, Integer percentConclued, TopicStatusEnum status){
        return new TopicResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getSubTitle(),
                topic.getSubject(),
                percentConclued,
                status
        );
    }
}
