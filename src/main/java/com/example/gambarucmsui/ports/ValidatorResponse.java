package com.example.gambarucmsui.ports;

import java.util.HashMap;
import java.util.Map;

public class ValidatorResponse {
    private Map<String, String> errors = new HashMap<>();

    public ValidatorResponse(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }
}
