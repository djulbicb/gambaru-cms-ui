package com.example.gambarucmsui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

public class GambaruUIAppGlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // Perform some action here, such as logging the exception
        System.err.println("Uncaught exception occurred: " + throwable.getMessage());
        System.out.println(getStackTraceAsString(throwable));;

        // Get the stack trace as a string
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTrace = sw.toString();

        // Show an alert with the exception message to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Error");
        alert.setHeaderText("Došlo je do greške.");
        alert.setContentText(throwable.getMessage());

        StackPane pane = new StackPane();
        pane.setPadding(new Insets(10));;
        Text content = new Text("Aplikacija i dalje radi. Sačuvaj grešku kao fajl i pokaži adminu");
        pane.getChildren().add(content);
        alert.getDialogPane().setContent(pane);

        ButtonType buttonYes = new ButtonType("Da");
        ButtonType buttonNo = new ButtonType("Ne");

        alert.getButtonTypes().setAll(buttonYes, buttonNo);
        alert.getDialogPane().setGraphic(null);


        // Close popup on X
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            // Handle the close button action here (if needed)
            System.out.println("Alert closed by user.");
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonYes) {
                saveStackTraceToFile(getStackTraceAsString(throwable));
            }
        }
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    private static void saveStackTraceToFile(String stackTrace) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Stack Trace");
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.print(stackTrace);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}