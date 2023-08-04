package com.example.gambarucmsui.ports;

import java.awt.geom.RectangularShape;

public class Response<T> {
    private boolean isOk;
    private String message;
    private T model;

    public static <T> Response<T> bad(String message) {
        return new Response<>(false, message, null);
    }

    public static <T> Response<T> ok(String message, T value) {
        return new Response<>(true, message, value);
    }

    private Response(boolean isOk, String message, T model) {
        this.isOk = isOk;
        this.message = message;
        this.model = model;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        this.isOk = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
}
