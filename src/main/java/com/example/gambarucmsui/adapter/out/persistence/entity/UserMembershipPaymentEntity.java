package com.example.gambarucmsui.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_membership_payments")
public class UserMembershipPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_payment_id")
    private Long membershipPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode_id")
    private BarcodeEntity barcode;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    private BigDecimal money;

    public UserMembershipPaymentEntity(BarcodeEntity barcode) {
        this.barcode = barcode;
        this.timestamp = LocalDateTime.now();
        this.money = BigDecimal.ONE;
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
}
