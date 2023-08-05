package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "person")
public class PersonEntity {

    public static enum Gender {
        MALE, FEMALE;
    }

    public PersonEntity() {
    }

    public PersonEntity(String firstName, String lastName, Gender gender, String phone, LocalDateTime createdAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public PersonEntity(String firstName, String lastName, Gender gender, String phone, LocalDateTime createdAt, PersonPictureEntity picture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.createdAt = createdAt;
        this.picture = picture;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long personId;

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

    @OneToMany(mappedBy = "person") // , fetch = FetchType.LAZY
    private List<BarcodeEntity> barcodes = new ArrayList<>();

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private PersonPictureEntity picture;

    // Constructors, getters, setters, and other fields/methods ...


    public List<BarcodeEntity> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<BarcodeEntity> barcodes) {
        this.barcodes = barcodes;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
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

    public PersonPictureEntity getPicture() {
        return picture;
    }

    public void setPicture(PersonPictureEntity picture) {
        this.picture = picture;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PersonEntity that = (PersonEntity) object;
        return Objects.equals(personId, that.personId) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && gender == that.gender && Objects.equals(phone, that.phone) && Objects.equals(createdAt, that.createdAt) && Objects.equals(barcodes, that.barcodes) && Objects.equals(picture, that.picture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId, firstName, lastName, gender, phone, createdAt, barcodes, picture);
    }
}
