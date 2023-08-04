package com.example.gambarucmsui.ports.user;

import java.math.BigDecimal;

public interface TeamUpdatePort {
    public boolean updateTeam(Long teamId, String team, BigDecimal membershipFee);

}
