package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class PersonRepository extends Repository<PersonEntity> {
    public PersonRepository(EntityManager entityManager) {
        super(entityManager, PersonEntity.class);
    }
    public Optional<PersonEntity> findUserByBarcodeId(Long barcodeId) {
        String queryString = "SELECT u FROM PersonEntity u JOIN u.barcodes b WHERE b.barcodeId = :barcodeId";
        return entityManager.createQuery(queryString, PersonEntity.class)
                .setParameter("barcodeId", barcodeId)
                .getResultStream()
                .findFirst();
    }

    public List<PersonEntity> findAll(int page, int pageSize, String sortColumn, String teamName, String firstName, String lastName, String barcode, boolean isOnlyActive) {
        // Distinct is added because of following reason
        // Person can have multiple barcodes. When joining these two tables means user would appear multiple times
        String jpql = "SELECT DISTINCT u FROM PersonEntity u LEFT JOIN u.barcodes b LEFT JOIN b.team t WHERE 1=1";

        if (firstName != null && !firstName.isBlank()) {
            jpql += " AND u.firstName LIKE :firstName";
        }
        if (lastName != null && !lastName.isBlank()) {
            jpql += " AND u.lastName LIKE :lastName";
        }
        if (teamName != null && !teamName.isBlank()) {
            jpql += " AND t.name LIKE :teamName";
        }
        if (barcode != null && !barcode.isBlank()) {
            jpql += " AND b.barcodeId = :barcode";
        }
        if (isOnlyActive) {
            jpql += " AND b.status = :status";
        }
        jpql += " ORDER BY u." + sortColumn + " DESC";
        int offset = (page - 1) * pageSize;
        jpql = addLimitOffsetToJpql(jpql, pageSize, offset);

        TypedQuery<PersonEntity> query = entityManager.createQuery(jpql, PersonEntity.class);

        if (firstName != null && !firstName.isBlank()) {
            query.setParameter("firstName", firstName + "%");
        }

        if (lastName != null && !lastName.isBlank()) {
            query.setParameter("lastName", lastName + "%");
        }

        if (teamName != null && !teamName.isBlank()) {
            query.setParameter("teamName", teamName + "%");
        }

        if (barcode != null && !barcode.isBlank()) {
            query.setParameter("barcode", barcode);
        }

        if (isOnlyActive) {
            query.setParameter("status", BarcodeEntity.Status.ASSIGNED);
        }

        return query.getResultList();
    }

    private String addLimitOffsetToJpql(String jpql, int pageSize, int offset) {
        return jpql + " LIMIT " + pageSize + " OFFSET " + offset;
    }


    public boolean isUserAlreadyInThisTeam(Long personId, Long teamId) {
        String jpql = "SELECT COUNT(b) FROM BarcodeEntity b WHERE b.person.personId = :personId AND b.team.teamId = :teamId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("personId", personId);
        query.setParameter("teamId", teamId);
        Long count = query.getSingleResult();
        return count > 0;
    }

    public boolean isUserAlreadyInThisTeam(Long personId, String teamName) {
        String jpql = "SELECT COUNT(b) FROM BarcodeEntity b WHERE b.person.personId = :personId AND b.team.name = :teamName";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("personId", personId);
        query.setParameter("teamName", teamName);
        Long count = query.getSingleResult();
        return count > 0;
    }

    public List<BarcodeEntity> findAllActiveUsersInTeam(Long teamId) {
        String jpql = "SELECT b FROM BarcodeEntity b WHERE b.team.teamId = :teamId AND b.status = :status";
        TypedQuery<BarcodeEntity> query = entityManager.createQuery(jpql, BarcodeEntity.class);
        query.setParameter("teamId", teamId);
        query.setParameter("status", BarcodeEntity.Status.ASSIGNED);
        return query.getResultList();
    }
}
