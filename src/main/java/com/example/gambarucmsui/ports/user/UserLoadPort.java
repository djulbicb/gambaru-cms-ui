package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.UserEntity;

import java.util.Optional;

public interface UserLoadPort {
    Optional<UserEntity> loadUserByUserId(Long userId);
}
