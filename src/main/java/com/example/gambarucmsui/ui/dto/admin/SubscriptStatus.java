package com.example.gambarucmsui.ui.dto.admin;

import com.example.gambarucmsui.common.model.Color;

import java.time.LocalDateTime;

public class SubscriptStatus {
    public static final String GREEN_CHECKMARK = "\u2714\uFE0F";
    public static final String ORANGE_EXCLAMATION = "\u2757\uFE0F";
    public static final String RED_X = "\u274C\uFE0F";
    private final Color color;
    private final String message;

    public SubscriptStatus(Color color, String message) {
        this.color = color;
        this.message = message;
    }

    public static SubscriptStatus red(String message) {
        return new SubscriptStatus(Color.RED, message);
    }

    public static SubscriptStatus orange(String message) {
        return new SubscriptStatus(Color.ORANGE, message);
    }

    public static SubscriptStatus green(String message) {
        return new SubscriptStatus(Color.GREEN, message);
    }

    public Color getColor() {
        return color;
    }

    public String getMessage() {
        return message;
    }

    public String getEmoji() {
        if (color == Color.ORANGE) {
            return ORANGE_EXCLAMATION;
        }
        if (color == Color.GREEN) {
            return GREEN_CHECKMARK;
        }
        return RED_X;
    }
}
