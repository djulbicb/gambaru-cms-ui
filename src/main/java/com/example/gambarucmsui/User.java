package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class User {
    private Long barcodeId;
    private String firstName;
    private String lastName;
    private UserEntity.Gender gender;
    private TeamEntity team;
    private LocalDateTime createdAt;
    private LocalDateTime lastAttendanceTimestamp;
    private LocalDateTime lastMembershipPaymentTimestamp;

    public User(Long barcodeId, String firstName, String lastName) {
        this.barcodeId = barcodeId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getBarcodeId() {
        return barcodeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserEntity.Gender getGender() {
        return gender;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastAttendanceTimestamp() {
        return lastAttendanceTimestamp;
    }

    public LocalDateTime getLastMembershipPaymentTimestamp() {
        return lastMembershipPaymentTimestamp;
    }
}
