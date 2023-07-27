package com.example.gambarucmsui.ui.form;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class FormBarcodeGet {

    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private TextField txtBarcodeId;
    @FXML private Label lblErrBarcodeId;

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady = false;
    private Long outBarcodeId;

    @FXML void onOk(MouseEvent event) {
        String barcodeId = txtBarcodeId.getText().trim();
        if (barcodeId.isBlank()) {
            lblErrBarcodeId.setText("Upiši barkod.");
            return;
        }
        isFormReady = true;
        outBarcodeId = parseBarcodeStr(barcodeId);
    }
    @FXML void onClose(MouseEvent event) {
        close();
    }
    @FXML
    void onTextfieldTyped(KeyEvent event) {
        lblErrBarcodeId.setText("");
    }

    public boolean isFormReady() {
        return isFormReady;
    }

    public Long getBarcodeId() {
        return outBarcodeId;
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

}
