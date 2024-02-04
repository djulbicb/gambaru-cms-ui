package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.io.IOException;
import java.math.BigDecimal;

public interface TeamUpdatePort {
    public ValidatorResponse verifyAndUpdateTeam(Long teamId, String team, String fee, byte[] pictureData) throws IOException;
    default TeamEntity updateTeam(Long teamId, String teamName, BigDecimal membershipFee, byte[] pictureData) throws IOException {
        return updateTeam(teamId, teamName, membershipFee, null, pictureData);
    }
    TeamEntity updateTeam(Long teamId, String teamName, BigDecimal membershipFee, TeamEntity.Status status, byte[] pictureData) throws IOException;
}
