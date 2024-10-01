package com.example.securitydemo.domain.global.enums;

public enum UserType {
    ADMIN, SOCIAL, EMAIL, USER;

    @Override
    public String toString() {
        return name();
    }
}
