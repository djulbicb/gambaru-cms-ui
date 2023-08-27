package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.TeamEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TeamRepository extends Repository<TeamEntity> {
    public TeamRepository(EntityManager entityManager) {
        super(entityManager, TeamEntity.class);
    }

    public boolean ifTeamNameExists(String teamName) {
        String jpql = "SELECT COUNT(t) FROM TeamEntity t WHERE t.name = :teamName AND t.status = :status";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("teamName", teamName);
        query.setParameter("status", TeamEntity.Status.ACTIVE);

        Long count = query.getSingleResult();
        return count == 1;
    }

    public List<TeamEntity> findAllActive() {
        String jpql = "SELECT t FROM TeamEntity t WHERE t.status = :status";
        TypedQuery<TeamEntity> query = entityManager.createQuery(jpql, TeamEntity.class);
        query.setParameter("status", TeamEntity.Status.ACTIVE);
        return query.getResultList();
    }

    public TeamEntity findByName(String teamName) {
        String jpql = "SELECT t FROM TeamEntity t WHERE t.name = :teamName AND t.status = :status";
        TypedQuery<TeamEntity> query = entityManager.createQuery(jpql, TeamEntity.class);
        query.setParameter("teamName", teamName);
        query.setParameter("status", TeamEntity.Status.ACTIVE);

        return query.getSingleResult();
    }

    public List<TeamEntity> findAllByUserId(Long personId) {
        String jpql = "SELECT b.team FROM BarcodeEntity b WHERE b.person.personId = :userId";
        TypedQuery<TeamEntity> query = entityManager.createQuery(jpql, TeamEntity.class);
        query.setParameter("personId", personId);

        return query.getResultList();
    }

    public TeamEntity updateOne(TeamEntity team) {
        return save(team);
    }
}
