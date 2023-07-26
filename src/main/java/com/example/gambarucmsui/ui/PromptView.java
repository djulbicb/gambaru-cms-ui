package com.example.gambarucmsui.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;

import java.util.Optional;

public class PromptView {
    public static void showConfirmation(String title, String contentText, Runnable onCloseAction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(title);
        alert.initStyle(StageStyle.UNDECORATED);

        StackPane pane = new StackPane();
        pane.setPadding(new Insets(10));;
        Text content = new Text(contentText);
        content.setStyle("-fx-fill: white;");
        pane.getChildren().add(content);
        alert.getDialogPane().setContent(pane);

        // Apply dark mode style to the alert
        Region dialogPane = (Region) alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #373e43;");
        alert.getDialogPane().getStyleClass().add("dark-mode");

        // Apply dark mode style to the buttons
        ButtonBar buttonBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().forEach(button -> button.setStyle("-fx-text-fill: #fff;"));

        // Apply dark mode style to the header
        Region header = (Region) alert.getDialogPane().lookup(".header-panel");
        header.setStyle("-fx-background-color: #1e74c6;");

        ButtonType buttonYes = new ButtonType("Da");
        ButtonType buttonNo = new ButtonType("Ne");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);
        alert.getDialogPane().setGraphic(null);

//        // Set custom icon
//        Image icon = new Image("path/to/custom_icon.png");
//        ImageView iconView = new ImageView(icon);
//        alert.getDialogPane().setGraphic(iconView);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonYes) {
                onCloseAction.run();
            }
        }
    }
}

