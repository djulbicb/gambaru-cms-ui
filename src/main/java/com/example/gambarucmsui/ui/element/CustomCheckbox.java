package com.example.gambarucmsui.ui.element;

import javafx.scene.control.ToggleButton;

public class CustomCheckbox extends ToggleButton {

    public CustomCheckbox(String text) {
        super(text);
        initialize();
    }

    private void initialize() {
        getStyleClass().add("custom-checkbox");
        setSelected(false);

        selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setText("✔ " + getText());
            } else {
                setText(getText().replace("✔ ", ""));
            }
        });

        setOnAction(event -> {
            if (isSelected()) {
                System.out.println("Custom Checkbox Selected");
                // Your logic for when the checkbox is selected
            } else {
                System.out.println("Custom Checkbox Deselected");
                // Your logic for when the checkbox is deselected
            }
        });
    }
}
