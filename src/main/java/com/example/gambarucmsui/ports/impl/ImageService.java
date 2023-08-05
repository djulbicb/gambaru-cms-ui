package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.ports.interfaces.utility.ResizedAndOptimizedImagePort;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageService implements ResizedAndOptimizedImagePort {
    @Override
    public void resizeAndOptimizeImage() throws IOException {
        String inputImagePath = "path/to/input/image.jpg";
        String outputImagePath = "path/to/output/resized_image.jpg";
        int newWidth = 300; // New width in pixels
        int newHeight = 200; // New height in pixels
        float quality = 0.7f; // Image quality (0.0 - 1.0)

        // Load the original image
        BufferedImage originalImage = ImageIO.read(new File(inputImagePath));

        // Create a new BufferedImage with the desired dimensions
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        // Draw the original image onto the new BufferedImage
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        // Compress and save the resized image
        File outputImageFile = new File(outputImagePath);
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(quality);

        ImageIO.createImageOutputStream(outputImageFile);
        imageWriter.setOutput(ImageIO.createImageOutputStream(outputImageFile));
        imageWriter.write(null, new javax.imageio.IIOImage(resizedImage, null, null), imageWriteParam);
        imageWriter.dispose();

        System.out.println("Image resized and compressed successfully.");
    }
}
