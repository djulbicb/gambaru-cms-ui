package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.PersonPictureEntity;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class PersonPictureRepository extends Repository<PersonPictureEntity> {
    public PersonPictureRepository(EntityManager entityManager) {
        super(entityManager, PersonPictureEntity.class);
    }

    public Optional<byte[]> findByPersonId(Long personId) {
        List<PersonPictureEntity> results = entityManager.createQuery(
                        "SELECT p FROM PersonPictureEntity p " +
                                "WHERE p.person.id = :personId", PersonPictureEntity.class)
                .setParameter("personId", personId)
                .setMaxResults(1)
                .getResultList();

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0).getPictureData());
    }

}
