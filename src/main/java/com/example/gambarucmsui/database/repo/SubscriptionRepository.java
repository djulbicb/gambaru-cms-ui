package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.database.entity.SubscriptionEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubscriptionRepository extends Repository<SubscriptionEntity> {
    public SubscriptionRepository(EntityManager entityManager) {
        super(entityManager, SubscriptionEntity.class);
    }


    public Optional<SubscriptionEntity> findByBarcodeId(Long barcodeId) {
        String jpql = "SELECT s FROM SubscriptionEntity s WHERE s.barcode.barcodeId = :barcodeId";
        TypedQuery<SubscriptionEntity> query = entityManager.createQuery(jpql, SubscriptionEntity.class);
        query.setParameter("barcodeId",barcodeId);

        List<SubscriptionEntity> results = query.getResultList();

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

}
