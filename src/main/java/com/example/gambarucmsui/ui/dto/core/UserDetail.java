package com.example.gambarucmsui.ui.dto.core;

import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.SubscriptionEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ui.dto.admin.SubscriptStatus;

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
    private String subscriptionStart;
    private String subscriptionEnd;
    public UserDetail(Long userId, String barcodeId, Long barcodeIdNum, String firstName, String lastName, String phone, String gender, String team, String createdAt, String subscriptionStart, String subscriptionEnd) {
        this.userId = userId;
        this.barcodeId = barcodeId;
        this.barcodeIdNum = barcodeIdNum;
        this.firstName = firstName;
        this.phone = phone;
        this.lastName = lastName;
        this.gender = gender;
        this.team = team;
        this.createdAt = createdAt;
        this.subscriptionStart = subscriptionStart;
        this.subscriptionEnd = subscriptionEnd;
    }

    public static UserDetail fromEntityToFull(BarcodeEntity b, LocalDate currentDate) {
        PersonEntity user = b.getPerson();
        TeamEntity team = b.getTeam();

        SubscriptStatus status = b.getSubscription().getStatus(currentDate);

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
                getStart(b.getSubscription()),
                getEnd(b.getSubscription()));
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
}
