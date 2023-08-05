package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.repo.PersonPictureRepository;
import com.example.gambarucmsui.ports.interfaces.user.PersonPictureBarcodePurgePort;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageService implements PersonPictureBarcodePurgePort {
    private final PersonPictureRepository personPictureRepository;

    public ImageService(PersonPictureRepository personPictureRepository) {
        this.personPictureRepository = personPictureRepository;
    }

    @Override
    public void purge() {
        personPictureRepository.deleteAll();
    }
}

