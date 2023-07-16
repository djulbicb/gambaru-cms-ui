package com.example.gambarucmsui.adapter.out.persistence.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class Repository<T> {
    protected final EntityManager entityManager;
    protected final Class<T> entityClass;

    public Repository(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    public void update(T entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    public void delete(T entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }

    public T findById(Long id) {
        return entityManager.find(entityClass, id);
    }

    public List<T> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        query.select(root);

        return entityManager.createQuery(query).getResultList();
    }

    public List<T> findAll(int page, int pageSize, String sortColumn) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        query.select(root);

        int offset = (page - 1) * pageSize;

        return entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }
}