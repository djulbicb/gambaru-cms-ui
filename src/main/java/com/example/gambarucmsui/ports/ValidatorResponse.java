package com.example.gambarucmsui.ports;

import java.util.HashMap;
import java.util.Map;

public class ValidatorResponse {
    private String message;
    private Map<String, String> errors = new HashMap<>();

    public ValidatorResponse(String message) {
        this.message = message;
    }
    public ValidatorResponse(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public boolean isOk() {
        return errors.isEmpty();
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public String getMessage() {
        return message;
    }
}
