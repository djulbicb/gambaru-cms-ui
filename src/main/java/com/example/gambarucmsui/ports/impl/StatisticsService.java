package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.repo.UserAttendanceRepository;
import com.example.gambarucmsui.database.repo.UserMembershipRepository;
import com.example.gambarucmsui.ports.interfaces.stats.StaticticsAttendance;
import com.example.gambarucmsui.ports.interfaces.stats.StatisticsMembership;
import com.example.gambarucmsui.ui.dto.statistics.AttendanceCount;
import com.example.gambarucmsui.ui.dto.statistics.MembershipCount;

import java.util.List;

public class StatisticsService implements StaticticsAttendance, StatisticsMembership {

    private final UserAttendanceRepository attendanceRepo;
    private final UserMembershipRepository membershipRepo;

    public StatisticsService(UserAttendanceRepository attendanceRepo, UserMembershipRepository membershipRepo) {
        this.attendanceRepo = attendanceRepo;
        this.membershipRepo = membershipRepo;
    }


    @Override
    public List<AttendanceCount> getAttendanceDataLast60Days() {
        return attendanceRepo.getAttendanceDataLast60Days();
    }

    @Override
    public List<MembershipCount> getMembershipCountByMonthLastYear() {
        return membershipRepo.getMembershipCountByMonthLastYear();
    }
}
