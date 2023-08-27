package com.example.gambarucmsui.ui.form.validation;

import static com.example.gambarucmsui.util.FormatUtil.isDecimal;

public class TeamInputValidator {


    public boolean isTeamNameValid(String teamName) {
        return teamName!= null && !teamName.isBlank();
    }

    public boolean isFeeValid(String paymentFeeStr) {
        return paymentFeeStr != null && !paymentFeeStr.isBlank() && isDecimal(paymentFeeStr);
    }
    public static String msgTeamIsUpdated(String name) {
        return String.format("Tim %s je updejtovan", name);
    }

}
