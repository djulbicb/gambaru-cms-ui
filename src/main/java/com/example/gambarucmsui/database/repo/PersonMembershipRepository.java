package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonAttendanceEntity;
import com.example.gambarucmsui.database.entity.PersonMembershipEntity;
import com.example.gambarucmsui.ui.dto.statistics.AttendanceCount;
import com.example.gambarucmsui.ui.dto.statistics.AttendanceUserCount;
import com.example.gambarucmsui.util.DataUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class PersonMembershipRepository extends Repository<PersonMembershipEntity> {
    public PersonMembershipRepository(EntityManager entityManager) {
        super(entityManager, PersonMembershipEntity.class);
    }

    public List<PersonMembershipEntity> getAllMembershipsInRange(LocalDateTime startOfMonth, LocalDateTime endOfMonth) {
        String jpql = "SELECT p FROM PersonMembershipEntity p WHERE p.compactDate BETWEEN :startCompactDate AND :endCompactDate ORDER BY p.compactDate DESC";
        TypedQuery<PersonMembershipEntity> query = entityManager.createQuery(jpql, PersonMembershipEntity.class);
        query.setParameter("startCompactDate", DataUtil.getCompactDate(startOfMonth));
        query.setParameter("endCompactDate", DataUtil.getCompactDate(endOfMonth));
        return query.getResultList();
    }
}
