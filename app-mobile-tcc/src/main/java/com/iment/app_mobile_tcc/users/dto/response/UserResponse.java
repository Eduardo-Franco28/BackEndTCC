package com.iment.app_mobile_tcc.users.dto.response;

import com.iment.app_mobile_tcc.users.entity.User;

public record UserResponse (Long id, String nome, String email){

    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getNome(),
                user.getEmail()
        );
    }
}
