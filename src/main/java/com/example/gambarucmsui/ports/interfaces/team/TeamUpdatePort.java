package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Response;

import java.math.BigDecimal;

public interface TeamUpdatePort {
    default Response<TeamEntity> updateTeam(Long teamId, String teamName, BigDecimal membershipFee) {
        return updateTeam(teamId, teamName, membershipFee, null);
    }

    Response<TeamEntity> updateTeam(Long teamId, String teamName, BigDecimal membershipFee, TeamEntity.Status status);
}
