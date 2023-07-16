package com.example.gambarucmsui.common;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Objects;

public abstract class DelayedListener<T> implements ChangeListener<T> {
    private final PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(1));
    private T ov, nv;

    public DelayedListener() {
        pause.setOnFinished(event -> {
            if (!Objects.equals(nv, ov)) {
                onChanged(nv);
                ov = nv;
            }
        });
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        nv = newValue;
        pause.playFromStart();
    }

    public abstract void onChanged(T value);
}
