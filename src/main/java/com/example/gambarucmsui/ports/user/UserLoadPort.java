package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserLoadPort {
    Optional<UserEntity> loadUserByUserId(Long userId);
    List<UserEntity> findAll(int page, int pageSize, String sortColumn, String teamName, String firstName, String lastName, String barcode, boolean isOnlyActive);
}
