package com.example.gambarucmsui.ports.interfaces.membership;

import java.time.LocalDate;

public interface GetMembershipStatusPort {

    State getLastMembershipForUser(Long userId, LocalDate forDate);

    public static class State {
        public static State red() {
            State state = new State(Color.RED, "Članarina nije plaćena.");
            return state;
        }
        public static State orange() {
            State state = new State(Color.ORANGE, "Članarina nije plaćena.");
            return state;
        }
        public static State green() {
            State state = new State(Color.GREEN, "Članarina plaćena.");
            return state;
        }


        private long days;
        private LocalDate timestamp;
        private String message;
        private Color color;
        public enum Color {
            GREEN, ORANGE, RED
        }

        public State(long days, LocalDate timestamp, String message, Color color) {
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

        public LocalDate getTimestamp() {
            return timestamp;
        }
    }
}
