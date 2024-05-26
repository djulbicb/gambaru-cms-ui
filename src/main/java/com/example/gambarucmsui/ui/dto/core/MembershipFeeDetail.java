package com.example.gambarucmsui.ui.dto.core;

import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.database.entity.PersonMembershipEntity;
import com.example.gambarucmsui.ports.impl.MembershipService;

import static com.example.gambarucmsui.util.FormatUtil.toDateFormat;

public class MembershipFeeDetail {
    private String fullName;
    private String membershipFee;
    private String date;

    public static MembershipFeeDetail fromEntityToFull(PersonMembershipEntity en) {
        PersonEntity person = en.getBarcode().getPerson();
        return new MembershipFeeDetail(
                person.getFirstName(),
                person.getLastName(),
                en.getFee(),
                toDateFormat(en.getTimestamp()));
    }

    public MembershipFeeDetail(String firstName, String lastName, int membershipFee, String date) {
        this.fullName = String.format("%s %s",firstName, lastName);
        this.membershipFee = String.format("%s RSD", membershipFee);
        this.date = date;
    }

    public MembershipFeeDetail(String firstName, String lastName, String feePreformated, String date) {
        this.fullName = String.format("%s %s",firstName, lastName);
        this.membershipFee = feePreformated;
        this.date = date;
    }

    public static MembershipFeeDetail empty() {
        return new MembershipFeeDetail("", "", "", "");
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMembershipFee() {
        return membershipFee;
    }

    public void setMembershipFee(String membershipFee) {
        this.membershipFee = membershipFee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
