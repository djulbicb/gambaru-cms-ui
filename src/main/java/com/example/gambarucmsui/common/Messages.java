package com.example.gambarucmsui.common;

import com.example.gambarucmsui.ports.ValidatorResponse;

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
    public final static String TEAM_IS_DELETED (String teamName) {
        return String.format("Tim %s je obrisan", teamName);
    }
    // MEMBERSHIP

    public final static String MEMBERSHIP_PAYMENT_ADDED(String firstName, String lastName) {
        return String.format("Članarina plaćena za %s %s.", firstName, lastName);
    };
    public final static String MEMBERSHIP_ALREADY_PAYED = "Članarina za ovaj mesec je već plaćena.";
    public final static String MEMBERSHIP_NOT_PAYED = "Članarina nije plaćena.";
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
}
