package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.UserEntity;

import java.time.LocalDateTime;

public interface UserSavePort {
    public UserEntity save(String firstName, String lastName, UserEntity.Gender gender, String phone, LocalDateTime now, byte[] pictureData);
    default void addToBulkSave(String firstName, String lastName, UserEntity.Gender gender, String phone, LocalDateTime createdAt) {
        addToBulkSave(firstName, lastName, gender, phone, createdAt, null);
    }
    public void addToBulkSave(String firstName, String lastName, UserEntity.Gender gender, String phone, LocalDateTime now, byte[] pictureData);
    public void executeBulkSave();
}
