package com.example.gambarucmsui.ui.dto.core;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;

import java.util.StringJoiner;

import static com.example.gambarucmsui.util.FormatUtil.*;

public class UserDetail {
    private Long userId;
    private String barcodeId;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String team;
    private String createdAt;
    private String timestamp;
    public UserDetail(Long userId, String barcodeId, String firstName, String lastName, String phone, String gender, String team, String createdAt, String timestamp) {
        this.userId = userId;
        this.barcodeId = barcodeId;
        this.firstName = firstName;
        this.phone = phone;
        this.lastName = lastName;
        this.gender = gender;
        this.team = team;
        this.createdAt = createdAt;
        this.timestamp = timestamp;
    }

    public static UserDetail fromEntityToFull(BarcodeEntity b, String timestamp) {
        PersonEntity user = b.getPerson();
        TeamEntity team = b.getTeam();
//        for (BarcodeEntity barcode : o.getBarcodes()) {
//            barcodeCsv.add(barcode.getBarcodeId().toString());
//        }
//        StringJoiner teamCsv = new StringJoiner(",");
//        for (BarcodeEntity barcode : o.getBarcodes()) {
//            TeamEntity team = barcode.getTeam();
//            if (team != null) {
//                teamCsv.add(team.getName());
//            }
//        }

        if (user == null) {
            System.out.println("");
        }

        return new UserDetail(
                user.getPersonId(),
                formatBarcode(b.getBarcodeId()),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                genderToSerbianAbbr(user.getGender()),
                team.getName(),
                toDateFormat(user.getCreatedAt()),
                timestamp);
    }

    public static UserDetail fromEntityToFull(PersonEntity user) {
        StringJoiner barcodeCsv = new StringJoiner(",");
        StringJoiner teamCsv = new StringJoiner(",");

        for (BarcodeEntity barcode : user.getBarcodes()) {
            barcodeCsv.add(barcode.getBarcodeId().toString());
        }


        return new UserDetail(
                user.getPersonId(),
                "barcode",
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                genderToSerbianAbbr(user.getGender()),
                teamCsv.toString(),
                toDateFormat(user.getCreatedAt()),
                "");
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

    public String getTimestamp() {
        return timestamp;
    }

}
