package com.example.gambarucmsui.database.entity;

import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.ui.dto.admin.SubscriptStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "subscription")
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @OneToOne
    @JoinColumn(name = "barcode_id")
    private BarcodeEntity barcode;

    private boolean isFreeOfCharge;

    private LocalDate startDate;
    private LocalDate endDate;

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(BarcodeEntity barcode, boolean isFreeOfCharge, LocalDate startDate, LocalDate endDate) {
        this.barcode = barcode;
        this.isFreeOfCharge = isFreeOfCharge;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public SubscriptStatus getStatus(LocalDate currentDate) {
        if (isFreeOfCharge) {
            return SubscriptStatus.green(Messages.MEMBERSHIP_IS_PAYED);
        }
        if (currentDate == null || startDate == null || endDate == null) {
            return SubscriptStatus.red(Messages.MEMBERSHIP_NO_PAYMENT);
        }
        if (currentDate.isBefore(startDate)) {
            return SubscriptStatus.red(Messages.MEMBERSHIP_NOT_PAYED);
        }
        if (currentDate.isAfter(endDate)) {
            return SubscriptStatus.red(Messages.MEMBERSHIP_NOT_PAYED);
        }

        long daysBetween = Math.abs(ChronoUnit.DAYS.between(endDate, currentDate));
        if (daysBetween < 5) {
            return SubscriptStatus.orange(Messages.MEMBERSHIP_IS_GOING_TO_EXPIRE(daysBetween));
        }

        return SubscriptStatus.green(Messages.MEMBERSHIP_IS_PAYED);
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public BarcodeEntity getBarcode() {
        return barcode;
    }

    public boolean isFreeOfCharge() {
        return isFreeOfCharge;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setBarcode(BarcodeEntity barcode) {
        this.barcode = barcode;
    }

    public void setFreeOfCharge(boolean freeOfCharge) {
        isFreeOfCharge = freeOfCharge;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "SubscriptionEntity{" +
                "subscriptionId=" + subscriptionId +
                ", barcode=" + barcode.getBarcodeId() +
                ", isFreeOfCharge=" + isFreeOfCharge +
                ", start=" + startDate +
                ", end=" + endDate +
                '}';
    }

}
