package com.iment.app_mobile_tcc.subjects.dto.response;

import com.iment.app_mobile_tcc.subjects.entity.Subject;

public record SubjectResponse (Long id, String name) {
    public static SubjectResponse from(Subject subject){
        return new SubjectResponse(
                subject.getId(),
                subject.getName()
        );
    }
}
