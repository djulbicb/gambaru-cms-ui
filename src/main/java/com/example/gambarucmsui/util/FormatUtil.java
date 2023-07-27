package com.example.gambarucmsui.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtil {
    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public static boolean isDecimal(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public final static boolean isBarcode(String barcodeStr) {
        if (barcodeStr == null || barcodeStr.isBlank()) {
            return false;
        }

        String numberOnly= barcodeStr.trim().replaceAll("[^0-9]", "");
        return numberOnly.length() == 10;
    }

    public final static Long parseBarcodeStr (String barcodeStr) {
        String numberOnly= barcodeStr.trim().replaceAll("[^0-9]", "");
        return Long.valueOf(numberOnly);
    }

    public final static String cleanBarcodeStr (String barcodeStr) {
        return barcodeStr.trim().replaceAll("[^0-9]", "");
    }

    final static DateTimeFormatter fullTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");
    public static String toFullDateTime(LocalDateTime time) {
        return fullTimeFormatter.format(time);
    }

}
