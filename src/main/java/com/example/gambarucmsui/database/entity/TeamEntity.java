package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "team")
public class TeamEntity {

    public static enum Status {
        ACTIVE, DELETED;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String name;
    public static final String TEAM_NAME = "name";
    @Column(name = "membership_payment")
    private BigDecimal membershipPayment;
    public static final String MEMBERSHIP_PAYMENT = "membershipPayment";
    public TeamEntity() {
    }

    public TeamEntity(String name, Status status, BigDecimal membershipPayment) {
        this.name = name;
        this.membershipPayment = membershipPayment;
        this.status = status;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
