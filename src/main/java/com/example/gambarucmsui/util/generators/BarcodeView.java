package com.example.gambarucmsui.util.generators;

import java.awt.image.BufferedImage;

public class BarcodeView{
    private final String barcodeText;
    private final BufferedImage bufferedImage;

    public BarcodeView(String barcodeText, BufferedImage bufferedImage) {
        this.barcodeText = barcodeText;
        this.bufferedImage = bufferedImage;
    }

    public String getBarcodeText() {
        return barcodeText;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
