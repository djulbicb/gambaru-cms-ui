package com.example.gambarucmsui.adapter.out.persistence.entity;

import jakarta.persistence.*;

import jakarta.persistence.*;

import jakarta.persistence.*;

@Entity
@Table(name = "barcode")
public class BarcodeEntity {

    public static enum Status {
        NOT_USED, ASSIGNED, DEACTIVATED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "barcode_id")
    private Long barcodeId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity team;

    // Constructors, getters, setters, and other fields/methods ...

    public Long getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(Long barcodeId) {
        this.barcodeId = barcodeId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public void setTeam(TeamEntity team) {
        this.team = team;
    }

    public boolean isAssigned() {
        return status.equals(Status.ASSIGNED);
    }
}


