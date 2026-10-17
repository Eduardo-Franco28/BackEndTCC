package com.iment.app_mobile_tcc.catalog.dto.response;

import com.iment.app_mobile_tcc.topics.entity.Topic;

public record CatalogResponse(Long id, String title, String subTitle, String subject, String author, long copies, boolean mine, boolean added) {
    public static CatalogResponse from(Topic topic, long copies, boolean mine, boolean added){
        return new CatalogResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getSubTitle(),
                topic.getSubject().getName(),
                topic.getOwner().getNome().trim().split("\\s+")[0],
                copies,
                mine,
                added
        );
    }
}
