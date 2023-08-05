package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.PersonPictureEntity;
import jakarta.persistence.EntityManager;

public class PersonPictureRepository extends Repository<PersonPictureEntity> {
    public PersonPictureRepository(EntityManager entityManager) {
        super(entityManager, PersonPictureEntity.class);
    }
}
