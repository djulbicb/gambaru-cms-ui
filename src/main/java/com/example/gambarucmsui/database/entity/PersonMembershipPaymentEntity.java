package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "person_membership_payment")
public class PersonMembershipPaymentEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode_id")
    private BarcodeEntity barcode;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_payment_id")
    private Long membershipPaymentId;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
    private int paymentMonth;
    private int paymentYear;
    private BigDecimal money;

    public PersonMembershipPaymentEntity() {
    }

    public PersonMembershipPaymentEntity(BarcodeEntity barcode, int paymentMonth, int paymentYear, LocalDateTime timestamp, BigDecimal fee) {
        this.barcode = barcode;
        this.timestamp = timestamp;
        this.money = fee;
        this.paymentMonth = paymentMonth;
        this.paymentYear = paymentYear;
    }

    public Long getMembershipPaymentId() {
        return membershipPaymentId;
    }

    public void setMembershipPaymentId(Long membershipPaymentId) {
        this.membershipPaymentId = membershipPaymentId;
    }

    public BarcodeEntity getBarcode() {
        return barcode;
    }

    public void setBarcode(BarcodeEntity barcode) {
        this.barcode = barcode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public int getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(int paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public int getPaymentYear() {
        return paymentYear;
    }

    public void setPaymentYear(int paymentYear) {
        this.paymentYear = paymentYear;
    }
}
