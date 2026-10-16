package com.iment.app_mobile_tcc.drafts.dto.response;

import com.iment.app_mobile_tcc.topics.entity.Topic;

public record DraftResponse(Long id, String title, String subTitle, String subject, boolean draft) {
    public static DraftResponse from(Topic topic){
        return new DraftResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getSubTitle(),
                topic.getSubject().getName(),
                topic.isDraft()
        );
    }
}
