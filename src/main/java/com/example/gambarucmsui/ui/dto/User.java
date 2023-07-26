package com.example.gambarucmsui.ui.dto;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.gambarucmsui.common.LayoutUtil.getOrElse;

public class User {
    private Long barcodeId;
    private String firstName;
    private String lastName;
    private String phone;
    private UserEntity.Gender gender;
    private TeamEntity team;
    private String createdAt;
    private String lastAttendanceTimestamp;
    private String lastMembershipPaymentTimestamp;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");

    public User(Long barcodeId, String firstName, String lastName, String phone, UserEntity.Gender gender, TeamEntity team, LocalDateTime createdAt, LocalDateTime lastAttendanceTimestamp, LocalDateTime lastMembershipPaymentTimestamp) {
        this.barcodeId = barcodeId;
        this.firstName = firstName;
        this.phone = phone;
        this.lastName = lastName;
        this.gender = gender;
        this.team = team;
        System.out.println(barcodeId);
        System.out.println(createdAt);

        if (createdAt != null) {
            this.createdAt = formatter.format(createdAt);
        }
        if (lastAttendanceTimestamp != null) {
            this.lastAttendanceTimestamp = formatter.format(lastAttendanceTimestamp);
        }
        if (lastMembershipPaymentTimestamp != null) {
            this.lastMembershipPaymentTimestamp = formatter.format(lastMembershipPaymentTimestamp);
        }
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

    public String getPhone() {
        return phone;
    }

    public UserEntity.Gender getGender() {
        return gender;
    }

    public TeamEntity getTeam() {
        return team;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLastAttendanceTimestamp() {
        return lastAttendanceTimestamp;
    }

    public String getLastMembershipPaymentTimestamp() {
        return lastMembershipPaymentTimestamp;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
