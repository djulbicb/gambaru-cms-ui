package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.UserPictureEntity;
import jakarta.persistence.EntityManager;

public class UserPictureRepository extends Repository<UserPictureEntity> {
    public UserPictureRepository(EntityManager entityManager) {
        super(entityManager, UserPictureEntity.class);
    }
}
