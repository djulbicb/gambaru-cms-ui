package com.example.gambarucmsui.ui.form.validation;

public class UserInputValidator {
    public boolean isValidFirstName(String firstName) {
        return firstName!= null && !firstName.isBlank();
    }
    public boolean isValidLastName(String lastName) {
        return lastName!= null && !lastName.isBlank();
    }
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }

        if (!phone.matches("[\\d+-/]+")) {
            return false;
        }

        return true;
    }
    public boolean isValidGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return false;
        }
        boolean isCorrectTranslation = gender.equals("Muški") || gender.equals("Ženski");
        return isCorrectTranslation;
    }
}
