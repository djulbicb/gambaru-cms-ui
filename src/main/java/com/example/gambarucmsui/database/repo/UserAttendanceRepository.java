package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.UserAttendanceEntity;
import com.example.gambarucmsui.ui.dto.statistics.AttendanceCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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

    public List<UserAttendanceEntity> fetchLastNEntriesForUserAttendance(List<BarcodeEntity> barcodeIds, int count) {
        String jpql = "SELECT ua FROM UserAttendanceEntity ua " +
                "WHERE ua.barcode IN :barcodeIds " +
                "ORDER BY ua.timestamp DESC";

        return entityManager.createQuery(jpql, UserAttendanceEntity.class)
                .setParameter("barcodeIds", barcodeIds)
                .setMaxResults(count)
                .getResultList();
    }

    public List<UserAttendanceEntity> findAllForAttendanceDate(LocalDate forDate) {
        String fetchBarcodesFromAttendanceQuery = "SELECT ua FROM UserAttendanceEntity ua WHERE DATE(ua.timestamp) = :date ORDER BY ua.timestamp DESC";
        TypedQuery<UserAttendanceEntity> subquery = entityManager.createQuery(fetchBarcodesFromAttendanceQuery, UserAttendanceEntity.class);
        subquery.setParameter("date", forDate);

//        List<Object[]> barcodeEntitiesWithTimestamp = subquery.getResultList();
//        List<BarcodeWithAttendance> wrappers = barcodeEntitiesWithTimestamp.stream()
//                .map(arr -> new BarcodeWithAttendance((BarcodeEntity) arr[0], (UserAttendanceEntity) arr[1]))
//                .collect(Collectors.toList());

        return subquery.getResultList();
    }
}
