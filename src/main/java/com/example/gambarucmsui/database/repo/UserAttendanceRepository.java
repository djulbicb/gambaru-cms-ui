package com.example.gambarucmsui.database.repo;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonAttendanceEntity;
import com.example.gambarucmsui.ui.dto.statistics.AttendanceCount;
import com.example.gambarucmsui.ui.dto.statistics.AttendanceUserCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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
        LocalDateTime startOfDay = forDate.atStartOfDay();
        LocalDateTime endOfDay = forDate.plusDays(1).atStartOfDay();

        String fetchBarcodesFromAttendanceQuery = "SELECT ua FROM PersonAttendanceEntity ua WHERE ua.timestamp >= :startOfDay AND ua.timestamp < :endOfDay ORDER BY ua.timestamp DESC";
        TypedQuery<PersonAttendanceEntity> subquery = entityManager.createQuery(fetchBarcodesFromAttendanceQuery, PersonAttendanceEntity.class);
        subquery.setParameter("startOfDay", startOfDay);
        subquery.setParameter("endOfDay", endOfDay);

        return subquery.getResultList();
    }

    public List<AttendanceCount> getAttendanceCount(LocalDate forDate) {

        LocalDateTime firstDayOfMonth = YearMonth.from(forDate).atDay(1).atStartOfDay();
        LocalDateTime lastDayOfMonth = YearMonth.from(forDate).atEndOfMonth().atTime(23, 59, 59);

        String queryString = """
        SELECT CAST(a.timestamp AS DATE), COUNT(a)
        FROM PersonAttendanceEntity a
        WHERE a.timestamp BETWEEN :dateStart AND :dateNow
        GROUP BY CAST(a.timestamp AS DATE)
        ORDER BY CAST(a.timestamp AS DATE)
        """;
        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        query.setParameter("dateStart", firstDayOfMonth );
        query.setParameter("dateNow", lastDayOfMonth);

        List<AttendanceCount> resultList = query.getResultList().stream()
                .map(row -> new AttendanceCount(LocalDate.parse(row[0] + ""), (Long) row[1]))
                .collect(Collectors.toList());

        return resultList;
    }

    private List<AttendanceCount> fillMissingDates(List<AttendanceCount> resultList, LocalDate forDate) {
        Map<LocalDate, Long> attendanceMap = resultList.stream()
                .collect(Collectors.toMap(AttendanceCount::getDate, AttendanceCount::getCount));

        List<AttendanceCount> filledList = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(forDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atDay(daysInMonth);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Long count = attendanceMap.getOrDefault(date, 0L);
            filledList.add(new AttendanceCount(date, count));
        }

        return filledList;
    }

    public List<AttendanceUserCount> getAttendancesByUsers(LocalDate monthDate) {
        LocalDateTime firstDayOfMonth = YearMonth.from(monthDate).atDay(1).atStartOfDay();
        LocalDateTime lastDayOfMonth = YearMonth.from(monthDate).atEndOfMonth().atTime(23, 59, 59);

        String queryString = """
            SELECT ua.barcode.person.firstName, ua.barcode.person.lastName, COUNT(ua)
            FROM PersonAttendanceEntity ua
            WHERE ua.timestamp >= :dateStart AND ua.timestamp < :dateEnd
            GROUP BY ua.barcode.id
            """;

        List<Object[]> resultList = entityManager.createQuery(queryString, Object[].class)
                .setParameter("dateStart", firstDayOfMonth)
                .setParameter("dateEnd", lastDayOfMonth)
                .getResultList();

        List<AttendanceUserCount> attendance = new ArrayList<>();
        for (Object[] result : resultList) {
            String userName = (String) result[0];
            String lastName = (String) result[1];
            Long attendanceCount = (Long) result[2];
            attendance.add(new AttendanceUserCount(String.format("%s %s", userName, lastName), attendanceCount));
        }
        System.out.println(resultList.size());
        attendance.sort(Comparator.comparingLong(AttendanceUserCount::getCount).reversed());

        return attendance;
    }


//    public List<PersonAttendanceEntity> findAllForAttendanceDate(LocalDate forDate) {
//        String fetchBarcodesFromAttendanceQuery = "SELECT ua FROM PersonAttendanceEntity ua WHERE DATE(ua.timestamp) = :date ORDER BY ua.timestamp DESC";
//        TypedQuery<PersonAttendanceEntity> subquery = entityManager.createQuery(fetchBarcodesFromAttendanceQuery, PersonAttendanceEntity.class);
//        subquery.setParameter("date", forDate);
//
////        List<Object[]> barcodeEntitiesWithTimestamp = subquery.getResultList();
////        List<BarcodeWithAttendance> wrappers = barcodeEntitiesWithTimestamp.stream()
////                .map(arr -> new BarcodeWithAttendance((BarcodeEntity) arr[0], (PersonAttendanceEntity) arr[1]))
////                .collect(Collectors.toList());
//
//        return subquery.getResultList();
//    }
}
