package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.alert.AlertShowAttendanceController;
import com.example.gambarucmsui.ui.panel.FxmlViewHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.*;
import static com.example.gambarucmsui.util.LayoutUtil.getOr;
import static com.example.gambarucmsui.util.PathUtil.ALERT_SHOW_ATTENDANCE;

public class FormBarcodeGetAttendance implements FxmlViewHandler {

    // REPO
    //////////////////////////////////////////
    private final AttendanceAddForUserPort addAttendance;
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
        addAttendance = Container.getBean(AttendanceAddForUserPort.class);
    }

    @FXML void onOk(MouseEvent event) throws IOException {
        String barcodeIdStr = getOr(txtBarcodeId, "");

        if (validate(barcodeIdStr)) {
            Long barcodeId = parseBarcodeStr(getOr(txtBarcodeId, ""));
            addAttendance.validateAndAddAttendance(barcodeId, timestamp);
            close();

            BarcodeEntity barcode = barcodeLoadPort.findById(parseBarcodeStr(barcodeIdStr)).get();
            AlertShowAttendanceController controller = new AlertShowAttendanceController(barcode, timestamp);
            Pane pane = loadFxml(ALERT_SHOW_ATTENDANCE, controller);
            ToastView.showModal(pane, 4000, 200);
        }
    }

    boolean validate(String barcodeIdStr) {
        ValidatorResponse validator = addAttendance.validateAddAttendance(barcodeIdStr);
        if (validator.hasErrors()) {
            lblErrBarcodeId.setText(validator.getErrorOrEmpty(BARCODE_ID));
        }
        Long barcodeId = parseBarcodeStr(getOr(txtBarcodeId, ""));
        Optional<BarcodeEntity> byId = barcodeLoadPort.findById(barcodeId);
        if (byId.isEmpty()) {
            return false;
        }

        BarcodeEntity barcode = byId.get();
        if (barcode.getStatus() == BarcodeEntity.Status.NOT_USED) {
            lblResult.setText("Taj barkod nije zadat korisniku");
            return false;
        }

        if (barcode.getStatus() == BarcodeEntity.Status.DELETED) {
            lblResult.setText("Taj barkod pripada timu koji je obrisan.");
            return false;
        }

        if (barcode.getStatus() == BarcodeEntity.Status.DEACTIVATED) {
            lblResult.setText("Taj barkod je deaktiviran.");
            return false;
        }

        PersonEntity user = barcode.getPerson();
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
