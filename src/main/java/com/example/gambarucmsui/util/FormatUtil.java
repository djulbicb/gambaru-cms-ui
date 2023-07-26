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


    final static DateTimeFormatter fullTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");
    public static String toFullDateTime(LocalDateTime time) {
        return fullTimeFormatter.format(time);
    }

}
