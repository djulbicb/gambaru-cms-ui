package com.example.gambarucmsui.ui.text;

public class Labels {

    public static String payNextMonth(int membershipFee) {
        return String.format("PLATI NAREDNI %s RSD", membershipFee);
    }

    public static String payNextMonth() {
        return "PLATI NAREDNI MESEC";
    }
}
