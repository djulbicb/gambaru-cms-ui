package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "person_membership_payment")
public class PersonMembershipPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "membership_payment_id")
    private Long membershipPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode_id")
    private BarcodeEntity barcode;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    private int month;
    private int year;
    private BigDecimal money;

    public PersonMembershipPaymentEntity() {
    }

    public PersonMembershipPaymentEntity(BarcodeEntity barcode, int month, int year, LocalDateTime timestamp, BigDecimal fee) {
        this.barcode = barcode;
        this.timestamp = timestamp;
        this.money = fee;
        this.month = month;
        this.year = year;
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
