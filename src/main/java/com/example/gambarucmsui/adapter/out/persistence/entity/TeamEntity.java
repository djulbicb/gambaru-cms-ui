package com.example.gambarucmsui.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "team")
public class TeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;
    private String name;
    @Column(name = "membership_payment")
    private BigDecimal membershipPayment;

    public TeamEntity() {
    }

    public TeamEntity(String name, BigDecimal membershipPayment) {
        this.name = name;
        this.membershipPayment = membershipPayment;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMembershipPayment() {
        return membershipPayment;
    }

    public void setMembershipPayment(BigDecimal membershipPayment) {
        this.membershipPayment = membershipPayment;
    }
}
