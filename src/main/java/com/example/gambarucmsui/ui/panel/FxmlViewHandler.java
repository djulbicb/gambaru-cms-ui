package com.example.gambarucmsui.ui.panel;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.example.gambarucmsui.util.PathUtil.CSS;

public interface FxmlViewHandler {
    default Stage createStage(String title, Pane root, Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.setTitle(title);
        dialogStage.setResizable(false);
        scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());
        return dialogStage;
    }

    default Pane loadFxml(String fxmlPath, Object controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        fxmlLoader.setController(controller);
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
