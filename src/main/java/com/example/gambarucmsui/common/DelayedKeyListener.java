package com.example.gambarucmsui.common;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

public class DelayedKeyListener implements EventHandler<KeyEvent> {
    private final PauseTransition pause = new PauseTransition(Duration.seconds(1));
    private KeyCode previousKeyCode;
    private String word = "";

    public DelayedKeyListener() {
        pause.setOnFinished(event -> {
            KeyCode currentKeyCode = previousKeyCode;
            previousKeyCode = null;
            if (currentKeyCode != null) {
                System.out.println(word);
                onFinish(word);
                word = "";
            }
        });
    }

    public void onFinish(String word) {
        System.out.println("Inside " + word);
    }

    @Override
    public void handle(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        word += keyCode.getChar();
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            previousKeyCode = keyCode;
            pause.stop();
            pause.playFromStart();
        } else if (event.getEventType() == KeyEvent.KEY_RELEASED && keyCode == previousKeyCode) {
            // Handle key release only if it matches the previously pressed key
            pause.stop();
//            onKeyReleased(keyCode);

            previousKeyCode = null;
        }
    }
}
