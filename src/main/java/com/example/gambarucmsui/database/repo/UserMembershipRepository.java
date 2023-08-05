package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonMembershipPaymentEntity;
import com.example.gambarucmsui.ui.dto.statistics.MembershipCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class UserMembershipRepository extends Repository<PersonMembershipPaymentEntity> {
    public UserMembershipRepository(EntityManager entityManager) {
        super(entityManager, PersonMembershipPaymentEntity.class);
    }

    public List<MembershipCount> getMembershipCountByMonthLastYear() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(1);

        String queryString = """
        SELECT m.year, m.month, COUNT(m)
        FROM PersonMembershipPaymentEntity m 
        WHERE   m.year >= :startYear AND m.year <= :endYear
        GROUP BY m.year, m.month
        ORDER BY m.year, m.month
        """;
        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        query.setParameter("startYear", startDate.getYear());
        query.setParameter("endYear", endDate.getYear());

        List<Object[]> results = query.getResultList();

        List<MembershipCount> membershipCounts = results.stream()
                .map(row -> {
                    int resultYear = (int) row[0];
                    int resultMonth = (int) row[1];
                    long count = (long) row[2];
                    return new MembershipCount(LocalDate.of(resultYear, resultMonth, 1), count);
                })
                .collect(Collectors.toList());

        return membershipCounts;
    }

    public List<PersonMembershipPaymentEntity> fetchLastNEntriesForUserMembership(List<BarcodeEntity> barcodeIds, int count) {
        String jpql = "SELECT um FROM PersonMembershipPaymentEntity um " +
                "WHERE um.barcode IN :barcodeIds " +
                "ORDER BY um.timestamp DESC";

        return entityManager.createQuery(jpql, PersonMembershipPaymentEntity.class)
                .setParameter("barcodeIds", barcodeIds)
                .setMaxResults(count)
                .getResultList();
    }


    public List<PersonMembershipPaymentEntity> findAllMembershipsForMonthAndYear(int month, int year) {
        String fetchBarcodesFromMembershipQuery = "SELECT um FROM PersonMembershipPaymentEntity um WHERE um.year = :year AND um.month = :month ORDER BY um.timestamp DESC";
        TypedQuery<PersonMembershipPaymentEntity> query = entityManager.createQuery(fetchBarcodesFromMembershipQuery, PersonMembershipPaymentEntity.class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        return query.getResultList();
    }

    public boolean isMembershipPayedByBarcodeAndMonthAndYear(Long barcodeId, int month, int year) {
        String fetchMembershipsQuery = "SELECT um FROM PersonMembershipPaymentEntity um WHERE um.barcode.barcodeId = :barcodeId AND um.year = :year AND um.month = :month";
        TypedQuery<PersonMembershipPaymentEntity> query = entityManager.createQuery(fetchMembershipsQuery, PersonMembershipPaymentEntity.class);
        query.setParameter("barcodeId", barcodeId);
        query.setParameter("year", year);
        query.setParameter("month", month);
        List<PersonMembershipPaymentEntity> memberships = query.getResultList();
        return !memberships.isEmpty();
    }
}
