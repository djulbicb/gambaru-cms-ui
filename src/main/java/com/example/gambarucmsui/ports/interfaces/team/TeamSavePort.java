package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Response;

import java.math.BigDecimal;

public interface TeamSavePort {
    public Response<TeamEntity> save(String team, BigDecimal membershipFee);
}
