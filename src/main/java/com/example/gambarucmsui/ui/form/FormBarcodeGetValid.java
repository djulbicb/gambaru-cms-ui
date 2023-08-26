package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ui.panel.FxmlViewHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormBarcodeGetValid implements FxmlViewHandler {

    // REPO
    //////////////////////////////////////////
    private final BarcodeLoadPort barcodeLoadPort;

    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private TextField txtBarcodeId;
    @FXML private Label lblErrBarcodeId;
    @FXML private Label lblResult;

    // OUT
    //////////////////////////////////////////

    boolean isReady = false;
    Long outBarcodeId = 0L;
    public FormBarcodeGetValid() {
        barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
    }

    @FXML void onOk(MouseEvent event) throws IOException {
        String barcodeIdStr = getOr(txtBarcodeId, "");

        if (validate(barcodeIdStr)) {
            isReady = true;
            outBarcodeId = parseBarcodeStr(getOr(txtBarcodeId, ""));
            close();
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public Long getBarcodeId() {
        return outBarcodeId;
    }

    boolean validate(String barcodeIdStr) {
        isReady = false;
        ValidatorResponse res = barcodeLoadPort.validateBarcodeActive(barcodeIdStr);
        if (res.isOk()) {
            lblResult.setText(res.getMessage());
            return true;
        }

        lblErrBarcodeId.setText(res.getErrorOrEmpty(BARCODE_ID));
        return false;
    }

    @FXML void onClose(MouseEvent event) {
        close();
    }
    @FXML
    void onTextfieldTyped(KeyEvent event) {
        lblErrBarcodeId.setText("");
        lblResult.setText("");
        validate(getOr(txtBarcodeId, ""));
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

}
