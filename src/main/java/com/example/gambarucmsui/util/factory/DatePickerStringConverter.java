package com.example.gambarucmsui.util.factory;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerStringConverter extends StringConverter<LocalDate> {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");

    @Override
    public String toString(LocalDate date) {
        if (date != null) {
            return dateFormatter.format(date);
        } else {
            return "";
        }
    }

    @Override
    public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) {
            return LocalDate.parse(string, dateFormatter);
        } else {
            return null;
        }
    }
}