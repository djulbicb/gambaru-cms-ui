package com.example.gambarucmsui.ui.form.validation;

import static com.example.gambarucmsui.util.FormatUtil.isDecimal;

public class TeamInputValidator {
    public boolean isTeamNameValid(String teamName) {
        return teamName!= null && !teamName.isBlank();
    }

    public boolean isFeeValid(String paymentFeeStr) {
        return !paymentFeeStr.isBlank() && isDecimal(paymentFeeStr);
    }

    public String errTeamName() {
        return "Upiši ime tima";
    }

    public String errTeamNameExists() {
        return "Takvo ime tima već postoji. Upiši drugačije ime.";
    }

    public String errTeamFee() {
        return "Upiši cenu članarine. Npr 4000";
    }
}
