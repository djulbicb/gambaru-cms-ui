package com.example.gambarucmsui.database.entity;

import jakarta.persistence.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity
@Table(name = "team_logo")
public class TeamLogoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_logo_id")
    private Long teamLogoId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "picture_data", columnDefinition = "LONGBLOB")
    private byte[] pictureData;

    @Column(name = "team_id")
    private Long teamId;

    public TeamLogoEntity() {
    }

    public TeamLogoEntity(byte[] pictureData, Long teamId) {
        this.pictureData = pictureData;
        this.teamId = teamId;
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

