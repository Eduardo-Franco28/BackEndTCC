package com.iment.app_mobile_tcc.users.enums;

public enum UserEnum {
    ADMIN("admin"),
    USER("user");

    private String role;

    UserEnum(String role){
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
