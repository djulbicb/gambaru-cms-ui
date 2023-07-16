package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class UserRepository extends Repository<UserEntity> {
    public UserRepository(EntityManager entityManager) {
        super(entityManager, UserEntity.class);
    }

    @Override
    public List<UserEntity> findAll(int page, int pageSize) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = criteriaBuilder.createQuery(entityClass);
        Root<UserEntity> root = query.from(entityClass);
        query.select(root);

        query.orderBy(criteriaBuilder.desc(root.get("lastAttendanceTimestamp")));

        int offset = (page - 1) * pageSize;

        return entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }
}
