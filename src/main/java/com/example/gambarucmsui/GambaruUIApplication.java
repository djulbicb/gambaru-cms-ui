package com.example.gambarucmsui;

import com.example.gambarucmsui.common.DelayedKeyListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;


public class GambaruUIApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gambaru-header-switch.fxml"));
        GambaruSwitchController controller = new GambaruSwitchController();
        loader.setController(controller);

        AnchorPane root = loader.load();
        Scene scene = new Scene(root, 1000, 700);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new DelayedKeyListener() {
            @Override
            public void onFinish(String word) {
                if (word == null || word.isBlank() ) {
                    return;
                }
                Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
                boolean matches = pattern.matcher(word).matches();
                if (!matches) {
                    return;
                }

                System.out.println("Input " + word.trim());
                controller.onBarcodeScanned(Long.parseLong(word.trim()));
            }
        });

        stage.setTitle("Hello!");
        stage.setScene(scene);


        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());



        stage.show();
    }


    

    public static void main(String[] args) {
        launch();
    }
}