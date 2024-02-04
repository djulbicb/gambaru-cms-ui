package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;

import java.io.IOException;
import java.math.BigDecimal;

public interface TeamSavePort {
    public ValidatorResponse verifyAndSaveTeam(String team, String fee, byte[] pictureData) throws IOException;
    public TeamEntity save(String team, BigDecimal membershipFee, byte[] pictureData) throws IOException;
}
