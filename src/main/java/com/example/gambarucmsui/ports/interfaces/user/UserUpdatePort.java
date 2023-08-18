package com.example.gambarucmsui.ports.interfaces.user;

import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.io.IOException;

public interface UserUpdatePort {
    public ValidatorResponse verify(String firstName, String lastName, String gender, String phone);
    public boolean update(Long userId, String firstName, String lastName, PersonEntity.Gender gender, String phone, byte[] pictureData) throws IOException;
    default boolean update(Long userId, String firstName, String lastName, PersonEntity.Gender gender, String phone) throws IOException {
        return update(userId, firstName, lastName, gender, phone, null);
    }
}
