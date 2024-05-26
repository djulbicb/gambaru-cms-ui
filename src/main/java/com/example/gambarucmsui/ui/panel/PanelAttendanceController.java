package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.SubscriptionEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceLoadForUserPort;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.subscription.AddSubscriptionPort;
import com.example.gambarucmsui.ports.interfaces.subscription.UpdateSubscriptionPort;
import com.example.gambarucmsui.ports.interfaces.user.UserLoadPort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.alert.AlertShowAttendanceController;
import com.example.gambarucmsui.ui.alert.AlertShowMembershipController;
import com.example.gambarucmsui.ui.dto.core.UserDetail;
import com.example.gambarucmsui.ui.form.FormBarcodeGetValid;
import com.example.gambarucmsui.ui.text.Labels;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.database.entity.BarcodeEntity.BARCODE_ID;
import static com.example.gambarucmsui.ui.dto.admin.SubscriptStatus.*;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.*;
import static com.example.gambarucmsui.util.PathUtil.*;

public class PanelAttendanceController implements PanelHeader {
    //  FXML
    //////////////////////////////////////////////////////////////////
    private final Stage primaryStage;
    @FXML TableView<UserDetail> table;
    @FXML Label paginationLabel;
    @FXML Button btnPayNextMonth;


    //  INIT
    //////////////////////////////////////////////////////////////////
    private final UserLoadPort userLoadPort;
    private final AttendanceAddForUserPort attendanceAddForUserPort;
    private final AttendanceLoadForUserPort attendanceLoadForUserPort;
    private final BarcodeLoadPort barcodeLoadPort;
    private final AddSubscriptionPort addSubscriptionPort;
    private final UpdateSubscriptionPort updateSubscriptionPort;

    public PanelAttendanceController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        attendanceAddForUserPort = Container.getBean(AttendanceAddForUserPort.class);
        attendanceLoadForUserPort = Container.getBean(AttendanceLoadForUserPort.class);
        userLoadPort = Container.getBean(UserLoadPort.class);
        barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
        this.addSubscriptionPort = Container.getBean(AddSubscriptionPort.class);
        this.updateSubscriptionPort = Container.getBean(UpdateSubscriptionPort.class);
    }

    @FXML
    public void initialize() {
        System.out.println("Attendance loaded");
        configureTable();

        updatePagination(LocalDate.now());
        listPageForDate();
    }

    private void configureTable() {
        table.getColumns().get(0).setCellFactory(createBarcodeCellFactory());
        stretchColumnsToEqualSize(table);

        table.setRowFactory(tv -> {
            TableRow<UserDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                UserDetail u = row.getItem();
                if (u == null) {
                    return;
                }
                String feeStr = Labels.payNextMonth(u.getMembershipFee());
                btnPayNextMonth.setText(feeStr);

            });
            return row;
        });

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
                .stream().map(o -> {
                    return UserDetail.fromEntityToFull(o.getBarcode(), paginationDate);
                }).collect(Collectors.toList());
        table.getItems().setAll(collect);
        btnPayNextMonth.setText(Labels.payNextMonth());
    }
    @FXML
    protected void addAttendanceManually() throws IOException {
        FormBarcodeGetValid controller = new FormBarcodeGetValid();

        Pane root = loadFxml(FORM_BARCODE_GET_VALID, controller);
        createStage("Upiši barkod", root, primaryStage).showAndWait();

        if (controller.isReady()) {
            BarcodeEntity barcode = barcodeLoadPort.findById(controller.getBarcodeId()).get();
            attendanceAddForUserPort.validateAndAddAttendance(barcode.getBarcodeId(), LocalDateTime.now());

            AlertShowAttendanceController alertCtrl = new AlertShowAttendanceController(barcode, LocalDate.now());
            Pane pane = loadFxml(ALERT_SHOW_ATTENDANCE, alertCtrl);
            ToastView.showModal(pane, 4000, 200);

            listPageForDate();
        }
    }

    public void onBarcodeRead(String barcodeIdStr)  {
        Long barcodeId = parseBarcodeStr(barcodeIdStr);
        ValidatorResponse res = attendanceAddForUserPort.validateAndAddAttendance(barcodeId, LocalDateTime.now());
        if (res.hasErrors()) {
            ToastView.showModal(res.getErrorOrEmpty(BARCODE_ID));
        } else {
            BarcodeEntity barcode = barcodeLoadPort.findById(parseBarcodeStr(barcodeIdStr)).get();
            AlertShowAttendanceController controller = new AlertShowAttendanceController(barcode, LocalDate.now());
            Pane pane = loadFxml(ALERT_SHOW_ATTENDANCE, controller);
            ToastView.showModal(pane, 4000, 200);
        }

        listPageForDate();
    }

    @FXML
    public void addSubscriptionForNextMonth () {
        UserDetail selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj red");
            return;
        }

        ValidatorResponse res = addSubscriptionPort.addNextMonthSubscription(selectedItem.getBarcodeId());
        if (res.isOk()) {
            ToastView.showModal(res.getMessage());
            listPageForDate();
        } else {
            ToastView.showModal(Messages.ERROR_ADDING_SUBSCRIPTION);
        }
    }
    @FXML
    public void showSubscriptionDetails () {
        UserDetail selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Selektuj red");
            return;
        }

        BarcodeEntity barcode = barcodeLoadPort.findById(parseBarcodeStr(selectedItem.getBarcodeId())).get();
        AlertShowMembershipController alertCtrl = new AlertShowMembershipController(barcode, LocalDateTime.now());
        Pane pane = loadFxml(ALERT_SHOW_MEMBERSHIP, alertCtrl);
        createStage("Članarina", pane, primaryStage).showAndWait();

        listPageForDate();
    }
}
