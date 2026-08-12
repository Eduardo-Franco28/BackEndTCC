package com.iment.app_mobile_tcc.users.dto.request;

public record ChangeUserRequest(String nome, String email, String currentPassword, String newPassword) {
}
