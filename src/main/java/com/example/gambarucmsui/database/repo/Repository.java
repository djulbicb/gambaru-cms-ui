package com.example.gambarucmsui.database.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

public class Repository<T> {
    protected final EntityManager entityManager;
    protected final Class<T> entityClass;

    public Repository(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        return entity;
    }

    public List<T> saveAll(List<T> entities) {
        entityManager.getTransaction().begin();
        for (T entity : entities) {
            entityManager.persist(entity);
        }
        entityManager.getTransaction().commit();
        return entities;
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

    public Optional<T> findById(Long id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    public List<T> findByIds(String fieldName, List<Long> ids) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        query.select(root).where(root.get(fieldName).in(ids));

        return entityManager.createQuery(query).getResultList();
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