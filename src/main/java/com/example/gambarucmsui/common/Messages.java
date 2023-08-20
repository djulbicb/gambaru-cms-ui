package com.example.gambarucmsui.common;

public class Messages {
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

}
