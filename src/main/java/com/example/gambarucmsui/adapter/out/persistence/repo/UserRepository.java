package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository extends Repository<UserEntity> {
    public UserRepository(EntityManager entityManager) {
        super(entityManager, UserEntity.class);
    }
    public Optional<UserEntity> findUserByBarcodeId(Long barcodeId) {
        String queryString = "SELECT u FROM UserEntity u WHERE u.barcode.barcodeId = :barcodeId";
        return entityManager.createQuery(queryString, UserEntity.class)
                .setParameter("barcodeId", barcodeId)
                .getResultStream()
                .findFirst();
    }

    public List<UserEntity> findAllForDate(LocalDate forDate, String sortByColumnName) {
        String queryString = "SELECT u FROM UserEntity u WHERE DATE(u." + sortByColumnName + ") = :date ORDER BY u." + sortByColumnName;
        TypedQuery<UserEntity> query = entityManager.createQuery(queryString, UserEntity.class);
        query.setParameter("date", forDate);
        return query.getResultList();
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

    public List<UserEntity> findAll(int page, int pageSize, String sortColumn, String barcodeId, String firstName, String lastName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(entityClass);
        Root<UserEntity> root = query.from(entityClass);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>();

        if (firstName != null && !firstName.isBlank()) {
            predicates.add(criteriaBuilder.like(root.get("firstName"), firstName + "%"));
        }
        if (lastName != null && !lastName.isBlank()) {
            predicates.add(criteriaBuilder.like(root.get("lastName"), lastName + "%"));
        }
        if (barcodeId != null && !barcodeId.isBlank()) {
            predicates.add(criteriaBuilder.equal(root.get("barcode").get("barcodeId"), Long.parseLong(barcodeId)));
        }

        Predicate finalPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        query.where(finalPredicate);

        query.orderBy(criteriaBuilder.desc(root.get(sortColumn)));

        int offset = (page - 1) * pageSize;

        return entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

}
