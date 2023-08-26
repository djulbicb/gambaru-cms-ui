package com.example.gambarucmsui;

import com.example.gambarucmsui.database.entity.BarcodeEntity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestData {
    public final static LocalDateTime NOW_DATE_TIME = LocalDate.of(2023, 6, 1).atStartOfDay(); // 21.06.2023
    public final static LocalDate JANUARY_END_DATE = LocalDate.of(2023, 1, 31);
    public final static LocalDate FEBRUARY_START_DATE = LocalDate.of(2023, 2, 1);
    public final static LocalDate FEBRUARY_MID_DATE = LocalDate.of(2023, 2, 14);
    public final static LocalDate FEBRUARY_END_DATE = LocalDate.of(2023, 2, 28);
    public final static LocalDate MARCH_START_DATE = LocalDate.of(2023, 3, 1);

    public static BarcodeEntity dummyBarcode(Long barcodeId) {
        BarcodeEntity barcode = new BarcodeEntity();
        barcode.setBarcodeId(barcodeId);
        barcode.setStatus(BarcodeEntity.Status.NOT_USED);
        return barcode;
    }

    public static byte[] createBlackImageByteArray() {
        int IMAGE_WIDTH = 30;
        int IMAGE_HEIGHT = 30;

        BufferedImage blackImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = blackImage.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        g.dispose();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(blackImage, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
