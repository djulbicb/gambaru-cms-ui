package com.example.gambarucmsui.util;

import com.example.gambarucmsui.database.entity.PersonEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    public final static boolean isBarcodeString(String barcodeStr) {
        if (barcodeStr == null || barcodeStr.isBlank()) {
            return false;
        }

        String numberOnly= barcodeStr.trim().replaceAll("[^0-9]", "");
        return numberOnly.length() == 10;
    }

    public final static Long parseBarcodeStr (String barcodeStr) {
        String numberOnly= barcodeStr.trim().replaceAll("[^0-9]", "");
        if (numberOnly.length() == 0) {
            return 0L;
        }
        return Long.valueOf(numberOnly);
    }

    public final static String cleanBarcodeStr (String barcodeStr) {
        return barcodeStr.trim().replaceAll("[^0-9]", "");
    }

    public final static String formatBarcode(Long barcodeId) {
        return String.format("%010d", barcodeId);
    }

    final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");

    public static String toDateFormat(LocalDateTime time) {
        return dateFormatter.format(time);
    }
    final static DateTimeFormatter fullTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy hh:mm");
    public static String toDateTimeFormat(LocalDateTime time) {
        if (time == null) {
            return "Nema uplata.";
        }
        return fullTimeFormatter.format(time);
    }
    final static DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM/yyyy");
    public static String toMonthYeah(LocalDateTime time) {
        if (time == null) {
            return "Nema uplata.";
        }
        return monthYearFormatter.format(time);
    }

    public static String genderToSerbianAbbr(PersonEntity.Gender gender) {
        return genderToSerbian(gender).substring(0, 1);
    }

    public static String genderToSerbian(PersonEntity.Gender gender) {
        if (gender.equals(PersonEntity.Gender.MALE)) {
            return "Muški";
        } else {
            return "Ženski";
        }
    }

    private static Locale serbianLocale() {
        return new Locale("sr", "RS", "latin");
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
