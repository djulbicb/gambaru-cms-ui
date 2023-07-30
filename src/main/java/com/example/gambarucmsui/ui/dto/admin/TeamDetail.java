package com.example.gambarucmsui.ui.dto.admin;

import java.math.BigDecimal;

public class TeamDetail {
    private Long teamId;
    private String name;
    private BigDecimal fee;

    public TeamDetail(Long teamId, String name, BigDecimal fee) {
        this.teamId = teamId;
        this.name = name;
        this.fee = fee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}
