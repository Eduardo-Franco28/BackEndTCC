package com.iment.app_mobile_tcc.alternatives.dto.response;

import com.iment.app_mobile_tcc.alternatives.entity.Alternative;
import com.iment.app_mobile_tcc.questions.entity.Question;

public record AlternativeResponse(Long id, String description) {
    public static AlternativeResponse from(Alternative alternative){
        return new AlternativeResponse(
                alternative.getId(),
                alternative.getDescription()
        );
    }
}