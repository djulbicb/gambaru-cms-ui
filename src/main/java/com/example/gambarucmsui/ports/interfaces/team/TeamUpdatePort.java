package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Response;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.math.BigDecimal;

public interface TeamUpdatePort {
    public ValidatorResponse verifyUpdateTeam (Long teamId, String team, String fee);
    default TeamEntity updateTeam(Long teamId, String teamName, BigDecimal membershipFee) {
        return updateTeam(teamId, teamName, membershipFee, null);
    }
    TeamEntity updateTeam(Long teamId, String teamName, BigDecimal membershipFee, TeamEntity.Status status);
}
