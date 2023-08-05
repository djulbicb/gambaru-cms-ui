package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.PersonPictureEntity;
import jakarta.persistence.EntityManager;

public class UserPictureRepository extends Repository<PersonPictureEntity> {
    public UserPictureRepository(EntityManager entityManager) {
        super(entityManager, PersonPictureEntity.class);
    }
}
