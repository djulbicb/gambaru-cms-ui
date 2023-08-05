package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AddUserAttendancePort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormBarcodeGetAttendance {

    // REPO
    //////////////////////////////////////////
    private final AddUserAttendancePort addAttendance;
    private final BarcodeLoadPort barcodeLoadPort;
    private final LocalDateTime timestamp;

    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private TextField txtBarcodeId;
    @FXML private Label lblErrBarcodeId;
    @FXML private Label lblResult;


    public FormBarcodeGetAttendance(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
        addAttendance = Container.getBean(AddUserAttendancePort.class);
    }

    @FXML void onOk(MouseEvent event) {
        String barcodeIdStr = getOr(txtBarcodeId, "");

        if (validate(barcodeIdStr)) {
            Long barcodeId = parseBarcodeStr(getOr(txtBarcodeId, ""));
            addAttendance.addAttendance(barcodeId, timestamp);
            close();
        }
    }

    boolean validate(String barcodeIdStr) {
        ValidatorResponse validator = addAttendance.verifyAddAttendance(barcodeIdStr);
        if (validator.hasErrors()) {
            Map<String, String> errors = validator.getErrors();
            if (errors.containsKey("barcodeId")) {
                lblErrBarcodeId.setText(errors.get("barcodeId"));
                return false;
            }
        }
        Long barcodeId = parseBarcodeStr(getOr(txtBarcodeId, ""));
        Optional<BarcodeEntity> byId = barcodeLoadPort.findById(barcodeId);
        if (byId.isEmpty()) {
            return false;
        }

        BarcodeEntity barcode = byId.get();
        UserEntity user = barcode.getUser();
        lblResult.setText(String.format("Polaznik: %s %s", user.getFirstName(), user.getLastName()));
        return true;
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
