package com.example.gambarucmsui.ports.interfaces.team;

import com.example.gambarucmsui.ports.ValidatorResponse;

public interface TeamDeletePort {
    public ValidatorResponse validateAndDeleteTeam(Long teamId);
}
