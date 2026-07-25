package com.iment.app_mobile_tcc.attempt.dto.request;

import com.iment.app_mobile_tcc.users.entity.User;

public record AnsweredAlternativeRequest(User user, Long alternativeId){
}
