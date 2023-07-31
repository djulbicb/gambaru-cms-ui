package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.ui.form.FormUserAddController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public interface PanelHeader {
    void initialize();
    void viewSwitched();

    default Stage createStage(String title, Pane root, Stage primaryStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.setTitle(title);
        dialogStage.setResizable(false);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return dialogStage;
    }

    default Pane loadFxml(String fxmlPath, Object controller) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }
}
