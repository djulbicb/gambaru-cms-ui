package com.example.gambarucmsui.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class UserEntity {

    public static enum Gender {
        MALE, FEMALE;

        public static String toSerbianLbl(Gender gender) {
            if (gender.equals(Gender.MALE)) {
                return "M";
            } else {
                return "Ž";
            }
        }

        public static String toSerbian(Gender gender) {
            if (gender.equals(Gender.MALE)) {
                return "Muški";
            } else {
                return "Ženski";
            }
        }
    }

    public UserEntity() {
    }

    public UserEntity(String firstName, String lastName, Gender gender, String phone, List<BarcodeEntity> barcodes, LocalDateTime createdAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.barcodes = barcodes;
        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "phone")
    private String phone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BarcodeEntity> barcodes = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "last_attendance_timestamp")
//    private LocalDateTime lastAttendanceTimestamp;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "last_membership_payment_timestamp")
//    private LocalDateTime lastMembershipPaymentTimestamp;


    // Constructors, getters, setters, and other fields/methods ...

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<BarcodeEntity> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<BarcodeEntity> barcodes) {
        this.barcodes = barcodes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

//    public LocalDateTime getLastAttendanceTimestamp() {
//        return lastAttendanceTimestamp;
//    }
//
//    public void setLastAttendanceTimestamp(LocalDateTime lastAttendanceTimestamp) {
//        this.lastAttendanceTimestamp = lastAttendanceTimestamp;
//    }
//
//    public LocalDateTime getLastMembershipPaymentTimestamp() {
//        return lastMembershipPaymentTimestamp;
//    }
//
//    public void setLastMembershipPaymentTimestamp(LocalDateTime lastMembershipPaymentTimestamp) {
//        this.lastMembershipPaymentTimestamp = lastMembershipPaymentTimestamp;
//    }

    public void addBarcode(BarcodeEntity barcode) {
        barcode.setUser(this);
        barcodes.add(barcode);
    }

    public void removeBarcode(BarcodeEntity barcode) {
        barcodes.remove(barcode);
        barcode.setUser(null);
    }
}
