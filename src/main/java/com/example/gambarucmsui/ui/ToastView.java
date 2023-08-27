package com.example.gambarucmsui.ui;

import com.example.gambarucmsui.ui.panel.FxmlViewHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javafx.animation.FadeTransition;

public class ToastView implements FxmlViewHandler {

    public static void showModal(String message) {
        Label label = createLabel(message);
        StackPane root = new StackPane(label);
        root.setPadding(new Insets(20));

        showModal(root, 1100, 100);
    }
    public static void showModal(Node content, double durationMillis, double fadeDurationMillis) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initStyle(StageStyle.TRANSPARENT);
        modalStage.setResizable(false);
        modalStage.setAlwaysOnTop(true);

        StackPane modalRoot = new StackPane(content);

        modalRoot.setStyle("-fx-background-radius: 5; -fx-background-color: rgba(0, 0, 0, 1); -fx-padding: 0px;");
        modalRoot.setAlignment(Pos.CENTER);
//        modalRoot.setStyle(
//                "-fx-background-color: rgba(0, 0, 0, 0.9);" +
//                        "-fx-padding: 3px;" +
//                        "-fx-background-insets: 5;" +
//                        "-fx-effect: dropshadow(gaussian, " +
//                        "rgba(0,0,0,0.3), " +
//                        dropShadow.getRadius() + ", " +
//                        dropShadow.getOffsetY() + ", " +
//                        dropShadow.getOffsetX() + ", 0);"
//        );

        Scene modalScene = new Scene(modalRoot);
        modalScene.getStylesheets().add("popup");
        modalScene.setFill(Color.TRANSPARENT);
        modalStage.setScene(modalScene);

        modalStage.sizeToScene();
        modalStage.show();

        // Fade in
        FadeTransition fadeInTransition = new FadeTransition(Duration.millis(fadeDurationMillis), modalRoot);
        fadeInTransition.setFromValue(0);
        fadeInTransition.setToValue(1);
        fadeInTransition.play();

        // Fade out
        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(fadeDurationMillis), modalRoot);
        fadeOutTransition.setFromValue(1);
        fadeOutTransition.setToValue(0);
        fadeOutTransition.setDelay(Duration.millis(durationMillis + fadeDurationMillis));
        fadeOutTransition.setOnFinished(event -> modalStage.hide());

        fadeOutTransition.play();
    }


    private static Label createLabel(String message) {
        Label label = new Label(message);
        label.setFont(Font.font(20));
        label.setStyle("-fx-text-fill: white;");
        return label;
    }
}

