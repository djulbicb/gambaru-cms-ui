package com.example.gambarucmsui;

import com.example.gambarucmsui.common.DelayedKeyListener;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.PathUtil.CSS;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
public class GambaruUIApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        setUpLiquibase();

        Thread.setDefaultUncaughtExceptionHandler(new GambaruUIAppGlobalExceptionHandler());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gambaru-header-switch.fxml"));
        GambaruSwitchController controller = new GambaruSwitchController(stage);
        loader.setController(controller);

        AnchorPane root = loader.load();
        Scene scene = new Scene(root, 1000, 700);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, new DelayedKeyListener() {
            @Override
            public void onFinish(String word) {
                if (isBarcodeString(word)) {
                    System.out.println(String.format("Barcode scanned: %s", word));
                    String barcodeId = cleanBarcodeStr(word);
                    controller.onBarcodeScanned(barcodeId);
                }
            }
        });

        stage.setTitle("Gambaru CMS");
        stage.setScene(scene);


        scene.getStylesheets().add(getClass().getResource(CSS).toExternalForm());



        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

    public static void setUpLiquibase() {
        try {
            System.out.println("Starting Liquibase...");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/gambaru",
                    "gambaru",
                    "password");

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase("META-INF/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts());
            connection.close();

            System.out.println("Liquibase started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}