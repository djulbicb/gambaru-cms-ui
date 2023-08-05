package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity
@Table(name = "person_picture")
public class PersonPictureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "picture_id")
    private Long pictureId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "picture_data", columnDefinition = "BLOB")
    private byte[] pictureData;

    @OneToOne
    @JoinColumn(name = "person_id")
    private PersonEntity person;

    public PersonPictureEntity() {
    }

    public PersonPictureEntity(byte[] pictureData, PersonEntity person) {
        this.pictureData = pictureData;
        this.person = person;
    }

    public BufferedImage getPicture() {
        try {
            return ImageIO.read(new ByteArrayInputStream(pictureData));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setPicture(BufferedImage picture) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(picture, "png", baos);
            pictureData = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getPictureData() {
        return pictureData;
    }

    public void setPictureData(byte[] pictureData) {
        this.pictureData = pictureData;
    }
}

