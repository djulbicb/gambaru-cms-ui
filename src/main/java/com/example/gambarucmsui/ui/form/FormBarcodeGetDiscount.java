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
import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormBarcodeGetDiscount implements FxmlViewHandler {

    // REPO
    //////////////////////////////////////////
    private final BarcodeLoadPort barcodeLoadPort;

    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private TextField txtDiscount;
    @FXML private Label lblErrDiscount;

    // OUT
    //////////////////////////////////////////

    boolean isReady = false;
    int outDiscount = 0;
    public FormBarcodeGetDiscount() {
        barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
    }

    @FXML void onOk(MouseEvent event) throws IOException {
        String discountStr = getOr(txtDiscount, "");

        if (validate(discountStr)) {
            isReady = true;
            outDiscount = Integer.parseInt(discountStr);
            close();
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public int getDiscount() {
        return outDiscount;
    }

    boolean validate(String discount) {
        isReady = false;
        if (isLong(discount)) {
            Long value = Long.parseLong(discount);

            if (value < 0 || value > 100000) {
                lblErrDiscount.setText("Cena je van očekivanog raspona.");
                return false;
            }

            return true;
        }
        lblErrDiscount.setText("Upiši celu brojčanu vrednost npr 100 ili 200.");
        return false;
    }

    @FXML void onClose(MouseEvent event) {
        close();
    }
    @FXML
    void onTextfieldTyped(KeyEvent event) {
        lblErrDiscount.setText("");
        validate(getOr(txtDiscount, ""));
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

}
