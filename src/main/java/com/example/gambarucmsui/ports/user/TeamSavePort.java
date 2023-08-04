package com.example.gambarucmsui.ports.user;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Response;

import java.math.BigDecimal;

public interface TeamSavePort {
    public Response<TeamEntity> saveTeam(String team, BigDecimal membershipFee);
    public void addToBulkSave(String team, BigDecimal membershipFee);
    public void executeBulkSave();
}
