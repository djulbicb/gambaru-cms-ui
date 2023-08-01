package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.ui.form.FormUserAddController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.gambarucmsui.util.PathUtil.CSS;

public interface PanelHeader extends FxmlViewHandler {
    void initialize();
    void viewSwitched();
}
