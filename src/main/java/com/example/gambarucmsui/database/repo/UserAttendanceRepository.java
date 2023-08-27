package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonAttendanceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class UserAttendanceRepository extends Repository<PersonAttendanceEntity> {
    public UserAttendanceRepository(EntityManager entityManager) {
        super(entityManager, PersonAttendanceEntity.class);
    }

    public List<PersonAttendanceEntity> fetchLastNEntriesForUserAttendance(List<BarcodeEntity> barcodeIds, int count) {
        String jpql = "SELECT ua FROM PersonAttendanceEntity ua " +
                "WHERE ua.barcode IN :barcodeIds " +
                "ORDER BY ua.timestamp DESC";

        return entityManager.createQuery(jpql, PersonAttendanceEntity.class)
                .setParameter("barcodeIds", barcodeIds)
                .setMaxResults(count)
                .getResultList();
    }

    public List<PersonAttendanceEntity> findAllForAttendanceDate(LocalDate forDate) {
        String fetchBarcodesFromAttendanceQuery = "SELECT ua FROM PersonAttendanceEntity ua WHERE DATE(ua.timestamp) = :date ORDER BY ua.timestamp DESC";
        TypedQuery<PersonAttendanceEntity> subquery = entityManager.createQuery(fetchBarcodesFromAttendanceQuery, PersonAttendanceEntity.class);
        subquery.setParameter("date", forDate);

//        List<Object[]> barcodeEntitiesWithTimestamp = subquery.getResultList();
//        List<BarcodeWithAttendance> wrappers = barcodeEntitiesWithTimestamp.stream()
//                .map(arr -> new BarcodeWithAttendance((BarcodeEntity) arr[0], (PersonAttendanceEntity) arr[1]))
//                .collect(Collectors.toList());

        return subquery.getResultList();
    }
}
