package com.example.gambarucmsui.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtil {
    public static ByteArrayInputStream resizeAndOptimizeImage(File inputImage, int fitToWidth) throws IOException {
        // Load the image from File
        BufferedImage originalImage = ImageIO.read(inputImage);
        return resizeAndOptimizeImage(originalImage, fitToWidth);
    }

    // Resize and compress image from byte[]
    public static ByteArrayInputStream resizeAndOptimizeImage(byte[] imageData, int fitToWidth) throws IOException {
        // Load the image from byte[]
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        BufferedImage originalImage = ImageIO.read(inputStream);
        return resizeAndOptimizeImage(originalImage, fitToWidth);
    }

    public static ByteArrayInputStream resizeAndOptimizeImage(byte[] imageData,  int newWidth, int newHeight) throws IOException {
        // Load the image from byte[]
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        BufferedImage originalImage = ImageIO.read(inputStream);
        return resizeAndOptimizeImage(originalImage, newWidth, newHeight);
    }

    public static ByteArrayInputStream resizeAndOptimizeImage(BufferedImage originalImage, int fitToWidth) throws IOException {
        // Calculate the aspect ratio to maintain proportions while resizing
        double aspectRatio = (double) originalImage.getHeight() / originalImage.getWidth();

        // Calculate the new height based on the fitToWidth value
        int newWidth = fitToWidth;
        int newHeight = (int) (fitToWidth * aspectRatio);

        return resizeAndOptimizeImage(originalImage, newWidth, newHeight);
    }

    public static ByteArrayInputStream resizeAndOptimizeImage(BufferedImage originalImage, int newWidth, int newHeight) throws IOException {
        float quality = 0.7f;

        // Create a new BufferedImage with the desired dimensions
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        // Draw the original image onto the new BufferedImage
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        // Compress the resized image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(quality);

        ImageIO.createImageOutputStream(baos);
        imageWriter.setOutput(ImageIO.createImageOutputStream(baos));
        imageWriter.write(null, new javax.imageio.IIOImage(resizedImage, null, null), imageWriteParam);
        imageWriter.dispose();

        System.out.println("Image resized and compressed successfully.");

        return new ByteArrayInputStream(baos.toByteArray());
    }
}
