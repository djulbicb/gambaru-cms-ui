package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.model.AttendanceCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UserAttendanceRepository extends Repository<UserAttendanceEntity> {
    public UserAttendanceRepository(EntityManager entityManager) {
        super(entityManager, UserAttendanceEntity.class);
    }

    public List<AttendanceCount> getAttendanceDataLast60Days() {
        LocalDateTime daysAgo = LocalDateTime.now().minusDays(60);
        LocalDateTime now = LocalDateTime.now();
        String queryString = """
        SELECT CAST(a.timestamp AS DATE), COUNT(a)
        FROM UserAttendanceEntity a
        WHERE a.timestamp BETWEEN :daysAgo AND :now
        GROUP BY CAST(a.timestamp AS DATE)
        ORDER BY CAST(a.timestamp AS DATE)
        """;
        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        query.setParameter("daysAgo", daysAgo);
        query.setParameter("now", now);

        return query.getResultList().stream()
                .map(row -> new AttendanceCount(LocalDate.parse(row[0]+""), (long) row[1]))
                .collect(Collectors.toList());
    }

    public List<UserAttendanceEntity> fetchLastNEntriesForUserAttendance(Long barcodeId, int count) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserAttendanceEntity> query = criteriaBuilder.createQuery(UserAttendanceEntity.class);
        Root<UserAttendanceEntity> root = query.from(UserAttendanceEntity.class);
        query.select(root)
                .where(criteriaBuilder.equal(root.get("barcode").get("barcodeId"), barcodeId))
                .orderBy(criteriaBuilder.desc(root.get("timestamp")));

        return entityManager.createQuery(query)
                .setMaxResults(count)
                .getResultList();
    }
}
