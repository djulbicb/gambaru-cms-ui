package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceLoadForUserPort;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.user.UserLoadPort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.dto.core.UserDetail;
import com.example.gambarucmsui.ui.form.FormBarcodeGetAttendance;
import com.example.gambarucmsui.util.FormatUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.formatPagination;
import static com.example.gambarucmsui.util.LayoutUtil.stretchColumnsToEqualSize;
import static com.example.gambarucmsui.util.PathUtil.FORM_BARCODE_GET_ATTENDANCE;

public class PanelAttendanceController implements PanelHeader {
    //  FXML
    //////////////////////////////////////////////////////////////////
    private final Stage primaryStage;
    @FXML
    TableView<UserDetail> table;
    @FXML
    Label paginationLabel;

    //  INIT
    //////////////////////////////////////////////////////////////////
    private final UserLoadPort barcodeLoadPort;
    private final AttendanceAddForUserPort attendanceAddForUserPort;
    private final AttendanceLoadForUserPort attendanceLoadForUserPort;

    public PanelAttendanceController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        attendanceAddForUserPort = Container.getBean(AttendanceAddForUserPort.class);
        attendanceLoadForUserPort = Container.getBean(AttendanceLoadForUserPort.class);
        barcodeLoadPort = Container.getBean(UserLoadPort.class);
    }

    @FXML
    public void initialize() {
        System.out.println("Attendance loaded");

        updatePagination(LocalDate.now());
        listPageForDate();

        stretchColumnsToEqualSize(table);
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Attendance.");
    }

    //  PAGINATION
    //////////////////////////////////////////////////////////////////
    private LocalDate paginationDate = LocalDate.now();
    private static final int PAGE_SIZE = 50;
    @FXML
    protected void goToToday() {
        updatePagination(LocalDate.now());
        listPageForDate();
    }
    @FXML
    protected void goNextPage() {
        updatePagination(paginationDate.plusDays(1));
        listPageForDate();
    }
    @FXML
    protected void goPrevPage() {
        updatePagination(paginationDate.minusDays(1));
        listPageForDate();
    }

    private void updatePagination(LocalDate localDate) {
        paginationDate = localDate;
        paginationLabel.setText(formatPagination(paginationDate));
    }

    // CODE
    ///////////////////////////////////////////////////////////////

    private void listPageForDate() {
        List<UserDetail> collect = attendanceLoadForUserPort.findAllForAttendanceDate(paginationDate)
                .stream().map(o -> UserDetail.fromEntityToFull(o.getBarcode(), FormatUtil.toDateTimeFormat(o.getTimestamp()))).collect(Collectors.toList());
        table.getItems().setAll(collect);
    }
    @FXML
    protected void addAttendanceManually() throws IOException {
        Pane root = loadFxml(FORM_BARCODE_GET_ATTENDANCE, new FormBarcodeGetAttendance(getDateTimeOfPaginationOrNow()));
        createStage("Dodaj korisnika u tim", root, primaryStage).showAndWait();
        listPageForDate();
    }

    public void onBarcodeRead(String barcodeIdStr) {
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        ValidatorResponse res = attendanceAddForUserPort.verifyAndAddAttendance(barcodeId, getDateTimeOfPaginationOrNow());
        if (res.hasErrors()) {
            ToastView.showModal(res.getErrors().get(BARCODE_ID));
        } else {
            PersonEntity user = barcodeLoadPort.findUserByBarcodeId(parseBarcodeStr(barcodeIdStr)).get();
            ToastView.showAttendance(user);
        }
        listPageForDate();
    }

    private LocalDateTime getDateTimeOfPaginationOrNow() {
        LocalDateTime now = LocalDateTime.now();
        if (paginationDate.equals(now.toLocalDate())) {
           return now;
        }
        return paginationDate.atStartOfDay();
    }
}
