package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BarcodeRepository extends Repository<BarcodeEntity> {
    public BarcodeRepository(EntityManager entityManager) {
        super(entityManager, BarcodeEntity.class);
    }

    public BarcodeEntity fetchOneOrGenerate(BarcodeEntity.Status status) {
        return fetchOrGenerateBarcodes(1, BarcodeEntity.Status.NOT_USED).get(0);
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

    public List<UserMembershipPaymentEntity> findAllMembershipsForMonthAndYear(int month, int year) {
        String fetchBarcodesFromMembershipQuery = "SELECT um FROM UserMembershipPaymentEntity um WHERE um.year = :year AND um.month = :month ORDER BY um.timestamp DESC";
        TypedQuery<UserMembershipPaymentEntity> query = entityManager.createQuery(fetchBarcodesFromMembershipQuery, UserMembershipPaymentEntity.class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        return query.getResultList();
    }

    public boolean isMembershipAlreadyPayedByBarcodeAndMonthAndYear(Long barcodeId, int month, int year) {
        String fetchMembershipsQuery = "SELECT um FROM UserMembershipPaymentEntity um WHERE um.barcode.barcodeId = :barcodeId AND um.year = :year AND um.month = :month";
        TypedQuery<UserMembershipPaymentEntity> query = entityManager.createQuery(fetchMembershipsQuery, UserMembershipPaymentEntity.class);
        query.setParameter("barcodeId", barcodeId);
        query.setParameter("year", year);
        query.setParameter("month", month);
        List<UserMembershipPaymentEntity> memberships = query.getResultList();
        return !memberships.isEmpty();
    }

    public List<BarcodeEntity> fetchOrGenerateBarcodes(int count, BarcodeEntity.Status status) {
        List<BarcodeEntity> barcodes = findBarcodesByStatus(count, status);

        int availableCount = barcodes.size();

        if (availableCount == count) {
            return barcodes;
        }

        int generateCount = count - availableCount;
       return generateNewBarcodes(generateCount);
    }

    public List<BarcodeEntity> generateNewBarcodes(int generateCount) {
        List<BarcodeEntity> toBeSaved = new ArrayList<>();
        for (int i = 0; i < generateCount; i++) {
            BarcodeEntity barcode = new BarcodeEntity();
            barcode.setStatus( BarcodeEntity.Status.NOT_USED);
            BarcodeEntity save = save(barcode);
            toBeSaved.add(save);
        }
        return saveAll(toBeSaved);
    }

    public List<BarcodeEntity> findBarcodesByStatus(int count, BarcodeEntity.Status status) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BarcodeEntity> query = criteriaBuilder.createQuery(BarcodeEntity.class);
        Root<BarcodeEntity> root = query.from(BarcodeEntity.class);

        query.select(root)
                .where(criteriaBuilder.equal(root.get("status"), status))
                .orderBy(criteriaBuilder.asc(root.get("barcodeId")));

        TypedQuery<BarcodeEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(count);

        return typedQuery.getResultList();
    }

    public void updateOne(BarcodeEntity barcode) {
        save(barcode);
    }

    public void updateBarcodeWithUserAndTeam(BarcodeEntity barcode, UserEntity user, TeamEntity team) {
        barcode.setStatus(BarcodeEntity.Status.ASSIGNED);
        barcode.setTeam(team);
        barcode.setUser(user);

        save(barcode);
    }
}
