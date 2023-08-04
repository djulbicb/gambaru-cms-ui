package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.ports.Response;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.time.LocalDateTime;

public interface UserSavePort {
    public ValidatorResponse verify(String firstName, String lastName, String gender, String phone);
    public UserEntity save(String firstName, String lastName, UserEntity.Gender gender, String phone, byte[] pictureData);
    default void addToBulkSave(String firstName, String lastName, UserEntity.Gender gender, String phone) {
        addToBulkSave(firstName, lastName, gender, phone, null);
    }
    public void addToBulkSave(String firstName, String lastName, UserEntity.Gender gender, String phone, byte[] pictureData);
    public void executeBulkSave();
}
