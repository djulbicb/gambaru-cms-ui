package com.example.gambarucmsui.ui.dto.admin;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;

import java.util.StringJoiner;

import static com.example.gambarucmsui.util.FormatUtil.*;

public class UserAdminDetail {
    private Long userId;
    private String barcodeTeam;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String createdAt;
    public UserAdminDetail(Long userId, String barcodeTeam, String firstName, String lastName, String phone, String gender, String createdAt) {
        this.userId = userId;
        this.barcodeTeam = barcodeTeam;
        this.firstName = firstName;
        this.phone = phone;
        this.lastName = lastName;
        this.gender = gender;
        this.createdAt = createdAt;
    }

    public static UserAdminDetail fromEntityToFull(PersonEntity user) {
        StringJoiner barcodeTeamCsv = new StringJoiner(",");

        for (BarcodeEntity barcode : user.getBarcodes()) {
            if (barcode.getStatus() != BarcodeEntity.Status.ASSIGNED) {
                continue;
            }
            String barcodeWithTeam = String.format("%s (%s)", formatBarcode(barcode.getBarcodeId()), barcode.getTeam().getName());
            barcodeTeamCsv.add(barcodeWithTeam);
        }


        return new UserAdminDetail(
                user.getPersonId(),
                barcodeTeamCsv.toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                genderToSerbianAbbr(user.getGender()),
                toDateFormat(user.getCreatedAt()));
    }

    public Long getUserId() {
        return userId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public String getBarcodeTeam() {
        return barcodeTeam;
    }
}
