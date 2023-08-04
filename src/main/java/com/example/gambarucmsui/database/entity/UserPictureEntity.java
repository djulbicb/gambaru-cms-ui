package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity
@Table(name = "user_picture")
public class UserPictureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "picture_id")
    private Long pictureId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "picture_data", columnDefinition = "BLOB")
    private byte[] pictureData;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public UserPictureEntity() {
    }

    public UserPictureEntity(byte[] pictureData, UserEntity user) {
        this.pictureData = pictureData;
        this.user = user;
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

