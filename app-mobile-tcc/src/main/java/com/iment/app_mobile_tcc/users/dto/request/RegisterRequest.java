package com.iment.app_mobile_tcc.users.dto.request;

import com.iment.app_mobile_tcc.users.enums.UserGrade;

public record RegisterRequest(String name, String email, String password) {
}
