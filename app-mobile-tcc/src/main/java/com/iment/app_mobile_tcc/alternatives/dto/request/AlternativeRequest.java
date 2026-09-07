package com.iment.app_mobile_tcc.alternatives.dto.request;

public record AlternativeRequest (String description, boolean correct, Long questionId, String correctSlot){
}
