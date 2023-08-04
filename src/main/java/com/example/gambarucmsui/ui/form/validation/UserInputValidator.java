package com.example.gambarucmsui.ui.form.validation;

public class UserInputValidator {
    public boolean isValidFirstName(String firstName) {
        return firstName!= null && !firstName.isBlank();
    }
    public String errFirstName() {
        return "Upiši ime.";
    }

    public boolean isValidLastName(String lastName) {
        return lastName!= null && !lastName.isBlank();
    }
    public String errLastName() {
        return "Upiši prezime.";
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
    public String errPhone() {
        return "Upiši telefon npr 0601123456 ili 060/123-456-78.";
    }

    public boolean isValidGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return false;
        }
        boolean isCorrectTranslation = gender.equals("Muški") || gender.equals("Ženski");
        return isCorrectTranslation;
    }

    public String errGender() {
        return "Izaberi pol.";
    }
}
