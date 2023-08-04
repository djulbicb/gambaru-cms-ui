package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.UserEntity;

import java.time.LocalDateTime;

public interface UserUpdatePort {
    public boolean update(Long userId, String firstName, String lastName, UserEntity.Gender gender, String phone, byte[] pictureData);
    default boolean update(Long userId, String firstName, String lastName, UserEntity.Gender gender, String phone) {
        return update(userId, firstName, lastName, gender, phone, null);
    }
}
