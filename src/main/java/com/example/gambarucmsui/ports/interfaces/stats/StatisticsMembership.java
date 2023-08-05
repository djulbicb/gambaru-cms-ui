package com.example.gambarucmsui.ports.interfaces.stats;

import com.example.gambarucmsui.ui.dto.statistics.MembershipCount;

import java.util.List;

public interface StatisticsMembership {
    public List<MembershipCount> getMembershipCountByMonthLastYear();
}
