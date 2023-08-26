package com.example.gambarucmsui.common;

import java.util.Map;

public class Messages {
    // USER
    public final static String USER_FIRST_NAME_MISSING = "Upiši ime.";
    public final static String USER_LAST_NAME_MISSING = "Upiši prezime.";
    public final static String USER_PHONE_MISSING = "Upiši telefon npr 0601123456 ili 060/123-456-78.";;
    public final static String USER_GENDER_MISSING = "Izaberi pol.";;

    // BARCODE
    public final static String BARCODE_NOT_REGISTERED = "Taj barkod uopšte nije registrovan u bazi.";
    public final static String BARCODE_WRONG_FORMAT = "Upiši barkod npr 123.";
    public final static String BARCODE_IS_DELETED = "Taj barkod je obrisan. Tim sa kojim je asociran je obrisan.";
    public final static String BARCODE_IS_DEACTIVATED = "Taj barkod je deaktiviran.";
    public final static String BARCODE_IS_NOT_ASSIGNED = "Taj barkod nije zadat ijednom korisniku.";

    // TEAM
    public final static String TEAM_DOESNT_EXIST = "Taj tim ne postoji.";
    public static final String BARCODE_IS_NULL = "Upiši barkod.";
    public static final String FREE_OF_CHARGE = "Besplatno";
    public static final String MEMBERSHIP_NO_PAYMENT = "Nema uplata.";

    public final static String TEAM_IS_DELETED (String teamName) {
        return String.format("Tim %s je obrisan", teamName);
    }
    // MEMBERSHIP

    public final static String MEMBERSHIP_PAYMENT_ADDED(String firstName, String lastName) {
        return String.format("Članarina plaćena za %s %s.", firstName, lastName);
    };
    public final static String MEMBERSHIP_ALREADY_PAYED = "Članarina za ovaj mesec je već plaćena.";
    public final static String MEMBERSHIP_NOT_PAYED = "Članarina nije plaćena.";
    public final static String MEMBERSHIP_EXPIRED = "Članarina istekla.";
    public final static String MEMBERSHIP_IS_PAYED = "Članarina je plaćena.";
    public static String TEAM_IS_CREATED(String teamName) {
        return String.format("Tim %s je kreiran", teamName);
    }
    public final static String TEAM_NAME_ALREADY_EXISTS =  "Takvo ime tima već postoji. Upiši drugačije ime.";
    public final static String TEAM_FEE_NOT_VALID =  "Upiši cenu članarine. Npr 4000";
    public final static String TEAM_NAME_NOT_VALID =  "Upiši ime tima";

    public static String TEAM_IS_UPDATED(String teamName) {
        return String.format("Tim %s je updejtovan.", teamName);
    }

    // ATTENDANCE
    public static String ATTENDANCE_MANUALLY_FOUND_USER(String firstName, String lastName) {
        return String.format("Polaznik: %s %s", firstName, lastName);
    }    public static String MEMBERSHIP_MANUALLY_FOUND_USER(String firstName, String lastName) {
        return String.format("Polaznik: %s %s", firstName, lastName);
    }

    public static String BARCODE_IS_VALID(String firstName, String lastName) {
        return String.format("Polaznik: %s %s", firstName, lastName);
    }

    public static String MEMBERSHIP_IS_GOING_TO_EXPIRE(long daysBetween) {
        String wordDay = "dana";
        if (daysBetween == 1) {
            wordDay = "dan";
        }
        return String.format("Članarina ističe za %s %s.", daysBetween, wordDay);
    }

    public static String USER_ADDED_TO_TEAM(String teamName) {
        return String.format("Dodat u tim %s.", teamName);
    }
}
