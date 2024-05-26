package com.example.gambarucmsui.ui.dto.core;

import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.SubscriptionEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ui.dto.admin.SubscriptStatus;
import com.example.gambarucmsui.util.DataUtil;

import java.time.LocalDate;
import java.util.StringJoiner;

import static com.example.gambarucmsui.util.FormatUtil.*;

public class UserDetail {
    private Long userId;
    private String barcodeId;
    private Long barcodeIdNum;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String team;
    private String createdAt;
    private String discount;
    private String subscriptionStart;
    private String subscriptionEnd;
    private int membershipFee;
    public UserDetail(Long userId, String barcodeId, Long barcodeIdNum, String firstName, String lastName, String phone, String gender, String team, String createdAt, String discount, String subscriptionStart, String subscriptionEnd, int membershipFee) {
        this.userId = userId;
        this.barcodeId = barcodeId;
        this.barcodeIdNum = barcodeIdNum;
        this.firstName = firstName;
        this.phone = phone;
        this.lastName = lastName;
        this.gender = gender;
        this.team = team;
        this.createdAt = createdAt;
        this.discount = discount;
        this.subscriptionStart = subscriptionStart;
        this.subscriptionEnd = subscriptionEnd;
        this.membershipFee= membershipFee;
    }

    public static UserDetail fromEntityToFull(BarcodeEntity b, LocalDate currentDate) {
        PersonEntity user = b.getPerson();
        TeamEntity team = b.getTeam();

        SubscriptStatus status = b.getSubscription().getStatus(currentDate);
        int discount = b.getDiscount();
        String discountStr = formatDiscount(discount);

        return new UserDetail(
                user.getPersonId(),
                String.format("%s %s", status.getEmoji(), formatBarcode(b.getBarcodeId())),
                b.getBarcodeId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                genderToSerbianAbbr(user.getGender()),
                team.getName(),
                toDateFormat(user.getCreatedAt()),
                discountStr,
                getStart(b.getSubscription()),
                getEnd(b.getSubscription()),
                DataUtil.deductFee(b.getTeam().getMembershipPayment(), b.getDiscount())
                );
    }

    public int getMembershipFee() {
        return membershipFee;
    }

    public void setMembershipFee(int membershipFee) {
        this.membershipFee = membershipFee;
    }

    private static String formatDiscount(int discount) {
        if (discount <= 0) {
            return "";
        }
        return String.format("%s RSD", discount);
    }

    private static String getStart(SubscriptionEntity subscription) {
        if (subscription.isFreeOfCharge()) {
            return Messages.FREE_OF_CHARGE;
        }
        if (subscription.getStartDate() == null) {
            return Messages.MEMBERSHIP_NO_PAYMENT;
        }
        return toDateFormat(subscription.getStartDate().atStartOfDay());
    }

    private static String getEnd(SubscriptionEntity subscription) {
        if (subscription.isFreeOfCharge()) {
            return Messages.FREE_OF_CHARGE;
        }
        if (subscription.getStartDate() == null) {
            return Messages.MEMBERSHIP_NO_PAYMENT;
        }
        return toDateFormat(subscription.getEndDate().atStartOfDay());
    }

    public Long getBarcodeIdNum() {
        return barcodeIdNum;
    }

    public void setBarcodeIdNum(Long barcodeIdNum) {
        this.barcodeIdNum = barcodeIdNum;
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

    public String getSubscriptionStart() {
        return subscriptionStart;
    }

    public String getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
