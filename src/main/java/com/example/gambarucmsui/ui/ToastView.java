package com.example.gambarucmsui.ui;

import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.util.DataUtil;
import com.example.gambarucmsui.util.PathUtil;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javafx.animation.FadeTransition;

import java.io.ByteArrayInputStream;

public class ToastView {

    public static void showModal(String message) {
        Label label = new Label(message);
        label.setFont(Font.font(20));
        label.setStyle("-fx-text-fill: white;");
        showModal(label, 1100, 100);
    }
    public static void showModal(Node content, double durationMillis, double fadeDurationMillis) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initStyle(StageStyle.TRANSPARENT);
        modalStage.setResizable(false);
        modalStage.setAlwaysOnTop(true);

        StackPane modalRoot = new StackPane(content);
        modalRoot.setStyle("-fx-background-radius: 10; -fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20px;");
        modalRoot.setAlignment(Pos.CENTER);

        Scene modalScene = new Scene(modalRoot);
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

    public static void showAttendance(UserEntity user) {
        VBox box = new VBox();

        Image userPicture;
        if (user.getPicture() != null && user.getPicture().getPictureData() != null) {
            userPicture = new Image(new ByteArrayInputStream(user.getPicture().getPictureData()));
        } else {
            userPicture = DataUtil.loadImageFromResources(PathUtil.USER_NOT_FOUND);
        }
        box.getChildren().add(new ImageView(userPicture));
        box.getChildren().add(new Label(user.getFirstName() + " " + user.getLastName()));

        showModal(box, 1100, 100);
    }
}

