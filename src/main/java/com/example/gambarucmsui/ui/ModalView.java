package com.example.gambarucmsui.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.stage.Stage;
public class ModalView<T> extends Stage {
    private final Consumer<T> closeHandler;

    public ModalView(Node content, Consumer<T> closeHandler) {
        this.closeHandler = closeHandler;
        setContent(content);

        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.TRANSPARENT);
        setResizable(false);
        setAlwaysOnTop(true);
    }

    public void setContent(Node content) {
        AnchorPane modalContent = new AnchorPane();
        modalContent.setPadding(new Insets(10));
        modalContent.getChildren().addAll(content);
        AnchorPane.setTopAnchor(content, 10.0);
        AnchorPane.setLeftAnchor(content, 10.0);

        setScene(new Scene(modalContent));
    }

    public void closeModal(T result) {
        closeHandler.accept(result);
        close();
    }
}

