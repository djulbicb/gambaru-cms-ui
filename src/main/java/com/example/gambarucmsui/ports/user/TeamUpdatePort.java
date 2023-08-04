package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Response;

import java.math.BigDecimal;

public interface TeamUpdatePort {
    public Response<TeamEntity> updateTeam(Long teamId, String team, BigDecimal membershipFee);

}
