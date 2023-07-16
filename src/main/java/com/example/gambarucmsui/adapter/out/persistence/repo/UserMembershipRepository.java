package com.example.gambarucmsui.adapter.out.persistence.repo;

import com.example.gambarucmsui.adapter.out.persistence.entity.UserAttendanceEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserMembershipPaymentEntity;
import com.example.gambarucmsui.model.AttendanceCount;
import com.example.gambarucmsui.model.MembershipCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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
        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        LocalDateTime endDate = LocalDateTime.now();

        String queryString = """
            SELECT FUNCTION('YEAR', a.timestamp), FUNCTION('MONTH', a.timestamp), COUNT(a)
            FROM UserMembershipPaymentEntity a
            WHERE a.timestamp >= :startDate AND a.timestamp <= :endDate
            GROUP BY FUNCTION('YEAR', a.timestamp), FUNCTION('MONTH', a.timestamp)
            ORDER BY FUNCTION('YEAR', a.timestamp), FUNCTION('MONTH', a.timestamp)
        """;
        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<Object[]> results = query.getResultList();

        List<MembershipCount> membershipCounts = new ArrayList<>();
        for (Object[] row : results) {
            int year = (int) row[0];
            int month = (int) row[1];
            long count = (long) row[2];
            membershipCounts.add(new MembershipCount(LocalDate.of(year, month, 1), count));
        }
        return membershipCounts;
    }
}
