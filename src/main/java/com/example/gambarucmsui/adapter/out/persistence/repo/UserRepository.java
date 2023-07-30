package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository extends Repository<UserEntity> {
    public UserRepository(EntityManager entityManager) {
        super(entityManager, UserEntity.class);
    }
    public Optional<UserEntity> findUserByBarcodeId(Long barcodeId) {
        String queryString = "SELECT u FROM UserEntity u JOIN u.barcodes b WHERE b.barcodeId = :barcodeId";
        return entityManager.createQuery(queryString, UserEntity.class)
                .setParameter("barcodeId", barcodeId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<UserEntity> findAll(int page, int pageSize, String sortColumn) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(entityClass);
        Root<UserEntity> root = query.from(entityClass);
        query.select(root);

        query.orderBy(criteriaBuilder.desc(root.get(sortColumn)));

        int offset = (page - 1) * pageSize;

        return entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<UserEntity> findAll(int page, int pageSize, String sortColumn, String teamName, String firstName, String lastName) {
        String jpql = "SELECT u FROM UserEntity u JOIN u.barcodes b JOIN b.team t WHERE 1=1";

        if (firstName != null && !firstName.isBlank()) {
            jpql += " AND u.firstName LIKE :firstName";
        }
        if (lastName != null && !lastName.isBlank()) {
            jpql += " AND u.lastName LIKE :lastName";
        }
        if (teamName != null && !teamName.isBlank()) {
            jpql += " AND t.name LIKE :teamName";
        }
        jpql += " ORDER BY u." + sortColumn + " DESC";

        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);

        if (firstName != null && !firstName.isBlank()) {
            query.setParameter("firstName", firstName + "%");
        }

        if (lastName != null && !lastName.isBlank()) {
            query.setParameter("lastName", lastName + "%");
        }

        if (teamName != null && !teamName.isBlank()) {
            query.setParameter("teamName", teamName + "%");
        }

        int offset = (page - 1) * pageSize;
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }





    public UserEntity saveOne(String firstName, String lastName, UserEntity.Gender gender, String phone, LocalDateTime now) {
        UserEntity en = new UserEntity(firstName,lastName, gender, phone, now);
        return save(en);
    }

    public UserEntity updateOne(UserEntity user) {
        return save(user);
    }
}
