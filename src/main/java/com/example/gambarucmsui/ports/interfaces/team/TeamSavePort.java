package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.math.BigDecimal;

public interface TeamSavePort {
    public ValidatorResponse verifySaveTeam (String team, String fee);
    public TeamEntity save(String team, BigDecimal membershipFee);
}
