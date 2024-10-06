package com.example.server.domain.global.enums;

public enum UserType {
    ADMIN, SOCIAL, EMAIL, USER;

    @Override
    public String toString() {
        return name();
    }
}
