package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;

public class TeamRepository extends Repository<TeamEntity> {
    public TeamRepository(EntityManager entityManager) {
        super(entityManager, TeamEntity.class);
    }

    public boolean ifTeamNameExists(String teamName) {
        String jpql = "SELECT COUNT(t) FROM TeamEntity t WHERE t.name = :teamName";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("teamName", teamName);

        Long count = query.getSingleResult();
        return count > 0;
    }

    public List<TeamEntity> findAllActive() {
        String jpql = "SELECT t FROM TeamEntity t WHERE t.status = :status";
        TypedQuery<TeamEntity> query = entityManager.createQuery(jpql, TeamEntity.class);
        query.setParameter("status", TeamEntity.Status.ACTIVE);
        return query.getResultList();
    }

    public TeamEntity findByName(String teamName) {
        String jpql = "SELECT t FROM TeamEntity t WHERE t.name = :teamName";
        TypedQuery<TeamEntity> query = entityManager.createQuery(jpql, TeamEntity.class);
        query.setParameter("teamName", teamName);

        return query.getSingleResult();
    }

    public List<TeamEntity> findAllByUserId(Long userId) {
        String jpql = "SELECT b.team FROM BarcodeEntity b WHERE b.user.userId = :userId";
        TypedQuery<TeamEntity> query = entityManager.createQuery(jpql, TeamEntity.class);
        query.setParameter("userId", userId);

        return query.getResultList();
    }

    public TeamEntity saveNewTeam(String teamName, BigDecimal membershipPaymentFee) {
        TeamEntity en = new TeamEntity(teamName, TeamEntity.Status.ACTIVE, membershipPaymentFee);
        return save(en);
    }

    public TeamEntity updateOne(TeamEntity team) {
        return save(team);
    }
}
