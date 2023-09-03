package com.example.gambarucmsui.ui.form.validation;

public class UserInputValidator {
    public boolean isValidFirstName(String firstName) {
        return firstName!= null && !firstName.isBlank();
    }
    public boolean isValidLastName(String lastName) {
        return lastName!= null && !lastName.isBlank();
    }
    public boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }

        // accepts empty phone
        if (phone.isBlank()) {
            return true;
        }

        return phone.matches("[\\d+-/]+");
    }
    public boolean isValidGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return false;
        }
        boolean isCorrectTranslation = gender.equals("Muški") || gender.equals("Ženski");
        return isCorrectTranslation;
    }
}
