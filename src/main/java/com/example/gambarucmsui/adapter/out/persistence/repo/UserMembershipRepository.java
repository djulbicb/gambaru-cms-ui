package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.model.AttendanceCount;
import com.example.gambarucmsui.model.MembershipCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMembershipRepository extends Repository<UserMembershipPaymentEntity> {
    public UserMembershipRepository(EntityManager entityManager) {
        super(entityManager, UserMembershipPaymentEntity.class);
    }

    public List<MembershipCount> getMembershipCountByMonthLastYear() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(1);

        String queryString = """
        SELECT m.year, m.month, COUNT(m)
        FROM UserMembershipPaymentEntity m 
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

    public List<UserMembershipPaymentEntity> fetchLastNEntriesForUserAttendance(Long barcodeId, int count) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserMembershipPaymentEntity> query = criteriaBuilder.createQuery(UserMembershipPaymentEntity.class);
        Root<UserMembershipPaymentEntity> root = query.from(UserMembershipPaymentEntity.class);
        query.select(root)
                .where(criteriaBuilder.equal(root.get("barcode").get("barcodeId"), barcodeId))
                .orderBy(criteriaBuilder.desc(root.get("timestamp")));

        return entityManager.createQuery(query)
                .setMaxResults(count)
                .getResultList();
    }
}
