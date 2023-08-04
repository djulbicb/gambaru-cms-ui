package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.UserEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.user.AddUserAttendancePort;
import com.example.gambarucmsui.ports.user.BarcodeLoadPort;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
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
        if (verify(getOr(txtBarcodeId, ""))) {
            Long barcodeId = parseBarcodeStr(getOr(txtBarcodeId, ""));
            addAttendance.addAttendance(barcodeId, timestamp);
            close();
        }
    }

    @FXML void onClose(MouseEvent event) {
        close();
    }
    @FXML
    void onTextfieldTyped(KeyEvent event) {
        lblErrBarcodeId.setText("");
        lblResult.setText("");
        verify(getOr(txtBarcodeId, ""));
    }

    private boolean verify(String barcodeIdStr) {
        if (!isLong(barcodeIdStr)) {
            if (barcodeIdStr.isBlank()) {
                return false;
            }
            lblErrBarcodeId.setText("Upiši barkod npr 123.");
            return false;
        }

        Long barcode = parseBarcodeStr(barcodeIdStr);
        Optional<BarcodeEntity> barcodeEntityOptional = barcodeLoadPort.findById(barcode);
        if (barcodeEntityOptional.isEmpty()) {
            lblErrBarcodeId.setText("Taj barkod nije registrovan u bazi.");
            return false;
        }

        BarcodeEntity b = barcodeEntityOptional.get();
        if (b.getStatus() != BarcodeEntity.Status.ASSIGNED)  {
            lblErrBarcodeId.setText("Taj barkod postoji ali nije u upotrebi. Probaj drugi drugi.");
            return false;
        }

        UserEntity user = b.getUser();
        lblResult.setText(String.format("Korisnik nađen: %s %s.", user.getFirstName(), user.getLastName()));
        return true;
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

}
