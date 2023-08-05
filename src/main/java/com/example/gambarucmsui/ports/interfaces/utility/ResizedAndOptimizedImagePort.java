package com.example.gambarucmsui.ports.interfaces.utility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public interface ResizedAndOptimizedImagePort {
    ByteArrayInputStream resizeAndOptimizeImage(BufferedImage originalImage, int fitToWidth) throws IOException;
    ByteArrayInputStream resizeAndOptimizeImage(File pictureData, int fitToWidth)  throws IOException;
    ByteArrayInputStream resizeAndOptimizeImage(byte[] imageData, int fitToWidth) throws IOException;
}
