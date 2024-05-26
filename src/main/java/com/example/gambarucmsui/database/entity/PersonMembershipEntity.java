package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Index;

import java.time.LocalDateTime;

@Entity
@Table(name = "person_membership")
public class PersonMembershipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long membershipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode_id")
    private BarcodeEntity barcode;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    @Column(name = "compact_date")
    @Index(name = "idx_membership_compact_date")
    private int compactDate;

    private int fee;

    public PersonMembershipEntity() {
    }

    public PersonMembershipEntity(BarcodeEntity barcode, LocalDateTime timestamp, int compactDate, int fee) {
        this.membershipId = membershipId;
        this.barcode = barcode;
        this.timestamp = timestamp;
        this.compactDate = compactDate;
        this.fee = fee;
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
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

    public int getCompactDate() {
        return compactDate;
    }

    public void setCompactDate(int compactDate) {
        this.compactDate = compactDate;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }
}
