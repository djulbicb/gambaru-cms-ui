package com.example.gambarucmsui.ports.interfaces.membership;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.gambarucmsui.common.Messages.MEMBERSHIP_IS_PAYED;
import static com.example.gambarucmsui.common.Messages.MEMBERSHIP_NOT_PAYED;

public interface GetMembershipStatusPort {

    default State getLastMembershipForUser(BarcodeEntity barcode, LocalDate currentDate) {
        LocalDateTime payment = barcode.getLastMembershipPaymentTimestamp();
        if (payment == null) {
            return State.empty();
        }
        return getLastMembershipForUser(payment.toLocalDate(), currentDate);
    }

    State getLastMembershipForUser(LocalDate lastMembershipPaymentTimestamp, LocalDate now);

    public static class State {
        public static State empty() {
            State state = new State(Color.RED, "Nema uplata članarine.");
            return state;
        }
        public static State orange(Long days) {
            String daysMessage = "";
            if (days == 1) {
                daysMessage = String.format("%s dan", days);
            } else {
                daysMessage = String.format("%s dana", days);
            }

            State state = new State(Color.ORANGE, String.format("Članarina uskoro ističe. Za %s.", daysMessage));
            return state;
        }
        public static State green(LocalDate timestamp) {
            State state = new State(Color.GREEN, MEMBERSHIP_IS_PAYED);
            return state;
        }
        public static State red() {
            State state = new State(Color.RED, MEMBERSHIP_NOT_PAYED);
            return state;
        }

        private long days;
        private LocalDateTime timestamp;
        private String message;
        private Color color;

        public boolean isOrange() {
            return color == Color.ORANGE;
        }
        public boolean isRed() {
            return color == Color.RED;
        }
        public boolean isGreen() {
            return color == Color.GREEN;
        }
        public enum Color {
            GREEN, ORANGE, RED
        }

        public State(long days, LocalDateTime timestamp, String message, Color color) {
            this.days = days;
            this.timestamp = timestamp;
            this.message = message;
            this.color = color;
        }
        public State(Color color, String message) {
            this.message = message;
            this.color = color;
        }

        public long getDays() {
            return days;
        }

        public String getMessage() {
            return message;
        }

        public Color getColor() {
            return color;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
