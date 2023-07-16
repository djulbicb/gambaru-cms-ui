package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

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
}
