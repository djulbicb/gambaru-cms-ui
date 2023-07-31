package com.example.gambarucmsui;

import com.example.gambarucmsui.common.DelayedKeyListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.PathUtil.CSS;


public class GambaruUIApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(new GambaruUIAppGlobalExceptionHandler());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gambaru-header-switch.fxml"));
        GambaruSwitchController controller = new GambaruSwitchController(stage);
        loader.setController(controller);

        AnchorPane root = loader.load();
        Scene scene = new Scene(root, 1000, 700);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new DelayedKeyListener() {
            @Override
            public void onFinish(String word) {
                if (isBarcode(word)) {
                    String barcodeId = cleanBarcodeStr(word);
                    System.out.println("Barcode scanned: " + barcodeId);
                    controller.onBarcodeScanned(barcodeId);
                }
            }
        });

        stage.setTitle("Hello!");
        stage.setScene(scene);


        scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());



        stage.show();
    }


    

    public static void main(String[] args) {
        launch();
    }
}