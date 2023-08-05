package com.example.gambarucmsui.ui.form.validation;

import static com.example.gambarucmsui.util.FormatUtil.isDecimal;

public class TeamInputValidator {
    public boolean isTeamNameValid(String teamName) {
        return teamName!= null && !teamName.isBlank();
    }

    public boolean isFeeValid(String paymentFeeStr) {
        return paymentFeeStr != null && !paymentFeeStr.isBlank() && isDecimal(paymentFeeStr);
    }

    public static String errTeamName() {
        return "Upiši ime tima";
    }

    public static String errTeamNameExists() {
        return "Takvo ime tima već postoji. Upiši drugačije ime.";
    }

    public static String errTeamFee() {
        return "Upiši cenu članarine. Npr 4000";
    }

    public static String msgTeamIsCreated(String teamName) {
        return String.format("Tim %s je kreiran", teamName);
    }
}
