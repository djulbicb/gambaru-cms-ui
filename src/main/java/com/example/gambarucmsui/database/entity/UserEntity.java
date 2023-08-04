package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class UserEntity {

    public static enum Gender {
        MALE, FEMALE;
    }

    public UserEntity() {
    }

    public UserEntity(String firstName, String lastName, Gender gender, String phone,LocalDateTime createdAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public UserEntity(String firstName, String lastName, Gender gender, String phone, LocalDateTime createdAt, UserPictureEntity picture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.createdAt = createdAt;
        this.picture = picture;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user") // , fetch = FetchType.LAZY
    private List<BarcodeEntity> barcodes = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserPictureEntity picture;

    // Constructors, getters, setters, and other fields/methods ...


    public List<BarcodeEntity> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<BarcodeEntity> barcodes) {
        this.barcodes = barcodes;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserPictureEntity getPicture() {
        return picture;
    }

    public void setPicture(UserPictureEntity picture) {
        this.picture = picture;
    }
}
