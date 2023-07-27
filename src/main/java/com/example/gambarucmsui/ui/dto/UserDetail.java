package com.example.gambarucmsui.ui.dto;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

public class UserDetail {
    private Long userId;
    private String barcodeId;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String team;
    private String createdAt;
    private String lastAttendanceTimestamp;
    private String lastMembershipPaymentTimestamp;

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");

    public UserDetail(Long userId, String barcodeId, String firstName, String lastName, String phone, String gender, String team, LocalDateTime createdAt, LocalDateTime lastAttendanceTimestamp, LocalDateTime lastMembershipPaymentTimestamp) {
        this.userId = userId;
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

    public static UserDetail fromEntity(UserEntity o) {
        StringJoiner barcodeCsv = new StringJoiner(",");
        for (BarcodeEntity barcode : o.getBarcodes()) {
            barcodeCsv.add(barcode.getBarcodeId().toString());
        }
        StringJoiner teamCsv = new StringJoiner(",");
        for (BarcodeEntity barcode : o.getBarcodes()) {
            TeamEntity team = barcode.getTeam();
            if (team != null) {
                teamCsv.add(team.getName());
            }
        }

        return new UserDetail(o.getUserId(), barcodeCsv.toString(), o.getFirstName(), o.getLastName(), o.getPhone(), UserEntity.Gender.toSerbianLbl(o.getGender()), teamCsv.toString(), o.getCreatedAt(), o.getLastAttendanceTimestamp(), o.getLastMembershipPaymentTimestamp());
    }

    public Long getUserId() {
        return userId;
    }

    public String getBarcodeId() {
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

    public String getGender() {
        return gender;
    }

    public String getTeam() {
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
